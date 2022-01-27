package cn.chitanda.music.ui.scene.main

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import cn.chitanda.music.R
import cn.chitanda.music.media.connect.NOTHING_PLAYING
import cn.chitanda.music.media.extensions.isPlaying
import cn.chitanda.music.ui.LocalMusicControllerBarHeight
import cn.chitanda.music.ui.LocalMusicViewModel
import cn.chitanda.music.ui.scene.home.HomeScene
import cn.chitanda.music.ui.scene.message.MessageScene
import cn.chitanda.music.ui.scene.mine.MineScene
import cn.chitanda.music.ui.widget.CoilImage
import cn.chitanda.music.ui.widget.navbar.BottomNavigationBar
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi

/**
 *@author: Chen
 *@createTime: 2021/8/31 13:35
 *@description:
 **/
private const val TAG = "HomeScene"

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun MainScene() {
    val homeNavController = rememberAnimatedNavController()
    var currentDestination by remember { mutableStateOf(homeNavController.currentDestination) }
    var musicControllerBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val musicViewModel = LocalMusicViewModel.current
    val playbackState by musicViewModel.playbackState.observeAsState()
    val nowPlaying by musicViewModel.nowPlaying.observeAsState()
    CompositionLocalProvider(LocalMusicControllerBarHeight provides musicControllerBarHeight) {
        Scaffold(
            bottomBar = {
                BottomBar(homeNavController)
            }) {
            Box(modifier = Modifier.padding(it)) {
                AnimatedNavHost(
                    navController = homeNavController,
                    startDestination = MainPageItem.Find.route
                ) {
                    composable(
                        MainPageItem.Find.route,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        HomeScene()
                    }
                    composable(
                        MainPageItem.Message.route,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        MessageScene()
                    }
                    composable(
                        MainPageItem.Mine.route,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        MineScene()
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = currentDestination?.route != MainPageItem.Message.route && nowPlaying != null && nowPlaying != NOTHING_PLAYING
                ) {
                    Surface(
                        modifier = Modifier
                            .onSizeChanged {
                                musicControllerBarHeight = with(density) { it.height.toDp() }
                            }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 3.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp), contentAlignment = Alignment.CenterStart
                        ) {

                            nowPlaying?.description?.let { description ->
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CoilImage(
                                        url = description.iconUri,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1f, true),
                                        shape = CircleShape
                                    )
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Text(
                                            text = "${description.title}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${description.subtitle}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = { }, modifier = Modifier
                                    ) {
                                        Icon(
                                            painter = painterResource(id = if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val listener = remember(homeNavController) {
        MainDestinationChangedListener {
            currentDestination = it
        }
    }
    DisposableEffect(key1 = homeNavController) {
        homeNavController.addOnDestinationChangedListener(listener)
        onDispose {
            homeNavController.removeOnDestinationChangedListener(listener)
        }
    }
}

class MainDestinationChangedListener(private val callBack: (NavDestination?) -> Unit) :
    NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        callBack(destination)
    }

}

@ExperimentalAnimationApi
private val exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition) = {
    fadeOut()
}

@ExperimentalAnimationApi
private val enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition) = {
    fadeIn(tween(500))
}

@Composable
private fun BottomBar(homeNavController: NavController) {
    val list = remember { listOf(MainPageItem.Find, MainPageItem.Message, MainPageItem.Mine) }
    var currentPage by remember {
        mutableStateOf<MainPageItem>(MainPageItem.Find)
    }
    BottomNavigationBar {
        list.forEach { scene ->
            NavigationBarItem(selected = currentPage == scene, onClick = {
                currentPage = scene
                homeNavController.navigate(scene.route) {
                    popUpTo(homeNavController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    painter = painterResource(id = scene.icon),
                    contentDescription = null
                )
            }, label = {
                Text(text = stringResource(id = scene.label))
            }, alwaysShowLabel = true)
        }
    }
}

sealed class MainPageItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Find : MainPageItem("found", R.string.label_found, R.drawable.ic_home)
    object Mine : MainPageItem("mine", R.string.label_mine, R.drawable.ic_me)
    object Message : MainPageItem("message", R.string.label_message, R.drawable.ic_chat)
}



