package by.my.elections.presentation.navigation

sealed class NavigationAction {
    object OpenSplash : NavigationAction()
    object OpenLogin : NavigationAction()
    object OpenMain : NavigationAction()
}