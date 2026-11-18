package com.ilham.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ilham.presentation.detail.MovieDetailScreen
import com.ilham.presentation.genre.GenreScreen
import com.ilham.presentation.movies.MovieListScreen

@Composable
fun MovieNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = GenreDestination) {
        composable<GenreDestination> {
            GenreScreen(
                onGenreClick = { genre ->
                    navController.navigate(MovieListDestination(genre.id, genre.name))
                },
            )
        }

        composable<GenreDestination> {
            GenreScreen(
                onGenreClick = { genre ->
                    navController.navigate(MovieListDestination(genre.id, genre.name))
                },
            )
        }

        composable<MovieListDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<MovieListDestination>()
            MovieListScreen(
                genreId = destination.genreId,
                genreName = destination.genreName,
                onMovieClick = { movie ->
                    navController.navigate(MovieDetailDestination(movie.id, movie.title))
                },
                onBack = { navController.navigateUp() },
            )
        }

        composable<MovieDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<MovieDetailDestination>()
            MovieDetailScreen(
                movieId = destination.movieId,
                title = destination.title,
                onBack = { navController.navigateUp() },
            )
        }
    }
}