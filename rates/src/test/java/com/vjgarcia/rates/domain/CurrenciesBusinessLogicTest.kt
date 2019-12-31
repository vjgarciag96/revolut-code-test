package com.vjgarcia.rates.domain

import arrow.core.Either
import com.vjgarcia.rates.data.repository.LatestRatesData
import com.vjgarcia.rates.data.repository.RatesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CurrenciesBusinessLogicTest {

    private val testScheduler = TestScheduler()

    private val ratesRepositoryMock = mockk<RatesRepository>()
    private val currenciesReducerMock = mockk<CurrenciesReducer>(relaxed = true)

    private val sut = CurrenciesBusinessLogic(
        ratesRepository = ratesRepositoryMock,
        currenciesReducer = currenciesReducerMock
    )

    @BeforeEach
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
        RxAndroidPlugins.setMainThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { testScheduler }
    }

    @AfterEach
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    internal fun `currencies stream starts reducing Initializing status plus RatesUpdated event`() {
        givenAnyLatestRates()

        val testObserver = sut.currenciesStream().test()

        verify {
            currenciesReducerMock.reduce(
                CurrenciesEvent.RatesUpdated(anyLatestRates().toDomainModel()),
                CurrenciesState.Initializing
            )
        }
        testObserver.dispose()
    }

    private fun givenAnyLatestRates() {
        every { ratesRepositoryMock.latestRates() } returns Either.right(anyLatestRates())
    }

    private fun anyLatestRates(): LatestRatesData = LatestRatesData("EUR", emptyMap())
}