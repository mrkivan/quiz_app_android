package com.tnm.quizmaster.presentation.utils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Preview(showBackground = true)
@Composable
fun PreviewJetpackComposeLayout() {
    MaterialTheme {
        CircleWithNumber(20)
    }
}