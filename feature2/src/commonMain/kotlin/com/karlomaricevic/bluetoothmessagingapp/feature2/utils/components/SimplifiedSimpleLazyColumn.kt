package com.karlomaricevic.bluetoothmessagingapp.feature2.utils.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> SimplifiedSimpleLazyColumn(
    items: List<T>,
    key: (T) -> Any,
    uiItemBuilder: @Composable (T) -> Unit,
    noItemsItem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    topInset: Dp = 16.dp,
) {
    Box(modifier) {
        if (items.isEmpty()) {
            noItemsItem()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                reverseLayout = reverseLayout,
                state = state,
            ) {
                item { Box(Modifier.height(topInset)) }
                items(
                    count = items.size,
                    key = { index -> key(items[index]) },
                ) { index ->
                    uiItemBuilder(items[index])
                }
            }
        }
    }
}