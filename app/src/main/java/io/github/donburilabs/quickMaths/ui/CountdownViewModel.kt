package io.github.donburilabs.quickMaths.ui

import androidx.lifecycle.ViewModel
import io.github.donburilabs.quickMaths.data.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountdownViewModel @Inject constructor(
    private val soundManager: SoundManager,
) : ViewModel() {
    fun onCountShown() {
        soundManager.playTick()
    }
}
