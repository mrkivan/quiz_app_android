package com.tnm.quizmaster.presentation.result.ui

import com.tnm.quizmaster.domain.model.result.ResultData

fun getMockResultScreenItem(
    result: Boolean = false,
    correctAnswer: List<String> = emptyList()
): ResultData.Item {
    return ResultData.Item(
        questionId = 1,
        question = "What is the capital of France?",
        answerSectionTitle = "Correct Answer",
        correctAnswer = correctAnswer,
        explanation = "Paris is the capital and largest city of France.",
        result = result
    )
}

fun getMockResultData(): ResultData {
    return ResultData(
        quizTitle = "General Knowledge Quiz",
        quizDescription = "This is a sample quiz description.",
        resultItems = listOf(
            ResultData.Item(
                questionId = 1,
                question = "What is the capital of France?",
                answerSectionTitle = "Correct Answer",
                correctAnswer = listOf("Paris"),
                explanation = "Paris is the capital and largest city of France.",
                result = true
            ),
            ResultData.Item(
                questionId = 2,
                question = "What is 2 + 2?",
                answerSectionTitle = "Correct Answer",
                correctAnswer = listOf("4"),
                explanation = "Basic arithmetic: 2 + 2 equals 4.",
                result = false
            )
        ),
        totalCorrectAnswers = 1,
        totalQuestions = 1,
        resultPercentage = 0
    )
}