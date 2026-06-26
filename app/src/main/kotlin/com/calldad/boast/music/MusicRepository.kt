package com.calldad.boast.music

import com.calldad.boast.R

data class MusicInfo(
    val id: Int,
    val name: String,
    val resourceId: Int
)

class MusicRepository {
    
    companion object {
        val MUSIC_LIST = listOf(
            MusicInfo(0, "默认音乐", R.raw.background_music),
            MusicInfo(1, "圣诞快乐，劳伦斯先生", R.raw.music_merry_christmas),
            MusicInfo(2, "千与千寻 (钢琴版)", R.raw.music_spirited_away),
            MusicInfo(3, "故乡的原风景", R.raw.music_hometown)
        )
    }
    
    fun getMusicList(): List<MusicInfo> = MUSIC_LIST
    
    fun getMusicById(id: Int): MusicInfo? = MUSIC_LIST.find { it.id == id }
    
    fun getMusicCount(): Int = MUSIC_LIST.size
}