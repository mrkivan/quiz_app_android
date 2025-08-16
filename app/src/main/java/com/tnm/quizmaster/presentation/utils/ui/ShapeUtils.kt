package com.tnm.quizmaster.presentation.utils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tnm.quizmaster.R

@Composable
fun CircleWithNumber(number: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ),
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }


}

@Composable
fun QuizProgressWithShape(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    bottomEnd = 16.dp,
                    bottomStart = 0.dp,
                    topEnd = 0.dp
                )
            )
            .background(Color.Blue)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(
                R.string.question_progress,
                currentQuestion,
                totalQuestions
            ),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJetpackComposeLayout() {
    MaterialTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            CircleWithNumber(20)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuizProgressWithShape() {
    MaterialTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            QuizProgressWithShape(22, 30)
        }
    }
}