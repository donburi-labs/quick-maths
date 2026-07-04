package com.jaidensiu.quickMaths.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.jaidensiu.quickMaths.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.media3.common.AudioAttributes as Media3AudioAttributes

@Singleton
class SoundManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSampleIds = mutableSetOf<Int>()

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSampleIds.add(sampleId)
            } else {
                Log.w(TAG, "Failed to load sample $sampleId (status=$status)")
            }
        }
    }

    private val correctId = soundPool.load(context, R.raw.sfx_correct, 1)
    private val tickId = soundPool.load(context, R.raw.sfx_tick, 1)
    private val pencilId = soundPool.load(context, R.raw.sfx_pencil_scratch, 1)

    private var pencilStreamId = 0
    private var pencilStartedAtMs = 0L
    private var musicPlayer: ExoPlayer? = null

    fun playCorrect() {
        playOnce(sampleId = correctId)
    }

    fun playTick() {
        playOnce(sampleId = tickId)
    }

    /**
     * Start on touch-down so SoundPool's play() startup latency isn't heard on the
     * first movement; movement then only adjusts volume, which is immediate.
     */
    fun startPencil() {
        if (pencilStreamId != 0 || pencilId !in loadedSampleIds) {
            return
        }
        playPencilStream(volume = PENCIL_MIN_VOLUME, rate = 1f)
    }

    fun updatePencilSpeed(speedPxPerMs: Float) {
        if (pencilStreamId == 0) {
            return
        }
        val intensity = (speedPxPerMs / PENCIL_FULL_SPEED_PX_PER_MS).coerceIn(0f, 1f)
        val volume = PENCIL_MIN_VOLUME + (PENCIL_MAX_VOLUME - PENCIL_MIN_VOLUME) * intensity
        val rate = 0.9f + 0.2f * intensity
        if (SystemClock.uptimeMillis() - pencilStartedAtMs >= PENCIL_RETRIGGER_MS) {
            // The one-shot is about to run out; overlap a fresh scratch with the old
            // one's fade-out so continuous drawing sounds unbroken.
            playPencilStream(volume = volume, rate = rate)
        } else {
            soundPool.setVolume(pencilStreamId, volume, volume)
            soundPool.setRate(pencilStreamId, rate)
        }
    }

    private fun playPencilStream(volume: Float, rate: Float) {
        pencilStreamId = soundPool.play(pencilId, volume, volume, 1, NO_LOOP, rate)
        pencilStartedAtMs = SystemClock.uptimeMillis()
    }

    /** Silence without stopping, so resuming mid-stroke has no restart latency. */
    fun mutePencil() {
        if (pencilStreamId == 0) {
            return
        }
        soundPool.setVolume(pencilStreamId, 0f, 0f)
    }

    fun stopPencil() {
        if (pencilStreamId == 0) {
            return
        }
        soundPool.stop(pencilStreamId)
        pencilStreamId = 0
    }

    fun startMusic() {
        val player = musicPlayer ?: createMusicPlayer().also { musicPlayer = it }
        player.play()
    }

    fun pauseMusic() {
        musicPlayer?.pause()
    }

    private fun createMusicPlayer(): ExoPlayer =
        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                Media3AudioAttributes.Builder()
                    .setUsage(C.USAGE_GAME)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.w(TAG, "Music playback error", error)
                }
            })
            setMediaItem(
                MediaItem.fromUri(
                    "android.resource://${context.packageName}/${R.raw.music_game}".toUri()
                )
            )
            repeatMode = Player.REPEAT_MODE_ONE
            volume = MUSIC_VOLUME
            prepare()
        }

    private fun playOnce(sampleId: Int) {
        if (sampleId in loadedSampleIds) {
            soundPool.play(sampleId, SFX_VOLUME, SFX_VOLUME, 1, 0, 1f)
        }
    }

    private companion object {
        const val TAG = "SoundManager"
        const val MAX_STREAMS = 4
        const val NO_LOOP = 0
        const val SFX_VOLUME = 1f
        const val MUSIC_VOLUME = 0.35f
        const val PENCIL_MIN_VOLUME = 0.15f
        const val PENCIL_MAX_VOLUME = 0.9f
        const val PENCIL_FULL_SPEED_PX_PER_MS = 1.5f

        // The scratch clip is 550 ms with an 80 ms fade-out; retriggering here lets
        // the new scratch overlap the old one's tail instead of leaving a gap.
        const val PENCIL_RETRIGGER_MS = 450L
    }
}
