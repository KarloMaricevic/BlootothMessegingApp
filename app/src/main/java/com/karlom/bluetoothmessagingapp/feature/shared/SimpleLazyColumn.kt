package com.karlom.bluetoothmessagingapp.feature.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue

@Composable
fun <T : Any> SimpleLazyColumn(
    items: LazyPagingItems<T>,
    key: T.() -> Any,
    uiItemBuilder: @Composable (T) -> Unit,
    noItemsItem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    topInset: Dp = 16.dp,
) {
    Box(modifier) {
        if (items.loadState.append.endOfPaginationReached && items.itemCount == 0) {
            noItemsItem()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                reverseLayout = reverseLayout,
            ) {
                item { Box(Modifier.height(topInset)) }
                items(
                    count = items.itemCount,
                    key = items.itemKey { item -> item.key() },
                ) { itemPosition -> uiItemBuilder(items[itemPosition]!!) }
                when {
                    items.loadState.refresh == LoadState.Loading -> item {
                        LoadingFirstPage(Modifier.fillParentMaxSize())
                    }

                    items.loadState.append == LoadState.Loading -> item {
                        LoadingNextPageIndicator(Modifier.padding(top = 24.dp, bottom = 16.dp))
                    }

                    items.loadState.refresh is LoadState.Error -> item {
                        ErrorWhileLoadingFirstPage(
                            onRetryClicked = { items.retry() },
                            modifier = Modifier.fillParentMaxSize(),
                        )
                    }

                    items.loadState.append is LoadState.Error -> item {
                        ErrorWhileLoadingMorePages(
                            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                            onRetryClicked = { items.retry() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingFirstPage(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun LoadingNextPageIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun ErrorWhileLoadingFirstPage(
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error_indicator),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier.size(80.dp),
        )
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.unexpected_error_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        OutlinedButton(
            onClick = { onRetryClicked() },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.try_again),
                style = MaterialTheme.typography.titleSmall,
                color = blue,
            )
        }
    }
}

@Composable
fun ErrorWhileLoadingMorePages(
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { onRetryClicked() },
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = stringResource(R.string.try_again),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview
@Composable
private fun LoadingFirstPagePreview() {
    BluetoothMessagingAppTheme {
        LoadingFirstPage()
    }
}

@Preview
@Composable
private fun LoadingNextPagePreview() {
    BluetoothMessagingAppTheme {
        LoadingNextPageIndicator()
    }
}

@Preview
@Composable
private fun ErrorWhileLoadingFirstPagePreview() {
    BluetoothMessagingAppTheme {
        ErrorWhileLoadingFirstPage({ })
    }
}

@Preview
@Composable
private fun ErrorWhileLoadingNextPagePreview() {
    BluetoothMessagingAppTheme {
        ErrorWhileLoadingMorePages({})
    }
}
