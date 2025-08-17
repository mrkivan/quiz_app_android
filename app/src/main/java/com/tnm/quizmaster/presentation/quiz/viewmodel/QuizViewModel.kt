package com.tnm.quizmaster.presentation.quiz.viewmodel

import androidx.lifecycle.viewModelScope
import com.tnm.quizmaster.domain.model.quiz.QuizData
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.domain.usecase.quiz.GetQuizDataBySetAndTopicUseCase
import com.tnm.quizmaster.domain.usecase.result.SaveResultDataUseCase
import com.tnm.quizmaster.presentation.quiz.intent.QuizIntent
import com.tnm.quizmaster.presentation.quiz.intent.QuizNavEvent
import com.tnm.quizmaster.presentation.quiz.route.QuizScreenData
import com.tnm.quizmaster.presentation.quiz.state.QuizState
import com.tnm.quizmaster.presentation.utils.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizDataUseCase: GetQuizDataBySetAndTopicUseCase,
    private val saveResultDataUseCase: SaveResultDataUseCase
) : BaseViewModel<QuizData>() {
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _quizResultState = MutableStateFlow<Boolean?>(null)
    val quizResultState: StateFlow<Boolean?> = _quizResultState.asStateFlow()

    private var cacheQuizList = listOf<QuizData>()

    private val _navigationEvents = MutableSharedFlow<QuizNavEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val resultItems = mutableListOf<ResultData.Item>()
    private var currentQuizPosition: Int = 0

    private var cacheScreenData: QuizScreenData? = null

    fun handleIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.LoadQuiz -> fetchQuiz(intent.data)
            QuizIntent.NavigateToResult -> {
                navigateToResultScreen()
            }

            QuizIntent.SkipQuestion -> {
                moveToNextQuestion(isSkipped = true)
            }

            QuizIntent.NextQuestion -> {
                moveToNextQuestion()
            }

            QuizIntent.SubmitAnswer -> {
                submitAnswer()
            }

            is QuizIntent.UpdateSelectedAnswers -> {
                updateSelectedAnswers(intent.answers)
            }
        }
    }

    private fun fetchQuiz(quizScreenData: QuizScreenData) {
        cacheScreenData = quizScreenData
        viewModelScope.launch {
            getQuizDataUseCase(quizScreenData.quizSection?.fileName.orEmpty())
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    setError(e.message.orEmpty())
                }
                .collect { quizItems ->
                    cacheQuizList = quizItems.shuffled()
                    setSuccess(cacheQuizList[0])
                    _quizState.value = QuizState(
                        isLastItem = isLastItem(),
                        currentQuestionNumber = currentQuizPosition + 1,
                        totalQuestions = quizItems.size
                    )
                }
        }
    }

    private fun updateSelectedAnswers(answers: List<Int>) {
        _quizState.value = _quizState.value.copy(selectedAnswers = answers)
    }

    private fun submitAnswer() {
        val currentQuiz = cacheQuizList[currentQuizPosition]
        val isCorrect =
            currentQuiz.correctAnswer.answerId.toSet() == _quizState.value.selectedAnswers.toSet()

        _quizState.value = _quizState.value.copy(isSubmitted = true, showExplanation = true)
        _quizResultState.value = isCorrect

    }

    fun showExitConfirmationDialog(): Boolean {
        return (getQuizId() > 1 || _quizState.value.selectedAnswers.isNotEmpty())
    }

    fun getQuizId() = currentQuizPosition + 1
    private fun moveToNextQuestion(isSkipped: Boolean = false) {
        _quizResultState.value = null
        saveResult(isSkipped)

        val nextIndex = currentQuizPosition + 1
        if (nextIndex < cacheQuizList.size) {
            currentQuizPosition = nextIndex

            setSuccess(cacheQuizList[currentQuizPosition])
            _quizState.value = QuizState(
                currentQuestionNumber = currentQuizPosition + 1,
                totalQuestions = cacheQuizList.size,
                isLastItem = isLastItem()
            )

        } else {
            navigateToResultScreen()
        }


    }

    fun getQuiz(): QuizData {
        return cacheQuizList[currentQuizPosition]
    }

    // Save all results using use case
    private fun navigateToResultScreen() {
        saveResult()

        cacheScreenData?.let { data ->

            val correctAnswers = getCorrectResultsCount()

            val resultData = ResultData(
                quizTitle = data.quizTitle,
                quizDescription = data.quizDescription,
                resultItems = this.resultItems.toList(),
                totalCorrectAnswers = correctAnswers,
                totalQuestions = cacheQuizList.count(),
                resultPercentage = getResultPercentage(correctAnswers),
            )

            viewModelScope.launch {
                saveResultDataUseCase(data.quizSection?.fileName.orEmpty(), resultData)
                    .collect {
                        _navigationEvents.emit(QuizNavEvent.NavigateToResult(getResultKey()))
                    }

            }
        }

    }

    fun getCorrectResultsCount(): Int {
        return resultItems.count { it.result }
    }

    fun getResultPercentage(correctAnswers: Int): Int {
        val totalQuestions = cacheQuizList.count()
        return if (totalQuestions > 0) {
            ((correctAnswers.toDouble() / totalQuestions.toDouble()) * 100).toInt()
        } else 0
    }

    fun isLastItem(): Boolean {
        return currentQuizPosition + 1 == cacheQuizList.size
    }

    private fun getResultKey(): String {
        cacheScreenData?.let { data ->
            return data.quizSection?.fileName.orEmpty()
        }
        return ""
    }

    private fun saveResult(isSkipped: Boolean = false) {
        val currentQuiz = cacheQuizList[currentQuizPosition]

        val resultItem = ResultData.Item(
            questionId = currentQuiz.questionId,
            question = currentQuiz.question,
            result = currentQuiz.correctAnswer.answerId.toSet() == _quizState.value.selectedAnswers.toSet(),
            correctAnswer = currentQuiz.correctAnswer.answer,
            explanation = currentQuiz.explanation,
            isSkipped = isSkipped
        )
        resultItems.add(resultItem)
    }
}