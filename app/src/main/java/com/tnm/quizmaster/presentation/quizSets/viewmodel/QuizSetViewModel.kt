package com.tnm.quizmaster.presentation.quizSets.viewmodel

import androidx.lifecycle.viewModelScope
import com.tnm.quizmaster.domain.model.quizset.QuizSetData
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.domain.usecase.quizSets.GetQuizSetListUseCase
import com.tnm.quizmaster.domain.usecase.result.GetResultDataUseCase
import com.tnm.quizmaster.presentation.quiz.route.QuizScreenData
import com.tnm.quizmaster.presentation.quizSets.intent.QuizSetIntent
import com.tnm.quizmaster.presentation.quizSets.intent.QuizSetNavEvent
import com.tnm.quizmaster.presentation.utils.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizSetViewModel @Inject constructor(
    private val getQuizSetList: GetQuizSetListUseCase,
    private val getResultDataUseCase: GetResultDataUseCase
) : BaseViewModel<QuizSetData?>() {
    private val _navigationEvents = MutableSharedFlow<QuizSetNavEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private var cacheQuizSetListData: QuizSetData? = null
    private val cacheResult = mutableMapOf<String, ResultData>()

    fun handleIntent(intent: QuizSetIntent) {
        when (intent) {
            is QuizSetIntent.LoadQuizSet -> loadQuizSet(intent.quizTopic)
            is QuizSetIntent.NavigateToQuiz -> navigateToQuiz(intent.data)
        }
    }

    fun getResultData(fileName: String): ResultData? {
        return cacheResult[fileName]
    }

    private fun loadQuizSet(quizTopic: String?) {
        viewModelScope.launch {
            getQuizSetList(quizTopic.orEmpty())
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    setError(e.message.orEmpty())
                }
                .collect { dashboard ->
                    dashboard?.let {
                        cacheQuizSetListData = it
                        // Process all sections concurrently using Flow
                        it.sections.forEach { section ->
                            getResultDataUseCase(section.fileName)
                                .firstOrNull()
                                ?.let { result ->
                                    cacheResult[section.fileName] = result
                                }
                        }
                        setSuccess(cacheQuizSetListData)
                    }
                }
        }
    }


    private fun navigateToQuiz(data: QuizScreenData) {
        viewModelScope.launch {
            _navigationEvents.emit(QuizSetNavEvent.NavigateToQuiz(data))
        }
    }
}