package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class IntrospectedDataClassConstructorPropertiesAllWithDefaults(
    val propertyWithNullDefault: Int? = null,
    val propertyWithNonNullDefault: Int? = 0
)
