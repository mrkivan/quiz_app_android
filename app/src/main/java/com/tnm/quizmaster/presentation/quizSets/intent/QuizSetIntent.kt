package com.tnm.quizmaster.presentation.quizSets.intent

import com.tnm.quizmaster.presentation.quiz.route.QuizScreenData

sealed class QuizSetIntent {
    data class LoadQuizSet(val quizTopic: String?) : QuizSetIntent()
    data class NavigateToQuiz(val data: QuizScreenData) : QuizSetIntent()
}

sealed class QuizSetNavEvent {
    data class NavigateToQuiz(val data: QuizScreenData) : QuizSetNavEvent()
}