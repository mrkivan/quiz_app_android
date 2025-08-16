package com.tnm.quizmaster.presentation.quiz.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.tnm.quizmaster.presentation.utils.ui.ConfirmDialog
import com.tnm.quizmaster.presentation.utils.ui.PlaceholderScaffold
import com.tnm.quizmaster.presentation.utils.ui.QuizAppToolbar

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    quizScreenData: QuizScreenData?,
    quizId: Int,
    navController: NavHostController
) {
    fun navigateToNextQuestion() {
        val quizId = viewModel.getQuizId()

        navController.navigate(QuizMasterDestinations.ROUTE_QUIZ + "/$quizId") {
            launchSingleTop = true
        }
    }

    val uiState by viewModel.state.collectAsState()
    val quizState by viewModel.quizState.collectAsState()
    val showExitConfirmationDialog = remember { mutableStateOf(false) }
    val showSubmitConfirmationDialog = remember { mutableStateOf(false) }

    LaunchedEffect(quizId) {
        if (quizId > 0) {
            viewModel.handleIntent(QuizIntent.NextQuestion)
        }
    }

    // Observe one-time events
    LaunchedEffect(Unit) {
        quizScreenData?.let {
            viewModel.handleIntent(QuizIntent.LoadQuiz(it))
        }

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

    // UI
    ConfirmDialog(
        title = "Submit Quiz",
        message = "Are you sure you want to submit this?",
        onConfirm = {
            // Actually exit screen
            navController.popBackStack()
        },
        confirmButtonLabel = "Confirm",
        showDialogState = showSubmitConfirmationDialog
    )

    ConfirmDialog(
        title = "Exit Quiz",
        message = "Are you sure you want to exit?",
        onConfirm = {
            // Actually exit screen
            navController.popBackStack()
        },
        confirmButtonLabel = "Confirm",
        showDialogState = showExitConfirmationDialog
    )


    PlaceholderScaffold(
        toolbarConfig = QuizAppToolbar(
            title = when (uiState) {
                QuizAppUiState.Loading -> stringResource(R.string.label_loading)
                is QuizAppUiState.Success -> (uiState as QuizAppUiState.Success<QuizScreenData>).data.quizSection?.title.orEmpty()
                is QuizAppUiState.Error -> stringResource(R.string.label_error)
            },
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationIconContentDescription = stringResource(R.string.label_back),
            onNavigationClick = {
                showExitConfirmationDialog.value = viewModel.showExitConfirmationDialog()
                if (!showExitConfirmationDialog.value) navController.navigateUp()
            },
        ),
        uiState = uiState,
        modifier = Modifier
    ) { paddingValues, data ->
        AnimatedContent(
            targetState = data,
            transitionSpec = {
                slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
            },
            label = "quiz-slide"
        ) { data ->
            Log.d("QuizScreen", "QuizScreen: ${data.quizTitle}")
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
                    navigateToNextQuestion()
                },
                moveToNextQuestion = {
                    navigateToNextQuestion()
                },
                navigateToResultScreen = {
                    viewModel.handleIntent(QuizIntent.NavigateToResult)
                }
            )
        }
    }
}