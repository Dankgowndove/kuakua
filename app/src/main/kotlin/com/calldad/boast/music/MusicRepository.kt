package com.calldad.boast.music

import com.calldad.boast.R

data class MusicInfo(
    val id: Int,
    val name: String,
    val resourceId: Int
)

class MusicRepository {
    
    companion object {
        /**
         * 音乐列表
         *
         * 当前状态：所有音乐项使用相同的资源文件 (R.raw.background_music)
         * 原因：为了提供多种音乐风格供用户选择，但资源文件需要单独添加
         *
         * 如需实现真正的多音乐切换，请按以下步骤操作：
         * 1. 在 app/src/main/res/raw 目录下添加以下音频文件：
         *    - music_light.mp3 (轻快旋律)
         *    - music_calm.mp3 (宁静时光)
         *    - music_warm.mp3 (温暖阳光)
         * 2. 将下方的资源引用从 R.raw.background_music 改为对应的文件名
         */
        val MUSIC_LIST = listOf(
            MusicInfo(0, "默认音乐", R.raw.background_music),
            MusicInfo(1, "轻快旋律", R.raw.background_music),
            MusicInfo(2, "宁静时光", R.raw.background_music),
            MusicInfo(3, "温暖阳光", R.raw.background_music)
        )
    }
    
    fun getMusicList(): List<MusicInfo> = MUSIC_LIST
    
    fun getMusicById(id: Int): MusicInfo? = MUSIC_LIST.find { it.id == id }
    
    fun getMusicCount(): Int = MUSIC_LIST.size
}