package com.jesushz.wear.run.presentation

import com.jesushz.wear.run.domain.ExerciseError
import com.jesushzc.core.presentation.ui.UiText

fun ExerciseError.toUiText(): UiText? {
    return when (this) {
        ExerciseError.ONGOING_OWN_EXERCISE,
        ExerciseError.ONGOING_OTHER_EXERCISE -> UiText.StringResource(R.string.error_ongoing_exercise)
        ExerciseError.EXERCISE_ALREADY_ENDED -> UiText.StringResource(R.string.error_exercise_already_ended)
        ExerciseError.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
        ExerciseError.TRACKING_NOT_SUPPORTED -> null
    }
}