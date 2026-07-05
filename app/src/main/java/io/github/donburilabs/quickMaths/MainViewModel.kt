package io.github.donburilabs.quickMaths

import androidx.lifecycle.ViewModel
import io.github.donburilabs.quickMaths.data.SoundManager
import io.github.donburilabs.quickMaths.data.ThemeRepository
import io.github.donburilabs.quickMaths.domain.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    themeRepository: ThemeRepository,
    private val soundManager: SoundManager,
) : ViewModel() {
    val theme: StateFlow<ThemePreference> = themeRepository.theme

    private var isAppForegrounded = false
    private var isMusicAllowed = true

    fun onAppForegrounded() {
        isAppForegrounded = true
        updateMusic()
    }

    fun onAppBackgrounded() {
        isAppForegrounded = false
        updateMusic()
        soundManager.stopPencil()
    }

    fun onMusicAllowedChanged(allowed: Boolean) {
        isMusicAllowed = allowed
        updateMusic()
    }

    private fun updateMusic() {
        if (isAppForegrounded && isMusicAllowed) {
            soundManager.startMusic()
        } else {
            soundManager.pauseMusic()
        }
    }
}
