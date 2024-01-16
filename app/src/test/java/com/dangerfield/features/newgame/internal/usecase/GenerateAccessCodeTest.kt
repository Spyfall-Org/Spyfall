package com.dangerfield.features.newgame.internal.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.test.isFailure
import com.dangerfield.libraries.test.isSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import oddoneout.core.Try

class GenerateAccessCodeTest {

    private val gameRepository: GameRepository = mockk()
    private val gameConfig: GameConfig = mockk {
        every { accessCodeLength } returns 6
    }
    private lateinit var generateAccessCode: GenerateAccessCode

    @Before
    fun setUp() {
        generateAccessCode = GenerateAccessCode(gameRepository, gameConfig)
    }

    @Test
    fun `GIVEN no game exists with generated code WHEN generating access code THEN success should return`() = runTest {
        coEvery { gameRepository.doesGameExist(any()) } returns Try.just(false)

        val result = generateAccessCode.invoke()

        assertThat(result).isSuccess()
    }

    @Test
    fun `GIVEN game config WHEN generating access code THENlength should match config`() = runTest {
        every { gameConfig.accessCodeLength } returns 5
        coEvery { gameRepository.doesGameExist(any()) } returns Try.just(false)

        val result = generateAccessCode.invoke()

        assertThat(result).isSuccess()
        assertThat(result.getOrThrow().length).isEqualTo(5)
    }

    @Test
    fun `GIVEN generated code already exists WHEN generating access code THEN new code should be generated`() = runTest {
        coEvery { gameRepository.doesGameExist(any()) } returns Try.just(true) andThen Try.just(false)

        val result = generateAccessCode.invoke()

        assertThat(result).isSuccess()
        assertThat(result.getOrThrow().length).isEqualTo(6)
        coVerify(exactly = 2) { gameRepository.doesGameExist(any()) }
    }

    @Test
    fun `GIVEN error checking existing game WHEN generating access code THEN failure should return`() = runTest {
        val exception = RuntimeException("Database error")
        coEvery { gameRepository.doesGameExist(any()) } returns Try.raise(exception)

        val result = generateAccessCode.invoke()

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull()).isEqualTo(exception)
    }
}
