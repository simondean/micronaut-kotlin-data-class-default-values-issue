package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class Example(
    val propertyWithDefault: Int? = 0,
    val propertyWithoutDefault: Int?,
    val unrelated: Int?
)
