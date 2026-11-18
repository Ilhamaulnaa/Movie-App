package com.ilham.domain.usecase

import com.ilham.common.DataResult
import com.ilham.common.map
import com.ilham.domain.model.Video
import com.ilham.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieTrailerUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): DataResult<Video?> =
        repository.getMovieVideos(movieId).map(::selectTrailer)

    private fun selectTrailer(videos: List<Video>): Video? {
        val youtubeVideos = videos.filter {
            it.site.equals(SITE_YOUTUBE, ignoreCase = true) && it.key.isNotBlank()
        }
        return youtubeVideos.firstOrNull { it.type == TYPE_TRAILER && it.isOfficial }
            ?: youtubeVideos.firstOrNull { it.type == TYPE_TRAILER }
            ?: youtubeVideos.firstOrNull { it.type == TYPE_TEASER }
            ?: youtubeVideos.firstOrNull()
    }

    private companion object {
        const val SITE_YOUTUBE = "YouTube"
        const val TYPE_TRAILER = "Trailer"
        const val TYPE_TEASER = "Teaser"
    }
}