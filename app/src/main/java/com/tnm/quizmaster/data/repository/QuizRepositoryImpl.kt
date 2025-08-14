package com.tnm.quizmaster.data.repository

import com.tnm.quizmaster.data.api.QuizApi
import com.tnm.quizmaster.data.cache.CacheManager
import com.tnm.quizmaster.data.mapper.toDomain
import com.tnm.quizmaster.data.mapper.toQuizSetDomain
import com.tnm.quizmaster.domain.model.dashboard.DashboardData
import com.tnm.quizmaster.domain.model.quiz.QuizData
import com.tnm.quizmaster.domain.model.quizset.QuizSetData
import com.tnm.quizmaster.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApi,
    private val cache: CacheManager
) : QuizRepository {

    override fun getDashboardData(): Flow<DashboardData> = flow {
        val cached = cache.getDashboard()
        if (cached != null) {
            emit(cached.toDomain())
            return@flow
        }

        val remoteDto = api.getDashboard()
        val remote = remoteDto.toDomain()
        cache.saveDashboard(remote)
        emit(remote)
    }

    override fun getQuizListByTopic(topic: String): Flow<QuizSetData?> = flow {
        val cached = cache.getDashboard()
        if (cached != null) {
            val data = cached.items.firstOrNull { it.topic == topic }
            emit(data?.toQuizSetDomain())
        } else {
            emit(null)
        }
    }

    override fun getQuizzesBySetAndTopic(fileName: String): Flow<List<QuizData>> = flow {
        val cached = cache.getQuiz(fileName)
        if (cached != null) {
            emit(cached.items.map { it.toDomain() })
            return@flow
        }
        val remote = api.getQuizSet(fileName)
        cache.saveQuiz(fileName, remote)
        emit(remote.items.map { it.toDomain() })
    }
}