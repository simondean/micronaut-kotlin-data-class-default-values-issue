package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class IntrospectedDataClassConstructorPropertiesNoneWithDefaults(
    val propertyWithNoDefault: Int?,
    val propertyWithNoDefault2: Int?
)
