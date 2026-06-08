package com.example.neurozen_front.neurozen.home.presentation.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _player = ExoPlayer.Builder(context).build()
    val player: Player = _player

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        _player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    fun playAudio(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        _player.setMediaItem(mediaItem)
        _player.prepare()
        _player.play()
    }

    fun togglePlayPause() {
        if (_player.isPlaying) {
            _player.pause()
        } else {
            _player.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        _player.release()
    }
}
