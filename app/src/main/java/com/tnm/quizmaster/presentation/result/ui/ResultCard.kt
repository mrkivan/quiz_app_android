package com.tnm.quizmaster.presentation.result.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tnm.quizmaster.R
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.presentation.utils.ui.TvMedium
import com.tnm.quizmaster.presentation.utils.ui.TvResultSectionTitle
import com.tnm.quizmaster.presentation.utils.ui.TvResultTitle

@Composable
fun ResultCard(item: ResultData.Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, color = if (item.result) Color.Green else Color.Red)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Question Title
                TvResultTitle(item.question)


                // Answer Section
                item.answerSectionTitle?.let { title ->
                    TvResultSectionTitle(title)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.correctAnswer.forEach { answer ->
                        TvMedium(answer)
                    }
                }

                // Explanation
                if (item.explanation.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            R.string.explanation,
                            item.explanation
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultCardWrongAnswerPreview() {
    MaterialTheme {
        ResultCard(getMockResultScreenItem())
    }
}

@Preview(showBackground = true)
@Composable
fun ResultCardCorrectAnswerPreview() {
    MaterialTheme {
        ResultCard(getMockResultScreenItem(result = true, correctAnswer = listOf("Paris")))
    }
}