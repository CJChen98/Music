package cn.chitanda.music.ui.scene.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import cn.chitanda.music.R
import cn.chitanda.music.ui.LocalNavController
import cn.chitanda.music.ui.LocalUserViewModel
import cn.chitanda.music.ui.scene.UserViewModel
import cn.chitanda.music.ui.scene.home.HomeScene
import cn.chitanda.music.ui.scene.message.MessageScene
import cn.chitanda.music.ui.scene.mine.MineScene
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.navigationBarsHeight
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
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun MainScene(
    userViewModel: UserViewModel = LocalUserViewModel.current,
    navController: NavController = LocalNavController.current
) {
    val homeNavController = rememberAnimatedNavController()
    Scaffold(
        bottomBar = {
            BottomBar(homeNavController)
        }) {
        AnimatedNavHost(
            modifier = Modifier.padding(it),
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
    val containerColor = MaterialTheme.colorScheme.inverseOnSurface
    Column {
        NavigationBar(modifier = Modifier, containerColor = containerColor) {
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
        Spacer(
            modifier = Modifier
                .background(color = containerColor)
                .navigationBarsHeight()
                .fillMaxWidth()
        )
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


