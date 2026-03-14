package com.launcherx.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.launcherx.ui.theme.LauncherColors

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPage) {
                LauncherColors.pageIndicatorActive
            } else {
                LauncherColors.pageIndicatorInactive
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (index == currentPage) 7.dp else 6.dp)
                    .background(color, CircleShape)
            )
        }
    }
}
