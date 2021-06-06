package com.example.reptimer.sound

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.reptimer.R
import kotlin.random.Random


class RepMediaPlayer(private val context: Context) : RepSoundPlayer {
    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }

    override fun playRepSound() {
        val assetFileDescriptor = context.resources.openRawResourceFd(getRandomSoundbite()) ?: return
        mediaPlayer.run {
            reset()
            setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
            prepareAsync()
        }
    }

    private fun getRandomSoundbite():Int{
        val index = Random.nextInt(soundClips.size)
        return soundClips[index]
    }

    private val soundClips = arrayOf(R.raw.stay_hard_1,
                                     R.raw.stay_hard_2,
                                     R.raw.stay_hard_2,

                                    )


}