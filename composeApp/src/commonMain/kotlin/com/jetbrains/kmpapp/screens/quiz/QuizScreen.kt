package com.jetbrains.kmpapp.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.screens.Screens
import kmp_app_template.composeapp.generated.resources.Res
import kmp_app_template.composeapp.generated.resources.back
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuizScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(pageCount = { Question.entries.size })
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isFinishedQuiz by remember { mutableStateOf(false) }
    var collectAnswerCount by remember { mutableStateOf(0) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(Screens.QUIZ.name) }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
                }
            })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(12.dp)
                    .fillMaxSize()
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
                        modifier = Modifier.padding(8.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
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
                                val msg = if (question.answerIndex == index) {
                                    collectAnswerCount++
                                    "正解です！"
                                } else {
                                    "不正解です！"
                                }
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isFinishedQuiz,
                    ) {
                        Text(text = answerText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        })
}