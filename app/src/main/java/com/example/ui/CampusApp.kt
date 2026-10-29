package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CampusBottomNav
import com.example.ui.components.CampusTab
import com.example.ui.components.CampusTopBar
import com.example.ui.screens.*
import com.example.ui.theme.CampusCircleTheme

sealed class Screen {
    data object Splash : Screen()
    data object Onboarding : Screen()
    data class Main(val tab: CampusTab = CampusTab.HOME) : Screen()
    data class PostDetail(val postId: String) : Screen()
    data class CommunityDetail(val communityId: String) : Screen()
    data object LocalGuide : Screen()
    data object Notifications : Screen()
    data object AdminDashboard : Screen()
    data object Settings : Screen()
}

@Composable
fun CampusApp(
    viewModel: CampusViewModel = viewModel()
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedTab by remember { mutableStateOf(CampusTab.HOME) }
    var createPostCommunityId by remember { mutableStateOf<String?>(null) }
    var createPostAnonymousStart by remember { mutableStateOf(false) }

    // Screen navigation history stack
    val screenStack = remember { mutableStateListOf<Screen>() }

    fun navigateTo(screen: Screen) {
        screenStack.add(currentScreen)
        currentScreen = screen
    }

    fun popBack() {
        if (screenStack.isNotEmpty()) {
            currentScreen = screenStack.removeLast()
        } else {
            currentScreen = Screen.Main(CampusTab.HOME)
            selectedTab = CampusTab.HOME
        }
    }

    CampusCircleTheme(darkTheme = isDarkTheme) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is Screen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = Screen.Main(CampusTab.HOME)
                        }
                    )
                }

                is Screen.Onboarding -> {
                    BackHandler {
                        currentScreen = Screen.Main(CampusTab.HOME)
                    }
                    OnboardingScreen(
                        currentUser = currentUser,
                        onComplete = { dept, sem, city ->
                            viewModel.updateUserProfile(
                                displayName = currentUser.displayName,
                                department = dept,
                                semester = sem,
                                commuteCity = city,
                                bio = currentUser.bio
                            )
                            currentScreen = Screen.Main(CampusTab.HOME)
                        }
                    )
                }

                is Screen.Main -> {
                    BackHandler(enabled = selectedTab != CampusTab.HOME) {
                        selectedTab = CampusTab.HOME
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CampusTopBar(
                                title = when (selectedTab) {
                                    CampusTab.HOME -> "CampusCircle"
                                    CampusTab.EXPLORE -> "Explore Circles"
                                    CampusTab.CREATE -> "New Discussion"
                                    CampusTab.COMMUTE -> "Commute Hub"
                                    CampusTab.PROFILE -> "Student Profile"
                                },
                                unreadCount = unreadNotifs,
                                userRole = currentUser.role,
                                onSearchClick = {
                                    selectedTab = CampusTab.EXPLORE
                                },
                                onNotificationsClick = {
                                    navigateTo(Screen.Notifications)
                                },
                                onAdminClick = {
                                    navigateTo(Screen.AdminDashboard)
                                }
                            )
                        },
                        bottomBar = {
                            CampusBottomNav(
                                selectedTab = selectedTab,
                                onTabSelected = { tab ->
                                    selectedTab = tab
                                    if (tab == CampusTab.CREATE) {
                                        createPostCommunityId = null
                                        createPostAnonymousStart = false
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        when (selectedTab) {
                            CampusTab.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onPostClick = { postId ->
                                        navigateTo(Screen.PostDetail(postId))
                                    },
                                    onCommunityClick = { commId ->
                                        navigateTo(Screen.CommunityDetail(commId))
                                    },
                                    onQuickAnonymousPost = {
                                        createPostCommunityId = "confessions"
                                        createPostAnonymousStart = true
                                        selectedTab = CampusTab.CREATE
                                    },
                                    onCommuteClick = {
                                        selectedTab = CampusTab.COMMUTE
                                    },
                                    onLocalGuideClick = {
                                        navigateTo(Screen.LocalGuide)
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }

                            CampusTab.EXPLORE -> {
                                ExploreScreen(
                                    viewModel = viewModel,
                                    onCommunityClick = { commId ->
                                        navigateTo(Screen.CommunityDetail(commId))
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }

                            CampusTab.CREATE -> {
                                CreatePostScreen(
                                    viewModel = viewModel,
                                    currentUser = currentUser,
                                    preselectedCommunityId = createPostCommunityId,
                                    startAsAnonymous = createPostAnonymousStart,
                                    onPostCreated = { newPostId ->
                                        selectedTab = CampusTab.HOME
                                        navigateTo(Screen.PostDetail(newPostId))
                                    },
                                    onCancel = {
                                        selectedTab = CampusTab.HOME
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }

                            CampusTab.COMMUTE -> {
                                CommuteScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }

                            CampusTab.PROFILE -> {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onPostClick = { postId ->
                                        navigateTo(Screen.PostDetail(postId))
                                    },
                                    onCommunityClick = { commId ->
                                        navigateTo(Screen.CommunityDetail(commId))
                                    },
                                    onSettingsClick = {
                                        navigateTo(Screen.Settings)
                                    },
                                    onAdminClick = {
                                        navigateTo(Screen.AdminDashboard)
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }

                is Screen.PostDetail -> {
                    BackHandler { popBack() }
                    PostDetailScreen(
                        postId = screen.postId,
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onCommunityClick = { commId ->
                            navigateTo(Screen.CommunityDetail(commId))
                        }
                    )
                }

                is Screen.CommunityDetail -> {
                    BackHandler { popBack() }
                    CommunityDetailScreen(
                        communityId = screen.communityId,
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onPostClick = { postId ->
                            navigateTo(Screen.PostDetail(postId))
                        },
                        onCreatePostInCommunity = { commId ->
                            createPostCommunityId = commId
                            createPostAnonymousStart = false
                            selectedTab = CampusTab.CREATE
                            currentScreen = Screen.Main(CampusTab.CREATE)
                        }
                    )
                }

                is Screen.LocalGuide -> {
                    BackHandler { popBack() }
                    LocalGuideScreen(
                        viewModel = viewModel,
                        onBack = { popBack() }
                    )
                }

                is Screen.Notifications -> {
                    BackHandler { popBack() }
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onPostClick = { postId ->
                            navigateTo(Screen.PostDetail(postId))
                        }
                    )
                }

                is Screen.AdminDashboard -> {
                    BackHandler { popBack() }
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onBack = { popBack() }
                    )
                }

                is Screen.Settings -> {
                    BackHandler { popBack() }
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onReopenOnboarding = {
                            navigateTo(Screen.Onboarding)
                        }
                    )
                }
            }
        }
    }
}
