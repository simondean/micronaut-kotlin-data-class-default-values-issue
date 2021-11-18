package com.example.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class Example(
    val noneWithDefaults: IntrospectedDataClassConstructorPropertiesNoneWithDefaults,
    val someWithDefaults: IntrospectedDataClassConstructorPropertiesSomeWithDefaults,
    val allWithDefaults: IntrospectedDataClassConstructorPropertiesAllWithDefaults
)
