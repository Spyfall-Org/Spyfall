package com.dangerfield.features.inAppMessaging.internal

import android.app.Activity
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.features.inAppMessaging.internal.update.InAppUpdateMessage
import com.dangerfield.features.inAppMessaging.internal.update.StartInAppUpdateImpl
import com.dangerfield.libraries.test.CoroutinesTestRule
import com.dangerfield.libraries.test.FakeDataStore
import com.dangerfield.libraries.test.ResetDataStoreRule
import com.dangerfield.libraries.test.containsItem
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class StartInAppUpdateImplTest {

    private val fakeInAppUpdateManager = spyk<FakeInAppUpdateManager>()

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @get:Rule
    val fakeInAppUpdateManagerRule = FakeInAppUpdateManagerRule(fakeInAppUpdateManager)

    private lateinit var activity: Activity
    private val inAppMessagesDataStore = FakeDataStore(emptyList<InAppUpdateMessage>())
    private val clock: Clock = Clock.fixed(Instant.parse("2023-07-11T02:38:00Z"), ZoneOffset.UTC)
    private lateinit var startInAppUpdateImpl: StartInAppUpdateImpl

    @get:Rule
    val resetDataStoreRule = ResetDataStoreRule(inAppMessagesDataStore)

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java)
            .create()
            .resume()
            .get()

        startInAppUpdateImpl = StartInAppUpdateImpl(
            fakeInAppUpdateManager,
            inAppMessagesDataStore,
            coroutineTestRule.testScope.backgroundScope,
            clock
        )
    }

    @Test
    fun `GIVEN no update available, WHEN starting, THEN emit NoUpdateAvailable & no listeners`() =
        coroutineTestRule.test {
            fakeInAppUpdateManager.setAppUpdateInfo(updateAvailable = false)

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = false,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.NoUpdateAvailable>()
                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN flexible update available, WHEN starting, THEN only flexible sheet should show`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 2 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = false,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()

                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isTrue()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN immediate update available, WHEN starting, THEN only immediate sheet should show`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()

                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isFalse()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update not immediate, WHEN foreground requested, THEN emit Invalid`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 0
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.InvalidUpdateRequest>()

                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isFalse()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update immediate, WHEN flexible requested, THEN invalid request should emit`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = false,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.InvalidUpdateRequest>()

                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isFalse()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN request foreground update, WHEN immediate not allowed, THEN emit update not allowed`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                allowedUpdateTypes = listOf(AppUpdateType.FLEXIBLE),
                updatePriority = 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.UpdateNotAllowed>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)
                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isFalse()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN request background update, WHEN flexible not allowed, THEN emit update not allowed`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                allowedUpdateTypes = listOf(AppUpdateType.IMMEDIATE),
                updatePriority = 3
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = false,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.UpdateNotAllowed>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)
                assertThat(fakeInAppUpdateManager.isShowingFlexibleUpdateMessage()).isFalse()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN showing update message, WHEN user accepts THEN message should be added to seen list after action`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                versionCode = 23,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isFalse()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN showing update message, WHEN user rejects THEN message should be added to seen list after action`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                versionCode = 55,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isFalse()

                fakeInAppUpdateManager.userRejectsUpdate()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update allowed, WHEN user accepts update, THEN emit pending`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update allowed, WHEN update starts, THEN add update message seen`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update does not reauire asset deletion WHEN updating THEN should update without asset deletion`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                requireAssetDeletion = false,
                updatePriority = 5 // immediate is only 5,
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()

                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                verify {
                    fakeInAppUpdateManager.startUpdateFlow(
                        appUpdateInfo,
                        activity,
                        withArg { appUpdateOptions ->
                            assertThat(appUpdateOptions.allowAssetPackDeletion()).isFalse()
                        }
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update requires asset deletion WHEN updating THEN should update with asset deletion`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                requireAssetDeletion = true,
                updatePriority = 5 // immediate is only 5,
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()

                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                verify {
                    fakeInAppUpdateManager.startUpdateFlow(
                        appUpdateInfo,
                        activity,
                        withArg { appUpdateOptions ->
                            assertThat(appUpdateOptions.allowAssetPackDeletion()).isTrue()
                        }
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update starts, WHEN update fails, THEN remove message seen and listener`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(1)

                fakeInAppUpdateManager.updateFails()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.Failed>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update starts, WHEN cancelled, THEN emit cancel & remove listener but keep seen message in list`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(1)

                fakeInAppUpdateManager.updateCancelled()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.Canceled>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update starts, WHEN progress is made, THEN emit progress`() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                totalBytes = 100,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                fakeInAppUpdateManager.updateStarts()

                assertThat(awaitItem())
                    .isInstanceOf<UpdateStatus.Downloading>()
                    .prop(UpdateStatus.Downloading::bytesDownloaded)
                    .isEqualTo(0)

                fakeInAppUpdateManager.updateProgresses(50)

                assertThat(awaitItem())
                    .isInstanceOf<UpdateStatus.Downloading>()
                    .prop(UpdateStatus.Downloading::bytesDownloaded)
                    .isEqualTo(50)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update progresses, WHEN progress matches total, THEN emit downloaded`() =
        coroutineTestRule.test {

            val totalBytes = 100L

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                totalBytes = totalBytes,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                fakeInAppUpdateManager.updateStarts()

                assertThat(awaitItem())
                    .isInstanceOf<UpdateStatus.Downloading>()
                    .prop(UpdateStatus.Downloading::bytesDownloaded)
                    .isEqualTo(0)

                fakeInAppUpdateManager.updateProgresses(totalBytes)

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.Downloaded>()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update starts, WHEN download finishes, THEN listener should be dropped`() =
        coroutineTestRule.test {

            val totalBytes = 100L

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                totalBytes = totalBytes,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                fakeInAppUpdateManager.updateStarts()

                assertThat(awaitItem())
                    .isInstanceOf<UpdateStatus.Downloading>()
                    .prop(UpdateStatus.Downloading::bytesDownloaded)
                    .isEqualTo(0)

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(1)

                fakeInAppUpdateManager.updateProgresses(totalBytes)

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.Downloaded>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(0)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN update starts, WHEN unknown state occurs, THEN emit unknown & keep listeners `() =
        coroutineTestRule.test {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5 // immediate is only 5
            )

            val appUpdateInfo = fakeInAppUpdateManager.fakeAppUpdateInfo()!!

            startInAppUpdateImpl(
                appUpdateInfo = appUpdateInfo,
                isForegroundUpdate = true,
                activity = activity
            ).test {
                assertThat(awaitItem()).isInstanceOf<UpdateStatus.WaitingUserAction>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isTrue()

                fakeInAppUpdateManager.userAcceptsUpdate()

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.PendingDownload>()
                assertThat(fakeInAppUpdateManager.isShowingImmediateUpdateMessage()).isFalse()

                assertThat(
                    inAppMessagesDataStore.containsItem {
                        it.versionCode == appUpdateInfo.availableVersionCode()
                    }
                ).isTrue()

                fakeInAppUpdateManager.updateStarts()

                assertThat(awaitItem())
                    .isInstanceOf<UpdateStatus.Downloading>()
                    .prop(UpdateStatus.Downloading::bytesDownloaded)
                    .isEqualTo(0)

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(1)

                fakeInAppUpdateManager.updateState(installStatus = InstallStatus.UNKNOWN)

                assertThat(awaitItem()).isInstanceOf<UpdateStatus.Unknown>()

                assertThat(fakeInAppUpdateManager.numberOfListeners).isEqualTo(1)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
