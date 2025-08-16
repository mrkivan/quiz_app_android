package com.tnm.quizmaster.presentation.quiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tnm.quizmaster.R
import com.tnm.quizmaster.domain.model.quiz.QuizData
import com.tnm.quizmaster.presentation.quiz.state.QuizState
import com.tnm.quizmaster.presentation.utils.ui.ConfirmDialog
import com.tnm.quizmaster.presentation.utils.ui.QuizProgressWithShape
import com.tnm.quizmaster.presentation.utils.ui.TvHeadSmall
import com.tnm.quizmaster.presentation.utils.ui.TvLarge
import com.tnm.quizmaster.presentation.utils.ui.TvMedium

@Composable
fun QuizBody(
    quizData: QuizData,
    quizState: QuizState,
    updateSelectedAnswers: (List<Int>) -> Unit,
    submitAnswer: () -> Unit,
    skipQuestion: () -> Unit,
    moveToNextQuestion: () -> Unit,
    navigateToResultScreen: () -> Unit,
) {
    var showSkipDialog = remember { mutableStateOf(false) }
    ConfirmDialog(
        title = "Skip this question",
        message = "Do you really want to skip this?",
        confirmButtonLabel = "Skip",
        onConfirm = {
            skipQuestion()
        },
        showDialogState = showSkipDialog
    )


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TvHeadSmall(
                        text = quizData.question,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    QuizProgressWithShape(
                        currentQuestion = quizState.currentQuestionNumber,
                        totalQuestions = quizState.totalQuestions,
                        modifier = Modifier.align(Alignment.Top)
                    )
                }
            }
            // Question and Answer Options
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(quizData.answerCellList) { index, answer ->
                        val isCorrect = quizData.correctAnswer.answerId.contains(answer.answerId)
                        val isSelected = quizState.selectedAnswers.contains(answer.answerId)
                        val backgroundColor = when {
                            !quizState.isSubmitted -> MaterialTheme.colorScheme.surface
                            isSelected && isCorrect -> Color.Green.copy(alpha = 0.2f)
                            isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Column(
                            modifier = Modifier.clickable(
                                enabled = !quizState.isSubmitted
                            ) {
                                updateSelectedAnswers(
                                    if (quizData.answerCellType == 0) {
                                        // Single choice: replace selection
                                        listOf(answer.answerId)
                                    } else {
                                        // Multiple choice: toggle selection
                                        if (isSelected) {
                                            quizState.selectedAnswers - answer.answerId
                                        } else {
                                            quizState.selectedAnswers + answer.answerId
                                        }
                                    }
                                )
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(backgroundColor)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (quizData.answerCellType == 0) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            updateSelectedAnswers(listOf(answer.answerId))
                                        },
                                        enabled = !quizState.isSubmitted
                                    )
                                } else {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            updateSelectedAnswers(
                                                if (it) {
                                                    quizState.selectedAnswers + answer.answerId
                                                } else {
                                                    quizState.selectedAnswers - answer.answerId
                                                }
                                            )
                                        },
                                        enabled = !quizState.isSubmitted
                                    )
                                }
                                TvLarge(answer.data, Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }

                // Explanation
                if (quizState.showExplanation) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        TvMedium(quizData.explanation, Modifier.padding(16.dp))
                    }
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        showSkipDialog.value = true
                    },
                    enabled = !quizState.isSubmitted && quizState.selectedAnswers.isEmpty() && !quizState.isLastItem,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_skip))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { submitAnswer() },
                    enabled = quizState.selectedAnswers.isNotEmpty() && !quizState.isSubmitted,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_submit))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (quizState.isLastItem) {
                            navigateToResultScreen()
                        } else {
                            moveToNextQuestion()
                        }
                    },
                    enabled = quizState.isSubmitted || quizState.isLastItem,
                    modifier = Modifier.weight(1f)
                ) {
                    val strId = if (quizState.isLastItem) R.string.btn_result else R.string.btn_next
                    Text(stringResource(strId))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizBodyPreview() {
    MaterialTheme {
        QuizBody(
            quizData = mockQuizData,
            quizState = QuizState(
                currentQuestionNumber = 20,
                totalQuestions = 30,
                selectedAnswers = listOf(),
                showExplanation = false,
                isSubmitted = false,
                isLastItem = false,
            ),
            updateSelectedAnswers = {},
            submitAnswer = {},
            skipQuestion = {},
            moveToNextQuestion = {},
            navigateToResultScreen = {}

        )
    }
}