package com.dangerfield.features.inAppMessaging.internal.update

import com.dangerfield.features.inAppMessaging.UpdateStatus
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.dangerfield.libraries.ui.components.text.Text
import oddoneout.core.Catching

@Composable
fun DownloadProgressBar(updateStatus: UpdateStatus?) {
    AnimatedVisibility(
        visible = updateStatus != null && updateStatus is UpdateStatus.Downloading,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        val status = updateStatus as? UpdateStatus.Downloading ?: return@AnimatedVisibility

        val actualProgress = remember(status.bytesDownloaded, status.totalBytesToDownload) {
            if (status.totalBytesToDownload > 0) {
                Catching { status.bytesDownloaded.toFloat() / status.totalBytesToDownload.toFloat() }.getOrElse { 0f }
            } else 0f
        }

        val showFakeProgress = actualProgress == 0f

        // Animate fake progress to 50%
        val fakeProgress by animateFloatAsState(
            targetValue = if (showFakeProgress) 0.5f else actualProgress,
            animationSpec = tween(durationMillis = 1500)
        )

        val displayProgress =
            if (actualProgress > 0f) (0.5f + (actualProgress / 2)) else fakeProgress

        ProgressRow(progress = displayProgress) {
            Text(text = "Downloading Update...")
        }
    }
}