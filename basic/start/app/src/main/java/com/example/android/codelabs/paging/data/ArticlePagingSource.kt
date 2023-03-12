package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.max

private const val STARTING_PAGE_INDEX = 0
private val firstItemCreatedTime = LocalDateTime.now()
private const val LOAD_DELAY = 3000L

class ArticlePagingSource : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = article.id - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val start = params.key ?: STARTING_PAGE_INDEX
        val range = start.until(start + params.loadSize)

        if (start != STARTING_PAGE_INDEX) delay(LOAD_DELAY)

        return LoadResult.Page(
            data = range.map { number ->
                Article(
                    id = number,
                    title = "Article $number",
                    description = "This describes article $number",
                    created = firstItemCreatedTime.minusDays(number.toLong())
                )
            },
            prevKey = when (start) {
                STARTING_PAGE_INDEX -> null
                else -> ensureValidKey(range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )
    }

    private fun ensureValidKey(key: Int) = max(STARTING_PAGE_INDEX, key)
}
