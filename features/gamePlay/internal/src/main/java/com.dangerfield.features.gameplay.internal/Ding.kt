package com.dangerfield.features.gameplay.internal

import android.content.Context
import android.media.MediaPlayer
import com.dangerfield.oddoneoout.features.gameplay.internal.R
import oddoneout.core.Try
import oddoneout.core.logOnError

fun playDingSound(context: Context) {
    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.timer_done_sound)
    mediaPlayer.setOnCompletionListener { mp -> mp.release() }
    Try {  mediaPlayer.start() }
        .logOnError()
}
