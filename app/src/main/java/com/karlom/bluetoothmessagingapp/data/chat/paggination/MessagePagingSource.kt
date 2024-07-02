package com.karlom.bluetoothmessagingapp.data.chat.paggination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import java.io.IOException

class MessagePagingSource(
    private val getMessagesDesc: suspend (offset: Int, items: Int) -> Either<ErrorMessage, List<MessageEntity>>,
) : PagingSource<Int, MessageEntity>() {

    override fun getRefreshKey(state: PagingState<Int, MessageEntity>) = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageEntity> {
        val offset = params.key ?: 0
        return getMessagesDesc(offset, params.loadSize)
            .fold(
                { error -> LoadResult.Error(IOException(error.errorMessage)) },
                { data ->
                    val prevKey = if (offset == 0) null else offset - params.loadSize
                    val nextKey = if (data.isNotEmpty() && data.size == params.loadSize) offset + params.loadSize else null
                    LoadResult.Page(
                        data = data,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
            )
    }
}
