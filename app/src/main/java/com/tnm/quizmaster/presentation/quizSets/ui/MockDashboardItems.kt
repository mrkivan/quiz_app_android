package com.tnm.quizmaster.presentation.quizSets.ui

import com.tnm.quizmaster.domain.model.dashboard.DashboardData

fun getMockDashboardItems(): List<DashboardData.Item> {
    return listOf(
        DashboardData.Item(
            total = 3,
            title = "Android Quizzes",
            description = "Test your Android knowledge with modern topics like Jetpack Compose, Hilt, and Coroutines.",
            sectionId = 1,
            topic = "Android",
            sections = listOf(
                DashboardData.Section(
                    title = "Jetpack Compose Basics",
                    description = "Learn and test the fundamentals of Jetpack Compose.",
                    position = 1,
                    fileName = "android_jetpack_compose_1.json"
                ),
                DashboardData.Section(
                    title = "Hilt Dependency Injection",
                    description = "Questions focused on Hilt setup, modules, and injection patterns.",
                    position = 2,
                    fileName = "android_hilt_1.json"
                ),
                DashboardData.Section(
                    title = "Coroutines and Flow",
                    description = "Concurrency and async programming in Android using Kotlin.",
                    position = 3,
                    fileName = "android_coroutines_1.json"
                )
            )
        ),
        DashboardData.Item(
            total = 2,
            title = "Kotlin Quizzes",
            description = "Sharpen your Kotlin skills including functional programming and coroutines.",
            sectionId = 2,
            topic = "Kotlin",
            sections = listOf(
                DashboardData.Section(
                    title = "Kotlin Basics",
                    description = "Covers syntax, control flow, and types.",
                    position = 1,
                    fileName = "kotlin_basics_1.json"
                ),
                DashboardData.Section(
                    title = "Kotlin Coroutines",
                    description = "Test coroutine scope, context, and suspend functions.",
                    position = 2,
                    fileName = "kotlin_coroutines_1.json"
                )
            )
        ),
        DashboardData.Item(
            total = 2,
            title = "Java Quizzes",
            description = "Revise Java fundamentals, OOP concepts, and collections.",
            sectionId = 3,
            topic = "Java",
            sections = listOf(
                DashboardData.Section(
                    title = "Java OOP",
                    description = "Classes, inheritance, and polymorphism.",
                    position = 1,
                    fileName = "java_oop_1.json"
                ),
                DashboardData.Section(
                    title = "Java Collections",
                    description = "Covers lists, maps, sets, and their usage.",
                    position = 2,
                    fileName = "java_collections_1.json"
                )
            )
        )
    )
}