package com.dangerfield.spyfall

import androidx.annotation.VisibleForTesting
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun getVoidTask(): Task<Void> {
    val dummy = Tasks.forResult(true)
    return Tasks.whenAll(dummy)
}