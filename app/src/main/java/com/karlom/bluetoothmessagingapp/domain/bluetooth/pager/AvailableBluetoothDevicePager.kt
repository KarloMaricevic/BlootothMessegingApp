package com.karlom.bluetoothmessagingapp.domain.bluetooth.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.models.foldToString
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice

class AvailableBluetoothDevicePager(
    private val source: suspend () -> Either<Failure, List<BluetoothDevice>>,
) : PagingSource<Int, BluetoothDevice>() {

    private companion object {
        const val FIRST_PAGE = 1
    }

    private var bluetoothDevices: List<BluetoothDevice>? = null

    override fun getRefreshKey(state: PagingState<Int, BluetoothDevice>) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BluetoothDevice> {
        if (bluetoothDevices == null) {
            source().fold(
                { failure -> return LoadResult.Error(Throwable(failure.foldToString())) },
                { content -> bluetoothDevices = content })
        }
        val page = params.key ?: FIRST_PAGE
        val pageItems = if (isLastPage(page, params.loadSize)) {
            bluetoothDevices?.subList(
                params.loadSize * (page - 1),
                bluetoothDevices?.size ?: 0,
            ) ?: listOf()
        } else if (pageDosentExist(page, params.loadSize)) {
            listOf()
        } else {
            bluetoothDevices?.subList(
                params.loadSize * (page - 1),
                bluetoothDevices?.lastIndex ?: 0,
            ) ?: listOf()
        }
        return LoadResult.Page(
            data = pageItems,
            prevKey = if (page == FIRST_PAGE) null else page.minus(1),
            nextKey = if (isLastPage(page, params.loadSize)) page.plus(1) else null,
        )
    }

    private fun isLastPage(page: Int, perPage: Int) =
        (page - 1) * perPage <= (bluetoothDevices?.lastIndex ?: 0) && (bluetoothDevices?.lastIndex
            ?: 0) < page * perPage

    private fun pageDosentExist(page: Int, perPage: Int) =
        page * perPage > (bluetoothDevices?.lastIndex ?: 0)
}
