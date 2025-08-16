package com.tnm.quizmaster.presentation.quiz.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.tnm.quizmaster.domain.model.quiz.QuizData
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.domain.usecase.quiz.GetQuizDataBySetAndTopicUseCase
import com.tnm.quizmaster.domain.usecase.result.SaveResultDataUseCase
import com.tnm.quizmaster.presentation.quiz.intent.QuizIntent
import com.tnm.quizmaster.presentation.quiz.intent.QuizNavEvent
import com.tnm.quizmaster.presentation.quiz.route.QuizScreenData
import com.tnm.quizmaster.presentation.quiz.state.QuizState
import com.tnm.quizmaster.presentation.utils.state.QuizAppUiState
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
) : BaseViewModel<QuizScreenData>() {
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val cacheQuizList = mutableListOf<QuizData>()

    private val _navigationEvents = MutableSharedFlow<QuizNavEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val resultItems = mutableListOf<ResultData.Item>()
    private var currentQuizPosition: Int = 0

    fun handleIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.LoadQuiz -> fetchQuiz(intent.data)
            QuizIntent.NavigateToResult -> {
                navigateToResultScreen()
            }

            QuizIntent.SkipQuestion,
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
        viewModelScope.launch {
            getQuizDataUseCase(quizScreenData.quizSection?.fileName.orEmpty())
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    setError(e.message.orEmpty())
                }
                .collect { quizItems ->
                    cacheQuizList.addAll(quizItems)
                    setSuccess(quizScreenData.copy(currentQuizPosition = 0))
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
        _quizState.value = _quizState.value.copy(isSubmitted = true, showExplanation = true)
    }

    private fun skipQuestion() {
        /*_quizState.value = _quizState.value.copy(
            isSubmitted = false,
            showExplanation = false,
            selectedAnswers = emptyList()
        )*/
        moveToNextQuestion()
    }

    fun showExitConfirmationDialog(): Boolean {
        return (getQuizId() > 1 || _quizState.value.selectedAnswers.isNotEmpty())
    }

    fun getQuizId() = currentQuizPosition + 1
    private fun moveToNextQuestion(isSkipped: Boolean = false) {
        saveResult(isSkipped)

        val currentData: QuizScreenData? = (state.value as? QuizAppUiState.Success)?.data
        currentData?.let { data ->
            val nextIndex = data.currentQuizPosition + 1
            if (nextIndex < cacheQuizList.size) {
                currentQuizPosition = nextIndex
                setSuccess(
                    data.copy(
                        currentQuizPosition = nextIndex,
                    )
                )
                _quizState.value = QuizState(
                    currentQuestionNumber = currentQuizPosition + 1,
                    totalQuestions = cacheQuizList.size,
                    isLastItem = isLastItem()
                )

            } else {
                navigateToResultScreen()
            }
        }

    }

    fun getQuiz(): QuizData {
        return cacheQuizList[currentQuizPosition]
    }

    // Save all results using use case
    private fun navigateToResultScreen() {
        saveResult()

        val currentData: QuizScreenData? = (state.value as? QuizAppUiState.Success)?.data
        currentData?.let { data ->

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

    @SuppressLint("DefaultLocale")
    fun getResultPercentage(correctAnswers: Int): String {
        val totalQuestions = cacheQuizList.count()
        return if (totalQuestions > 0) {
            val percentage = (correctAnswers.toDouble() / totalQuestions.toDouble()) * 100
            String.format("%.2f", percentage) + "%"
        } else {
            "0%"
        }
    }

    fun isLastItem(): Boolean {
        return currentQuizPosition + 1 == cacheQuizList.size
    }

    private fun getResultKey(): String {
        val currentData: QuizScreenData? = (state.value as? QuizAppUiState.Success)?.data
        currentData?.let { data ->
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