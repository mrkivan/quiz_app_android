package com.tnm.quizmaster.presentation.quiz.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.tnm.quizmaster.NavKeys
import com.tnm.quizmaster.QuizMasterDestinations
import com.tnm.quizmaster.R
import com.tnm.quizmaster.presentation.quiz.intent.QuizIntent
import com.tnm.quizmaster.presentation.quiz.intent.QuizNavEvent
import com.tnm.quizmaster.presentation.quiz.route.QuizScreenData
import com.tnm.quizmaster.presentation.quiz.viewmodel.QuizViewModel
import com.tnm.quizmaster.presentation.utils.state.QuizAppUiState
import com.tnm.quizmaster.presentation.utils.ui.PlaceholderScaffold
import com.tnm.quizmaster.presentation.utils.ui.QuizAppToolbar

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    quizScreenData: QuizScreenData,
    navController: NavHostController
) {
    val uiState by viewModel.state.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    // Observe one-time events
    LaunchedEffect(Unit) {
        viewModel.handleIntent(QuizIntent.LoadQuiz(quizScreenData))
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is QuizNavEvent.NavigateToResult -> {
                    navController.popBackStack()
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        NavKeys.DATA_KEY_RESULT,
                        event.key
                    )
                    navController.navigate(QuizMasterDestinations.ROUTE_RESULT)
                }
            }
        }
    }

    PlaceholderScaffold(
        toolbarConfig = QuizAppToolbar(
            title = when (uiState) {
                QuizAppUiState.Loading -> stringResource(R.string.label_loading)
                is QuizAppUiState.Success -> (uiState as QuizAppUiState.Success<QuizScreenData>).data.quizSection?.title.orEmpty()
                is QuizAppUiState.Error -> stringResource(R.string.label_error)
            },
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationIconContentDescription = stringResource(R.string.label_back),
            onNavigationClick = { navController.navigateUp() },
        ),
        uiState = uiState,
        modifier = Modifier
    ) { paddingValues, data ->

        QuizBody(
            quizData = viewModel.getQuiz(),
            quizState = quizState,
            updateSelectedAnswers = { answers ->
                viewModel.handleIntent(QuizIntent.UpdateSelectedAnswers(answers))
            },
            submitAnswer = {
                viewModel.handleIntent(QuizIntent.SubmitAnswer)
            },
            skipQuestion = {
                viewModel.handleIntent(QuizIntent.SkipQuestion)
            },
            moveToNextQuestion = {
                viewModel.handleIntent(QuizIntent.NextQuestion)
            },
            navigateToResultScreen = {
                viewModel.handleIntent(QuizIntent.NavigateToResult)
            }
        )
    }

}

