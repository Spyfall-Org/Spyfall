package com.dangerfield.features.inAppMessaging.internal

import assertk.assertThat
import com.dangerfield.features.inAppMessaging.internal.update.CompleteInAppUpdateImpl
import com.dangerfield.libraries.test.isFailure
import com.dangerfield.libraries.test.isSuccess
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CompleteInAppUpdateImplTest {

    private val fakeInAppUpdateManager = spyk<FakeInAppUpdateManager>()

    @get:Rule
    val fakeInAppUpdateManagerRule = FakeInAppUpdateManagerRule(fakeInAppUpdateManager)

    private val completeInAppUpdateImpl = CompleteInAppUpdateImpl(fakeInAppUpdateManager)

    @Test
    fun `GIVEN complete fails, WHEN invoking, THEN return failure`() = runTest {
        fakeInAppUpdateManager.completeFails()

        val result = completeInAppUpdateImpl()

        assertThat(result).isFailure()
    }

    @Test
    fun `GIVEN complete succeeds, WHEN invoking, THEN return success`() = runTest {
        fakeInAppUpdateManager.completeSucceeds()

        val result = completeInAppUpdateImpl()

        assertThat(result).isSuccess()
    }
}
