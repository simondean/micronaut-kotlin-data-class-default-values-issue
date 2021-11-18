package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class IntrospectedDataClassConstructorPropertiesSomeWithDefaults(
    val propertyWithNoDefault: Int?,
    val propertyWithNonNullDefault: Int? = 0
)
