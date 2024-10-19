package com.loong.composedemo.feature.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.loong.composedemo.core.network.model.NetworkArticle

/**
 *
 * Compose + Paging + PullToRefresh 参考了：
 * Compose 文档：https://developer.android.google.cn/jetpack/androidx/releases/compose?hl=zh-cn
 * Compose 文档：https://developer.android.google.cn/develop/ui/compose/tutorial?hl=zh-cn
 * Compose 文档：https://developer.android.google.cn/develop/ui/compose/documentation?hl=zh-cn
 * Compose 教程总览：https://developer.android.google.cn/courses/pathways/compose?hl=zh-cn
 * Compose 教程基础：https://developer.android.google.cn/codelabs/jetpack-compose-basics?hl=zh-cn#1
 * Compose 状态：https://developer.android.google.cn/codelabs/jetpack-compose-state?hl=zh-cn#0
 * Compose 教程布局：https://developer.android.google.cn/codelabs/jetpack-compose-layouts?hl=zh-cn#2
 * Compose 教程高级：https://developer.android.google.cn/codelabs/jetpack-compose-advanced-state-side-effects?hl=zh-cn#6
 * Compose 下拉刷新：https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/pulltorefresh/PullToRefreshState
 * Compose 的阶段：https://developer.android.google.cn/develop/ui/compose/phases?hl=zh-cn
 * todo：Compose 动画（下拉刷新？）：https://developer.android.google.cn/codelabs/jetpack-compose-animation?hl=zh-cn#3
 * todo：需要继续看文档：https://developer.android.google.cn/codelabs/jetpack-compose-state?hl=zh-cn#6
 * Paging + Compose文档：https://developer.android.google.cn/reference/kotlin/androidx/paging/compose/package-summary#extension-functions
 * Paging 文档：https://developer.android.google.cn/topic/libraries/architecture/paging/v3-paged-data?hl=zh-cn
 * Paging 文档：https://developer.android.google.cn/topic/libraries/architecture/paging/load-state?hl=zh-cn
 * Paging 教程：https://developer.android.google.cn/codelabs/android-paging?hl=zh-cn#4
 * Paging 教程：https://developer.android.google.cn/codelabs/android-paging?hl=zh-cn#10
 * 注意，只有RemoteMediator的load方法里才有LoadType，只有网络请求场景时，不需要配置loadType，只需要显示界面使用即可：
 *   - LoadType - 此参数为我们提供以下信息：https://developer.android.google.cn/codelabs/android-paging?hl=zh-cn#14
 *     - 在之前已加载数据的情况下，我们是否需要在数据末尾 (LoadType.APPEND)
 *     - 或开头 (LoadType.PREPEND) 加载数据，
 *     - 或这是否是我们第一次加载数据 (LoadType.REFRESH)。
 *
 * todo：实现状态容器（状态三种提取方式）：https://developer.android.google.cn/codelabs/jetpack-compose-advanced-state-side-effects?hl=zh-cn#6
 * https://mp.weixin.qq.com/s/6oN4rFqI7K2x1fNhGeVv2Q
 *
 * mutableStateOf 和 MutableStateFlow 都可以在 Jetpack Compose 或其他 Kotlin 协程相关的场景中用于状态管理，但它们之间有一些关键的区别：
 * mutableStateOf：
 * - 用途：主要用于 Jetpack Compose 中的状态管理。
 * - 可见性：仅在 Composable 函数的作用域内有效。
 * - 更新机制：通过 by 关键字与 remember 结合使用来创建可记住的状态。更新状态时，Composable 会重新绘制。
 * - 类型：支持各种类型，包括基本类型和复杂对象。对于基本类型如 Int，mutableStateOf 实际上会使用装箱类型（例如 Integer）。
 * - 生命周期：与 Composable 的生命周期绑定，当 Composable 重建时，状态会被保留。
 * - 示例：  var count by remember { mutableStateOf(0) }
 * MutableStateFlow：
 * - 用途：用于更广泛的范围，不仅限于 Compose，也适用于整个应用的状态管理。
 * - 可见性：可以在整个应用或组件间共享。
 * - 更新机制：通过调用 emit 方法来更新状态。可以被多个协程收集并响应。
 * - 类型：同样支持各种类型。
 * - 生命周期：独立于任何特定的 Composable 或 UI 组件。可以通过取消收集器来控制生命周期。
 * - 并发控制：提供了内置的并发控制选项，如 isBuffered 和 replayPolicy。
 * - 示例：
 *   val count = MutableStateFlow(0)
 *   // 更新状态
 *   count.emit(1)
 *   // 收集状态变化
 *   LaunchedEffect(Unit) {
 *       count.collect { newCount ->
 *           // 更新UI
 *       }
 *   }
 * 总结来说：mutableStateOf 更适合局部状态管理，而 MutableStateFlow 则更适合跨组件或者全局状态管理，并且具有更好的并发控制特性。在选择使用哪个时，主要考虑状态管理的范围以及是否需要跨组件共享状态。
 *
 */

