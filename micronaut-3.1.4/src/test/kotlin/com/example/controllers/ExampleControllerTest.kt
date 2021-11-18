package com.example.controllers

import com.example.models.Example
import com.example.models.ExampleRequestBody
import com.example.services.FakeExampleService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.jackson.modules.BeanIntrospectionModule
import io.micronaut.jackson.serialize.ResourceModule
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@MicronautTest
class ExampleControllerTest {

    @Inject
    lateinit var fakeExampleService: FakeExampleService
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient
    @Inject
    lateinit var micronautObjectMapper: ObjectMapper

    @MockBean(FakeExampleService::class)
    fun exampleService() = FakeExampleService()

    @Test
    fun dataClassDefaultValuesShouldBeUsedForRequestBodyDeserialisation() {

        val jsonRequestBody = """
            {
                "example": {
                    "propertyWithNullDefault": null
                }
            }
        """.trimIndent()

        val request = POST("/examples", jsonRequestBody)
        val response = client
            .toBlocking()
            .exchange(request, String::class.java)

        assertThat(response.code()).isEqualTo(201)

        val examples = fakeExampleService.examples
        assertThat(examples).containsExactly(
            Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
        )
    }

    @Test
    fun dataClassDefaultValuesShouldBeUsedByMicronautObjectMapper() {

        val jsonRequestBody = """
            {
                "example": {
                    "propertyWithNullDefault": null
                }
            }
        """.trimIndent()

        val requestBody = micronautObjectMapper.readValue(jsonRequestBody, ExampleRequestBody::class.java)

        assertThat(requestBody.example).isEqualTo(
            Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
        )
    }

    @Test
    fun dataClassDefaultValuesShouldBeUsedByAnIndependentObjectMapperUsingOnlyKotlinModule() {

        val independentObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())

        val jsonRequestBody = """
            {
                "example": {
                    "propertyWithNullDefault": null
                }
            }
        """.trimIndent()

        val requestBody = independentObjectMapper.readValue(jsonRequestBody, ExampleRequestBody::class.java)

        assertThat(requestBody.example).isEqualTo(
            Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
        )
    }

    @Test
    fun dataClassDefaultValuesShouldNotBeUsedByAnIndependentObjectMapperUsingMicronautModules() {

        val independentObjectMapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(KotlinModule())
            .registerModule(BeanIntrospectionModule())
            .registerModule(ResourceModule())

        val jsonRequestBody = """
            {
                "example": {
                    "propertyWithNullDefault": null
                }
            }
        """.trimIndent()

        val requestBody = independentObjectMapper.readValue(jsonRequestBody, ExampleRequestBody::class.java)

        assertThat(requestBody.example).isEqualTo(
            Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
        )
    }

}