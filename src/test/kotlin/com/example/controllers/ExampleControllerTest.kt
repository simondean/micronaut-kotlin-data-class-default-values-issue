package com.example.controllers

import com.example.models.Example
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
import javax.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

@MicronautTest
class ExampleControllerTest {

    val micronautVersion = System.getenv("MICRONAUT_VERSION")
    val micronautMajorVersion = micronautVersion.substringBefore(".").toInt()

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
                "propertyWithNullDefault": null
            }
        """.trimIndent()

        val request = POST("/examples", jsonRequestBody)
        val response = client
            .toBlocking()
            .exchange(request, String::class.java)

        assertThat(response.code()).isEqualTo(201)

        val examples = fakeExampleService.examples
        assertThat(examples).hasSize(1)
        val example = examples[0]

        assertThat(example).isEqualTo(when (micronautMajorVersion) {
            2 -> Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
            3 -> Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
    }

    @Test
    fun dataClassDefaultValuesShouldBeUsedByMicronautObjectMapper() {

        val jsonRequestBody = """
            {
                "propertyWithNullDefault": null
            }
        """.trimIndent()

        val example = micronautObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example).isEqualTo(when (micronautMajorVersion) {
            2 -> Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
            3 -> Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
    }

    @Test
    fun dataClassDefaultValuesShouldBeUsedByAnIndependentObjectMapperUsingOnlyKotlinModule() {

        val independentObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())

        val jsonRequestBody = """
            {
                "propertyWithNullDefault": null
            }
        """.trimIndent()

        val example = independentObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example).isEqualTo(when (micronautMajorVersion) {
            2 -> Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
            3 -> Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
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
                "propertyWithNullDefault": null
            }
        """.trimIndent()

        val example = independentObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example).isEqualTo(when (micronautMajorVersion) {
            2 -> Example(
                propertyWithNonNullDefault = null,
                propertyWithNullDefault = null
            )
            3 -> Example(
                propertyWithNonNullDefault = 0,
                propertyWithNullDefault = null
            )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
    }

}