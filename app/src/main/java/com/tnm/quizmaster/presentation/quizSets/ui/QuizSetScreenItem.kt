package com.tnm.quizmaster.presentation.quizSets.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tnm.quizmaster.NavKeys
import com.tnm.quizmaster.QuizMasterDestinations
import com.tnm.quizmaster.domain.model.quizset.QuizSetData
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.presentation.utils.ui.BaseCardView
import com.tnm.quizmaster.presentation.utils.ui.CircleWithNumber
import com.tnm.quizmaster.presentation.utils.ui.TvQuizBodyDesc
import com.tnm.quizmaster.presentation.utils.ui.TvQuizBodyTitle

@Composable
fun QuizSetScreenItem(
    section: QuizSetData.SectionItem,
    previousResult: ResultData?,
    onClick: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseCardView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                onClick = onClick,
                bodyContent = {

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        TvQuizBodyTitle(section.title)

                        Spacer(modifier = Modifier.height(4.dp))

                        TvQuizBodyDesc(section.description)

                        previousResult?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            PreviousResultButton(it) {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    NavKeys.DATA_KEY_RESULT,
                                    section.fileName
                                )
                                navController.navigate(QuizMasterDestinations.ROUTE_RESULT)
                            }
                        }
                    }// Column end
                }// BaseCardView end
            )

            CircleWithNumber(
                number = section.position,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }// box with circle end

    }// main box end
}

@Composable
fun PreviousResultButton(resultData: ResultData?, navigateToResultView: () -> Unit) {
    if (resultData == null) return

    val totalQuestions = resultData.resultItems.size
    val correctAnswers = resultData.resultItems.count { it.result }
    val resultPercentage = if (totalQuestions > 0) {
        (correctAnswers * 100f / totalQuestions).toInt()
    } else 0
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { navigateToResultView() },
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Previous Result: $resultPercentage%")
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate to result"
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun QuizSetScreenItemPreview() {
    MaterialTheme {
        QuizSetScreenItem(
            section = getMockQuizSetData(),
            previousResult = generateMockResultData(),
            onClick = {},
            navController = rememberNavController(),
            modifier = Modifier,
        )
    }
}
