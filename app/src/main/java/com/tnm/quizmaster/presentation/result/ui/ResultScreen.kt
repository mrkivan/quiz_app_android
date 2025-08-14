package com.tnm.quizmaster.presentation.result.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tnm.quizmaster.R
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.presentation.result.viewmodel.ResultViewModel
import com.tnm.quizmaster.presentation.utils.state.QuizAppUiState
import com.tnm.quizmaster.presentation.utils.ui.PlaceholderScaffold
import com.tnm.quizmaster.presentation.utils.ui.QuizAppToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    key: String,
    viewModel: ResultViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getResult(key)
    }
    PlaceholderScaffold(
        toolbarConfig = QuizAppToolbar(
            title = when (uiState) {
                QuizAppUiState.Loading -> stringResource(R.string.label_loading)
                is QuizAppUiState.Success -> (uiState as QuizAppUiState.Success<ResultData>).data.quizTitle.orEmpty()
                is QuizAppUiState.Error -> stringResource(R.string.label_error)
            },
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationIconContentDescription = stringResource(R.string.label_back),
            onNavigationClick = { navController.navigateUp() },
        ),
        uiState = uiState,
        modifier = Modifier
    ) { paddingValues, data ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Section
            item {
                ResultReportCard(data)
            }
            // Question Cards
            items(data.resultItems) { item ->
                ResultCard(item)
            }
        }
    }
}

@Composable
fun ResultReportCard(data: ResultData) {
    val totalQuestions = data.resultItems.size
    val correctAnswers = data.resultItems.count { it.result }
    val resultPercentage = if (totalQuestions > 0) {
        (correctAnswers * 100f / totalQuestions).toInt()
    } else 0
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        data.quizDescription?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(
                        R.string.result_total_questions,
                        totalQuestions
                    )
                )
                Text(
                    stringResource(
                        R.string.result_correct_answers,
                        correctAnswers
                    )
                )
                Text(
                    stringResource(
                        R.string.result_percentage,
                        resultPercentage
                    )
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MaterialTheme {
        ResultReportCard(getMockResultData())
    }
}