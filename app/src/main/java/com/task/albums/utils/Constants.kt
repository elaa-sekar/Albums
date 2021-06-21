package com.task.albums.utils

object ViewType {
    const val GRID = 1
    const val LIST = 2
}

object SortType {
    const val DEFAULT = 1
    const val TITLE_ASC = 2
    const val TITLE_DESC = 3
    const val USER_NAME_ASC = 4
    const val USER_NAME_DESC = 5
}

object FilterType {
    const val NONE = 1
    const val FAVORITES_ONLY = 2
    const val NON_FAVORITES_ONLY = 3
}

object EventType {
    const val ERROR = 0
    const val SUCCESS = 1
}
