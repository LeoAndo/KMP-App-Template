package com.jetbrains.kmpapp.screens.githubsearch

internal enum class SortType(val sort: String) {
    STARS("stars"),
    FORKS("forks"),
    HELP_WANTED_ISSUES("help-wanted-issues"),
    UPDATED("updated");
}