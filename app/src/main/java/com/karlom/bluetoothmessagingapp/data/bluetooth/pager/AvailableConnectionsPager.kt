package com.karlom.bluetoothmessagingapp.data.bluetooth.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.models.foldToString
import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection

class AvailableConnectionsPager(
    private val source: suspend () -> Either<Failure, List<Connection>>,
) : PagingSource<Int, Connection>() {

    private companion object {
        const val FIRST_PAGE = 1
    }

    private var connections: List<Connection>? = null

    override fun getRefreshKey(state: PagingState<Int, Connection>) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Connection> {
        if (connections == null) {
            source().fold(
                { failure -> return LoadResult.Error(Throwable(failure.foldToString())) },
                { content -> connections = content })
        }
        val page = params.key ?: FIRST_PAGE
        val pageItems = if (isLastPage(page, params.loadSize)) {
            connections?.subList(
                params.loadSize * (page - 1),
                connections?.size ?: 0,
            ) ?: listOf()
        } else if (pageDosentExist(page, params.loadSize)) {
            listOf()
        } else {
            connections?.subList(
                params.loadSize * (page - 1),
                connections?.lastIndex ?: 0,
            ) ?: listOf()
        }
        return LoadResult.Page(
            data = pageItems,
            prevKey = if (page == FIRST_PAGE) null else page.minus(1),
            nextKey = if (isLastPage(page, params.loadSize)) page.plus(1) else null,
        )
    }

    private fun isLastPage(page: Int, perPage: Int) =
        (page - 1) * perPage <= (connections?.lastIndex ?: 0) && (connections?.lastIndex
            ?: 0) < page * perPage

    private fun pageDosentExist(page: Int, perPage: Int) =
        page * perPage > (connections?.lastIndex ?: 0)
}
