package com.jetbrains.kmpapp.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun QuizScreen() {
    val pagerState = rememberPagerState(pageCount = { Question.entries.size })
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isFinishedQuiz by remember { mutableStateOf(false) }
    var collectAnswerCount by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues()).padding(12.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            modifier = Modifier.heightIn(min = 400.dp)
                .background(color = MaterialTheme.colorScheme.secondary),
            state = pagerState,
            userScrollEnabled = false,
        ) { pageIndex ->
            Box(
                Modifier.padding(8.dp).fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                val message =
                    Question.entries.first { it.ordinal == pageIndex }.message
                Text(text = message, overflow = TextOverflow.Ellipsis)
            }
        }

        AnimatedVisibility(isFinishedQuiz) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${collectAnswerCount}問正解しました。")
                Button(onClick = {
                    scope.launch {
                        pagerState.scrollToPage(page = 0)
                        collectAnswerCount = 0
                        isFinishedQuiz = false
                    }
                }) {
                    Text("やり直す")
                }
            }
        }
        val question = Question.entries
            .first { question -> question.ordinal == pagerState.currentPage }
        question.answers.forEachIndexed { index, answerText ->
            Button(
                onClick = {
                    scope.launch {
                        isFinishedQuiz =
                            Question.entries.size <= pagerState.currentPage + 1
                        pagerState.scrollToPage(page = pagerState.currentPage + 1)
                        if (question.answerIndex == index) { // 正解
                            collectAnswerCount++
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isFinishedQuiz,
            ) {
                Text(text = answerText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}