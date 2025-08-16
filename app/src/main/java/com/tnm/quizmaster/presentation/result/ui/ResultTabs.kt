package com.tnm.quizmaster.presentation.result.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.presentation.utils.ui.AppCardDefaults
import com.tnm.quizmaster.presentation.utils.ui.SpacerMediumHeight
import com.tnm.quizmaster.presentation.utils.ui.SpacerSmallHeight

@Composable
fun ResultTabs(
    resultItems: List<ResultData.Item>
) {
    // Separate items
    val correctItems = resultItems.filter { it.result && !it.isSkipped }
    val incorrectItems = resultItems.filter { !it.result || it.isSkipped }

    // Build tab data dynamically
    val tabs = mutableListOf<Pair<String, List<ResultData.Item>>>()
    if (correctItems.isNotEmpty()) tabs.add("Correct (${correctItems.size})" to correctItems)
    if (incorrectItems.isNotEmpty()) tabs.add("Incorrect (${incorrectItems.size})" to incorrectItems)

    // Selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),

            ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(tab.first) }
                )
            }
        }
        SpacerMediumHeight()

        val filteredItems = if (selectedTabIndex == 0 && correctItems.isNotEmpty()) {
            correctItems
        } else {
            incorrectItems
        }

        // ⚡ Important: Use Column here, not LazyColumn
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredItems.forEach { item ->
                ResultItemRow(item)
            }
        }
    }
}

@Composable
fun ResultItemRow(item: ResultData.Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppCardDefaults.shape,
        elevation = AppCardDefaults.elevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.question, fontWeight = FontWeight.Bold)
            SpacerSmallHeight()
            Text(
                text = "Answer:",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Column(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                item.correctAnswer.forEach { answer ->
                    Text(
                        text = "• $answer",
                        color = if (item.result) Color(0xFF4CAF50) else Color.Red
                    )
                }
            }

            if (item.explanation.isNotEmpty()) {
                SpacerSmallHeight()
                Text(text = item.explanation, fontStyle = FontStyle.Italic)
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewQuizResultTabs() {
    MaterialTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            ResultTabs(resultItems = getMockResultData().resultItems)
        }
    }
}

