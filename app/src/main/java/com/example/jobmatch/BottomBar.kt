@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController


@Composable
fun BottomBar(navController: NavController, userRole: String) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, 0),
        NavItem("Profile", Icons.Default.AccountCircle, 0)
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var bottomBarVisible by remember { mutableStateOf(true) }

    // Scroll behavior for BottomBar visibility
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            if (bottomBarVisible) {
                NavigationBar(
                    modifier = Modifier
                        .height(56.dp)
                        .zIndex(1f),
                ) {
                    navItemList.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            icon = {
                                BadgedBox(badge = {
                                    if (navItem.badgeCount > 0)
                                        Badge { Text(text = navItem.badgeCount.toString()) }
                                }) {
                                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                                }
                            },
                            label = { Text(text = navItem.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController,
            userRole = userRole,
            onScroll = { isScrollingUp ->
                // Update visibility based on scroll direction
                bottomBarVisible = !isScrollingUp
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    userRole: String,
    onScroll: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()

    // Track the scroll direction by comparing the scroll offset
    val isScrollingUp by derivedStateOf {
        val firstVisibleItemIndex = scrollState.firstVisibleItemIndex
        val firstVisibleItemScrollOffset = scrollState.firstVisibleItemScrollOffset
        firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0
    }

    // Call onScroll when scroll direction changes
    LaunchedEffect(scrollState) {
        snapshotFlow { isScrollingUp }.collect { onScroll(it) }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 56.dp), // Leave space for the bottom bar
    ) {
        items(50) { index ->
            Text(
                text = "Item $index",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

