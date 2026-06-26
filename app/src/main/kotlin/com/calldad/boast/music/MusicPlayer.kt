package com.calldad.boast.music

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayer(private val context: Context) {
    
    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentMusicIndex = MutableStateFlow(0)
    val currentMusicIndex: StateFlow<Int> = _currentMusicIndex.asStateFlow()
    
    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()
    
    private val musicRepository = MusicRepository()
    
    @OptIn(UnstableApi::class)
    fun initialize() {
        if (exoPlayer == null) {
            // 移动设备优化：降低缓冲大小以减少内存占用
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    15000,  // 最小缓冲 15 秒
                    50000,  // 最大缓冲 50 秒（降低默认值）
                    1500,   // 缓冲播放 1.5 秒
                    5000    // 缓冲重播 5 秒
                )
                .build()
            
            exoPlayer = ExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .build()
                .apply {
                    setHandleAudioBecomingNoisy(true)
                    // 设置循环播放模式
                    repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
                }
            
            Log.d("MusicPlayer", "播放器初始化完成")
        }
    }
    
    fun playMusic(index: Int) {
        exoPlayer?.let { player ->
            val musicList = musicRepository.getMusicList()
            if (index in musicList.indices) {
                _currentMusicIndex.value = index
                val music = musicList[index]
                val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/${music.resourceId}")
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                _isPlaying.value = true
            }
        }
    }
    
    fun pauseMusic() {
        exoPlayer?.pause()
        _isPlaying.value = false
    }
    
    fun resumeMusic() {
        exoPlayer?.play()
        _isPlaying.value = true
    }
    
    fun stopMusic() {
        exoPlayer?.stop()
        _isPlaying.value = false
    }
    
    fun setVolume(volume: Float) {
        _volume.value = volume
        exoPlayer?.volume = volume
    }
    
    fun nextMusic() {
        val currentIndex = _currentMusicIndex.value
        val nextIndex = (currentIndex + 1) % musicRepository.getMusicCount()
        playMusic(nextIndex)
    }
    
    fun previousMusic() {
        val currentIndex = _currentMusicIndex.value
        val previousIndex = if (currentIndex == 0) {
            musicRepository.getMusicCount() - 1
        } else {
            currentIndex - 1
        }
        playMusic(previousIndex)
    }
    
    fun getCurrentMusicName(): String {
        val musicList = musicRepository.getMusicList()
        return musicList.getOrNull(_currentMusicIndex.value)?.name ?: "未知音乐"
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
    }
}