package com.discogs.mobilechallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.discogs.mobilechallenge.presentation.search.SearchScreen

object Routes {
    const val SEARCH = "search"
    const val ARTIST_DETAIL = "artistDetail/{artistId}"

    fun artistDetail(artistId: Int) = "artistDetail/$artistId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) {
            SearchScreen(
                onArtistClick = { artistId ->
                    navController.navigate(Routes.artistDetail(artistId))
                },
            )
        }
        composable(
            route = Routes.ARTIST_DETAIL,
            arguments = listOf(navArgument("artistId") { type = NavType.IntType }),
        ) {
            //TODO ArtistDetailScreen
        }
    }
}
