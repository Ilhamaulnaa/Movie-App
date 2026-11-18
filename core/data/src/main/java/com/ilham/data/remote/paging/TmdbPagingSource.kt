package com.ilham.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilham.data.remote.dto.PagedResponse
import com.ilham.data.util.toAppError
import kotlin.coroutines.cancellation.CancellationException

class TmdbPagingSource<Dto : Any, Model : Any>(
    private val fetchPage: suspend (page: Int) -> PagedResponse<Dto>,
    private val mapper: (Dto) -> Model,
) : PagingSource<Int, Model>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Model> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val response = fetchPage(page)
            val lastPage = minOf(response.totalPages, MAX_PAGE)
            LoadResult.Page(
                data = response.results.map(mapper),
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (page >= lastPage || response.results.isEmpty()) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e.toAppError())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Model>): Int? = state.anchorPosition?.let { anchor ->
        val closestPage = state.closestPageToPosition(anchor)
        closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
    }

    companion object {
        const val STARTING_PAGE = 1
        const val MAX_PAGE = 500
    }
}