private const val TAG = "HomeScreen"

/**
 * 建议将 ViewModel 用于屏幕级可组合函数，即靠近从导航图的 activity、fragment 或目的地调用的根可组合函数。
 * 绝不应将 ViewModel 传递给其他可组合函数，而是应当仅向它们传递所需的数据以及以参数形式执行所需逻辑的函数。
 * https://developer.android.google.cn/codelabs/jetpack-compose-state?hl=zh-cn#11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val articles  = viewModel.article
    Scaffold {padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        //val articles by rememberSaveable { mutableStateOf(viewModel.article) }//注意rememberSaveable和remember的去吧，by和=的去吧
        /**
         * todo：待sdk新版本优化
         * bug: 下拉刷新时，网络请求了三次。应该时官方bug，待修复。
         * 在"androidx.compose.material3:material3:1.3.0-rc01"中下拉刷新改动很大，
         * 当前compose-bom=2024.06.00，对应的material3为版本1.2.1
         */
        if (pullToRefreshState.isRefreshing ) {
            viewModel.refreshData()
        }
        /**
         * todo：待学习Compose绘制流程
         * 注意不能用下面的article1，因为当执行HomeViewModel当init方法后，viewModel.article会发生变化，虽然viewModel
         * 当id没有变化，但是下面代码中当viewModel.article当id一直在变化，且article1.itemCount一只为0，导致加载不出来
         * 界面。且会一直执行下面LazyColumn的绘制流程。需要复习下compose的绘制流程
         *
         * 原因：
         * https://developer.android.google.cn/codelabs/jetpack-compose-basics?hl=zh-cn#6
         * https://developer.android.google.cn/codelabs/jetpack-compose-basics?hl=zh-cn#7
         */
//        val article1: LazyPagingItems<NetworkArticle> = viewModel.article.collectAsLazyPagingItems()
        val lazyPagingItems: LazyPagingItems<NetworkArticle> = articles.collectAsLazyPagingItems()//可参考源码中注释的例子
        Log.d(TAG, "HomeScreen: 刷新加载 loadState=${lazyPagingItems.loadState}")

        LaunchedEffect(lazyPagingItems.loadState) {
            when (lazyPagingItems.loadState.refresh) {
                is  LoadState.Loading -> {
                    Log.d(TAG, "HomeScreen: refresh刷新加载中。。。")
                    Unit
                }
                is LoadState.Error,is LoadState.NotLoading -> {
                    Log.d(TAG, "HomeScreen: refresh刷新加载结束，原因: lazyPagingItems.loadState.refresh=${lazyPagingItems.loadState.refresh}")
                    pullToRefreshState.endRefresh()
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ){

            if (pullToRefreshState.isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                /**
                 *  todo：待sdk新版本优化
                 *  在"androidx.compose.material3:material3:1.3.0-rc01"中下拉刷新改动很大，中可以使用distanceFraction，参考：
                 *  https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/pulltorefresh/PullToRefreshState#distanceFraction()
                 */
//                LinearProgressIndicator(
//                    modifier = Modifier.fillMaxWidth(),
//                    progress = { pullToRefreshState.distanceFraction }
//                )
            }

            /**
             * todo：参考官方讲解，此处可以单独抽出一个界面：
             * https://developer.android.google.cn/codelabs/jetpack-compose-advanced-state-side-effects?hl=zh-cn#4
             * todo：同时还要Compose界面的其他优化，且新增item点击事件。
             * todo: item 的点赞收藏viewmodle可以参考：https://developer.android.google.cn/codelabs/jetpack-compose-state?hl=zh-cn#11
             */
            if (lazyPagingItems.loadState.refresh == LoadState.Loading && lazyPagingItems.itemCount == 0) {
                Log.d(TAG, "HomeScreen: ljh 11111")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "数据加载中。。。",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            LazyColumn(Modifier.fillMaxSize()) {
                Log.d(TAG, "HomeScreen: ljh 11111 lazyPagingItems.itemCount${lazyPagingItems.itemCount}, lazyPagingItems.loadState=${lazyPagingItems.loadState}")

                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.id },
                ) { index ->
                    Log.d(TAG, "HomeScreen: ljh index $index ，： ${lazyPagingItems[index]?.title}")
//                    ListItem({ Text(text = "Item ${itemCount - it}") })
                    ListItem(
                        { Text(text = "第${index+1}个: ${lazyPagingItems[index]?.title?:return@ListItem}") },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.padding(16.dp),
                    )
                }

                Log.d(TAG, "HomeScreen: append刷新加载 lazyPagingItems.loadState.append=${lazyPagingItems.loadState.append}")
                if (lazyPagingItems.loadState.append == LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }

            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState
            )

/*            if (pullToRefreshState.isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { pullToRefreshState.progress }
                )
            }*/
        }
    }

}