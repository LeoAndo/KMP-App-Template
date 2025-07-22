package com.jetbrains.kmpapp.screens.quiz

internal enum class Question(
    val message: String,
    val answers: List<String>,
    val answerIndex: Int
) {
    Q1(
        message = "Androidの開発言語は？",
        answers = listOf("Java", "PHP", "Ruby", "Go", "Swift"),
        answerIndex = 0
    ),
    Q2(message = "iOSの開発言語は？", answers = listOf("PHP", "Swift", "Ruby"), answerIndex = 1),
    Q3(
        message = "デザインツールは？",
        answers = listOf("word", "Xcode", "Figma", "Excel"),
        answerIndex = 2
    ),
    Q4(
        message = "存在する大学は？",
        answers = listOf("東京大学", "安藤大学", "伊藤大学", "田中大学"),
        answerIndex = 0
    ),
}