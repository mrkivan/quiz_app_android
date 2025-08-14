package com.tnm.quizmaster.presentation.result.viewmodel

import androidx.lifecycle.viewModelScope
import com.tnm.quizmaster.domain.model.result.ResultData
import com.tnm.quizmaster.domain.usecase.result.GetResultDataUseCase
import com.tnm.quizmaster.presentation.utils.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getResultDataUseCase: GetResultDataUseCase,
) : BaseViewModel<ResultData>() {
    fun getResult(key: String) {
        viewModelScope.launch {
            getResultDataUseCase(key)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    setError(e.message.orEmpty())
                }
                .collect { result ->
                    if (result == null) setError(ERROR_DATA_NOT_FOUND)
                    else {
                        setSuccess(
                            ResultData(
                                quizTitle = result.quizTitle,
                                quizDescription = result.quizDescription,
                                resultItems = result.resultItems,
                                totalCorrectAnswers = result.totalCorrectAnswers,
                                totalQuestions = result.totalQuestions,
                                resultPercentage = result.resultPercentage
                            )
                        )
                    }

                }
        }

    }
}