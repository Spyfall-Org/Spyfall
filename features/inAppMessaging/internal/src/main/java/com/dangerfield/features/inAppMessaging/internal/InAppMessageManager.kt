package com.dangerfield.features.inAppMessaging.internal

import com.dangerfield.features.inAppMessaging.InAppMessagePriority
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.navigation.Router
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import oddoneout.core.Try
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoInitialize
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

/*
GIVEN no messages shown yet, WHEN low p message queued, THEN message is shown after spacer delay

GIVEN no messages shown yet, WHEN high p message queued, THEN message is shown with no delay

GIVEN low p retryable message waiting, WHEN high p message queued, THEN low p message is cancelled, high p message is shown, and low p is restarted

GIVEN low p non retryable message waiting, WHEN high p message queued, THEN low p message is cancelled forever, high p message is shown

GIVEN multiple low p messages queued, THEN messages are shown in order with spacer delay between each

GIVEN multiple high p messages queued, THEN all are shown with no delays or cancellations

 */
@Singleton
@AutoInitialize
class InAppMessageManager @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val router: Router,
    private val clock: Clock
) {
    private val messageQueue = Channel<InAppMessage>(Channel.RENDEZVOUS)
    private var lowPriorityMessageJob: Pair<InAppMessage, Job>? = null
    private var highPriorityMessageJob: Pair<InAppMessage, Job>? = null

    @Volatile
    private var lastMessageShownTime = clock.instant()

    init {
        processLowPriorityMessages()
    }

    /**
     * Queues a message to be displayed
     * @param spamSpacer the minimum spacing between this message and the message before it
     * @param openMessage the function to be called when the message is ready to be opened
     * @param cancelIfInterrupted if true, this message will not retry if it is interrupted by a higher priority message
     * @param priority the priority of the message
     * @param tag a tag to identify the message
     */
    fun queueMessage(
        spamSpacer: Duration,
        openMessage: (Router) -> Unit,
        cancelIfInterrupted: Boolean,
        priority: InAppMessagePriority,
        tag: String
    ) {
        val message = InAppMessage(
            spamSpacer,
            openMessage,
            cancelIfInterrupted,
            priority,
            tag
        )
        if (priority == InAppMessagePriority.Critical) {
            applicationScope.launch {
                displayHighPriorityMessage(message)
            }
        } else {
            applicationScope.launch {
                // suspends until collector is ready to receive
                messageQueue.send(message)
            }
        }
    }

    /*
    For each message we continually to try to display it until it is successful or cancelled
    suspending until we known
     */
    private fun processLowPriorityMessages() {
        applicationScope.launch {
            for (message in messageQueue) {
                var shouldTryToDisplay = true
                while (shouldTryToDisplay && isActive) {
                    Try {
                        displayLowPriorityMessage(message)
                    }
                        .onFailure {
                            if (it is CancellationException || highPriorityMessageJob != null) {
                                // wait for high priority message to finish
                                highPriorityMessageJob?.second?.join()
                                // mark weather or not we should try again
                                shouldTryToDisplay = !message.cancelIfInterrupted
                            }
                        }.onSuccess {
                            shouldTryToDisplay = false
                        }
                }
            }
        }
    }

    private fun displayMessage(message: InAppMessage): Pair<InAppMessage, Job> {
        return message to applicationScope.launch {
            val millisSinceLastMessage =
                clock.instant().toEpochMilli() - lastMessageShownTime.toEpochMilli()

            if (millisSinceLastMessage < message.spamSpacer.inWholeMilliseconds) {
                delay(message.spamSpacer.inWholeMilliseconds - millisSinceLastMessage)
            }

            if (isActive) {
                Try {
                    message.openMessage.invoke(router)
                }
                    .onSuccess {
                        Timber.d("Message displayed: ${message.tag}")
                        synchronized(this) {
                            lastMessageShownTime = clock.instant()
                        }
                    }
                    .onFailure {
                        Timber.e(it, "Failed to display in app message: ${message.tag}")
                    }
            }
        }
    }

    private suspend fun displayLowPriorityMessage(message: InAppMessage) {
        val messageJob = displayMessage(message)
        lowPriorityMessageJob = messageJob
        messageJob.second.join()
    }

    private suspend fun displayHighPriorityMessage(message: InAppMessage) {
        val didInterrupt = Try { lowPriorityMessageJob?.second?.cancelAndJoin() }
            .logOnFailure()
            .isSuccess

        Timber.d("Critical message being displayed. Did interrupt $didInterrupt")

        val messageJob = displayMessage(message)
        highPriorityMessageJob = messageJob
        messageJob.second.join()
    }
}
