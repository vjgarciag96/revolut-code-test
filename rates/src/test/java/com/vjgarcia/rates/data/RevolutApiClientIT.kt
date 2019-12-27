package com.vjgarcia.rates.data

import arrow.core.orNull
import arrow.core.right
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RevolutApiClientIT {

    private val mockWebServer = MockWebServer()

    private val okHttpClient = OkHttpClient.Builder().build()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val revolutHttpUrlFactory = RevolutHttpUrlFactory()

    private lateinit var sut: RevolutApiClient

    @BeforeEach
    internal fun setUp() {
        mockWebServer.start()
        val mockWebServerUrl = mockWebServer.url("").toString()

        val revolutApiConfig = RevolutApiConfig(
            baseEndpoint = mockWebServerUrl,
            latestRatesPath = "latest"
        )

        sut = RevolutApiClient(okHttpClient, moshi, revolutApiConfig, revolutHttpUrlFactory)
    }

    @AfterEach
    internal fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    internal fun `latestRates happy case`() {
        givenAValidLatestRatesResponse()

        val latestRatesResult = sut.latestRates()

        assertTrue(latestRatesResult.isRight())
        assertEquals(expectedLatestRates(), latestRatesResult.orNull())
    }

    private fun givenAValidLatestRatesResponse() {
        val latestRatesResponse = getFileAsStringFromResources<RevolutApiClientIT>("LatestRatesValidResponse.json")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(latestRatesResponse)
        )
    }

    private fun expectedLatestRates(): LatestRatesDTO = LatestRatesDTO(
        base = "EUR",
        date = "2018-09-06",
        rates = mapOf(
            "AUD" to 1.613,
            "BGN" to 1.9516,
            "BRL" to 4.7816,
            "CAD" to 1.5305,
            "CHF" to 1.1251,
            "CNY" to 7.9282,
            "CZK" to 25.66,
            "DKK" to 7.4408,
            "GBP" to 0.89633,
            "HKD" to 9.1129,
            "HRK" to 7.4183,
            "HUF" to 325.79,
            "IDR" to 17287.0,
            "ILS" to 4.1617,
            "INR" to 83.539,
            "ISK" to 127.53,
            "JPY" to 129.27,
            "KRW" to 1302.0,
            "MXN" to 22.318,
            "MYR" to 4.8017,
            "NOK" to 9.7552,
            "NZD" to 1.7595,
            "PHP" to 62.459,
            "PLN" to 4.3091,
            "RON" to 4.6286,
            "RUB" to 79.405,
            "SEK" to 10.568,
            "SGD" to 1.5966,
            "THB" to 38.049,
            "TRY" to 7.612,
            "USD" to 1.1609,
            "ZAR" to 17.785
        )
    )
}