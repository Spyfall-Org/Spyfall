package com.dangerfield.features.inAppMessaging.internal

import androidx.datastore.core.DataStore
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.internal.update.DaysBetweenInAppUpdateMessages
import com.dangerfield.features.inAppMessaging.internal.update.GetAppUpdateInfo
import com.dangerfield.features.inAppMessaging.internal.update.GetInAppUpdateAvailabilityImpl
import com.dangerfield.features.inAppMessaging.internal.update.InAppUpdateMessage
import com.dangerfield.libraries.test.isFailure
import com.dangerfield.libraries.test.isSuccess
import com.google.android.play.core.install.model.InstallStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import oddoneout.core.BuildInfo
import oddoneout.core.BuildType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class GetInAppUpdateAvailabilityImplTest {

    private val fakeInAppUpdateManager = spyk<FakeInAppUpdateManager>()
    @get:Rule
    val fakeInAppUpdateManagerRule = FakeInAppUpdateManagerRule(fakeInAppUpdateManager)

    private val seenInAppUpdateMessages: DataStore<List<InAppUpdateMessage>> = mockk()
    private val clock: Clock = Clock.fixed(Instant.parse("2023-07-11T02:38:00Z"), ZoneOffset.UTC)
    private val daysBetweenInAppUpdateMessages = mockk<DaysBetweenInAppUpdateMessages>().also {
        coEvery { it.invoke() } returns 0
    }
    private val getAppUpdateInfo = mockk<GetAppUpdateInfo>().also {
        coEvery { it.invoke() } answers {
            Try.Success(fakeInAppUpdateManager.fakeAppUpdateInfo()!!)
        }
    }

    private lateinit var getInAppUpdateAvailabilityImpl: GetInAppUpdateAvailabilityImpl
    private val buildInfo: BuildInfo = BuildInfo(
        versionCode = 1329,
        versionName = "1.3.2",
        packageName = "com.dangerfield.oddoneout",
        buildType = BuildType.RELEASE,
        deviceName = "Pixel"
    )

    @Before
    fun setup() {
        every { seenInAppUpdateMessages.data } returns flowOf(emptyList())

        getInAppUpdateAvailabilityImpl = GetInAppUpdateAvailabilityImpl(
            getAppUpdateInfo,
            seenInAppUpdateMessages,
            clock,
            daysBetweenInAppUpdateMessages,
            buildInfo
        )
    }

    @Test
    fun `GIVEN getting app info fails, WHEN getting availability, THEN return failure`() =
        runTest {
            coEvery { getAppUpdateInfo.invoke() } returns Try.failure(Exception("Failed to get app update info"))

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isFailure()
        }

    @Test
    fun `GIVEN no update is available, WHEN getting availability, THEN return matching status`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = false,
                updatePriority = 4
            )

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()
            assertThat(result.getOrThrow()).isInstanceOf(InAppUpdateAvailability.NoUpdateAvailable::class)
        }


    @Test
    fun `GIVEN install status is downloaded, When priority was 5, THEN return UpdateReadyToInstall`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5,
                installStatus = InstallStatus.DOWNLOADED
            )
            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateReadyToInstall::class)
                .prop(InAppUpdateAvailability.UpdateReadyToInstall::wasDownloadedInBackground)
                .isFalse()
        }

    @Test
    fun `GIVEN install status downloading, When getting availability, THEN return such`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5,
                installStatus = InstallStatus.DOWNLOADING
            )
            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow()).isInstanceOf(InAppUpdateAvailability.UpdateInProgress::class)
        }

    @Test
    fun `GIVEN install status pending, When getting availability, THEN return such`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5,
                installStatus = InstallStatus.PENDING
            )
            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow()).isInstanceOf(InAppUpdateAvailability.UpdateInProgress::class)
        }

    @Test
    fun `GIVEN install status is downloaded, When priority was less than 5, THEN return UpdateReadyToInstall`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 3,
                installStatus = InstallStatus.DOWNLOADED
            )

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateReadyToInstall::class)
                .prop(InAppUpdateAvailability.UpdateReadyToInstall::wasDownloadedInBackground)
                .isTrue()
        }

    @Test
    fun `GIVEN message has not been seen, When update available, THEN should show`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 3,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(emptyList())

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isTrue()
        }

    @Test
    fun `GIVEN should show, When priority is below 5, THEN foreground false`() =
        runTest {

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 3,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(emptyList())

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isTrue()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::isForegroundUpdate)
                .isFalse()
        }

    @Test
    fun `GIVEN should show, When priority is 5, THEN foreground true`() =
        runTest {
            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 5,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(emptyList())

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isTrue()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::isForegroundUpdate)
                .isTrue()
        }

    @Test
    fun `GIVEN update available and message already seen, When required time not elapsed, THEN show false`() =
        runTest {
            every { daysBetweenInAppUpdateMessages() } returns 5

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 3,
                versionCode = 99,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(listOf(
                InAppUpdateMessage(99, clock.instant().minus(Duration.ofDays(4)))
            ))

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isFalse()
        }

    @Test
    fun `GIVEN update available and message already seen, When required time elapsed, THEN show true`() =
        runTest {
            every { daysBetweenInAppUpdateMessages() } returns 5

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 3,
                versionCode = 99,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(listOf(
                InAppUpdateMessage(99, clock.instant().minus(Duration.ofDays(6)))
            ))

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isTrue()
        }

    @Test
    fun `GIVEN update available, When ignorable, THEN return should show false`() =
        runTest {
            every { daysBetweenInAppUpdateMessages() } returns 5

            fakeInAppUpdateManager.setAppUpdateInfo(
                updateAvailable = true,
                updatePriority = 0,
                versionCode = 99,
                installStatus = InstallStatus.UNKNOWN
            )

            every { seenInAppUpdateMessages.data } returns flowOf(listOf(
                InAppUpdateMessage(99, clock.instant().minus(Duration.ofDays(6)))
            ))

            val result = getInAppUpdateAvailabilityImpl()

            assertThat(result).isSuccess()

            assertThat(result.getOrThrow())
                .isInstanceOf(InAppUpdateAvailability.UpdateAvailable::class)
                .prop(InAppUpdateAvailability.UpdateAvailable::shouldShow)
                .isFalse()
        }
}