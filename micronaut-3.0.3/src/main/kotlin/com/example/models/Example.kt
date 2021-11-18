package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class Example(
    val propertyWithNonNullDefault: Int? = 0,
    val propertyWithNullDefault: Int? = null
)
