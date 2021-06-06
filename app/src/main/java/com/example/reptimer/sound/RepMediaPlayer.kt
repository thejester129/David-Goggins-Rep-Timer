package com.example.reptimer.sound

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.reptimer.R


class RepMediaPlayer(private val context: Context) : RepSoundPlayer {
    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }

    override fun playRepSound() {
        val assetFileDescriptor = context.resources.openRawResourceFd(R.raw.stay_hard_1) ?: return
        mediaPlayer.run {
            reset()
            setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
            prepareAsync()
        }
    }

}