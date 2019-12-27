package com.vjgarcia.rates.data

inline fun <reified T> getFileAsStringFromResources(resName: String): String =
    T::class.java.getResource("/$resName")?.readText()
        ?: error("unable to get resource $resName")