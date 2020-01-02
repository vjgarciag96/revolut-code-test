package com.vjgarcia.currencies.domain

import arrow.core.Either
import com.vjgarcia.currencies.data.repository.LatestRatesData
import com.vjgarcia.currencies.data.repository.RatesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

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

    @Test
    internal fun `onCurrencyChanged emits the correspondent BaseCurrencyUpdated event`() {
        givenAnyLatestRates()

        val testObserver = sut.currenciesStream().test()
        sut.onCurrencyChanged(ANY_NEW_BASE)

        verify {
            currenciesReducerMock.reduce(
                CurrenciesEvent.BaseCurrencyUpdated(ANY_NEW_BASE),
                CurrenciesState.Initializing
            )
        }
        testObserver.dispose()
    }

    @Test
    internal fun `onAmountChange emits the correspondent BaseAmountUpdated event`() {
        givenAnyLatestRates()

        val testObserver = sut.currenciesStream().test()
        sut.onAmountChanged(ANY_AMOUNT)

        verify {
            currenciesReducerMock.reduce(
                CurrenciesEvent.BaseAmountUpdated(ANY_AMOUNT),
                CurrenciesState.Initializing
            )
        }
        testObserver.dispose()
    }

    private fun givenAnyLatestRates() {
        every { ratesRepositoryMock.latestRates() } returns Either.right(anyLatestRates())
    }

    private fun anyLatestRates(): LatestRatesData = LatestRatesData("EUR", emptyMap())

    private companion object {
        const val ANY_NEW_BASE = "GBP"
        val ANY_AMOUNT = CurrencyDecimal.fromDouble(1.0)
    }
}