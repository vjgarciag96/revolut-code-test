package com.vjgarcia.currencies.domain

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test


class CurrencyDecimalTest {

    @Test
    internal fun `to plain string removes trailing zeros`() {
        assertEquals("196", CurrencyDecimal.fromDouble(196.0000).toPlainString())
        assertEquals("280.01", CurrencyDecimal.fromDouble(280.0100).toPlainString())
    }
}