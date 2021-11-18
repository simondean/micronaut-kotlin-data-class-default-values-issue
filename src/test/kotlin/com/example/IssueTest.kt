package com.example

import com.example.models.Example
import com.example.models.IntrospectedDataClassConstructorPropertiesAllWithDefaults
import com.example.models.IntrospectedDataClassConstructorPropertiesNoneWithDefaults
import com.example.models.IntrospectedDataClassConstructorPropertiesSomeWithDefaults
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
class IssueTest {

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
    fun dataClassDefaultValuesShouldBeUsedByMicronautObjectMapper() {

        val jsonRequestBody = """
            {
                "noneWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "someWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "allWithDefaults": {
                    "propertyWithNullDefault": 99
                }
            }
        """.trimIndent()

        val example = micronautObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example.noneWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesNoneWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNoDefault2 = null
            )
        )
        assertThat(example.someWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesSomeWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNonNullDefault = null
            )
        )
        assertThat(example.allWithDefaults).isEqualTo(when (micronautMajorVersion) {
            2 -> 
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
                )
            3 ->
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
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
                "noneWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "someWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "allWithDefaults": {
                    "propertyWithNullDefault": 99
                }
            }
        """.trimIndent()

        val example = independentObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example.noneWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesNoneWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNoDefault2 = null
            )
        )
        assertThat(example.someWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesSomeWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNonNullDefault = 0
            )
        )
        assertThat(example.allWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                propertyWithNullDefault = 99,
                propertyWithNonNullDefault = 0
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
                "noneWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "someWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "allWithDefaults": {
                    "propertyWithNullDefault": 99
                }
            }
        """.trimIndent()

        val example = independentObjectMapper.readValue(jsonRequestBody, Example::class.java)

        assertThat(example.noneWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesNoneWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNoDefault2 = null
            )
        )
        assertThat(example.someWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesSomeWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNonNullDefault = null
            )
        )
        assertThat(example.allWithDefaults).isEqualTo(when (micronautMajorVersion) {
            2 -> 
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
                )
            3 ->
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
                )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
    }

    @Test
    fun dataClassDefaultValuesShouldBeUsedForRequestBodyDeserialisation() {

        val jsonRequestBody = """
            {
                "noneWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "someWithDefaults": {
                    "propertyWithNoDefault": 99
                },
                "allWithDefaults": {
                    "propertyWithNullDefault": 99
                }
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

        assertThat(example.noneWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesNoneWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNoDefault2 = null
            )
        )
        assertThat(example.someWithDefaults).isEqualTo(
            IntrospectedDataClassConstructorPropertiesSomeWithDefaults(
                propertyWithNoDefault = 99,
                propertyWithNonNullDefault = null
            )
        )
        assertThat(example.allWithDefaults).isEqualTo(when (micronautMajorVersion) {
            2 -> 
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
                )
            3 ->
                IntrospectedDataClassConstructorPropertiesAllWithDefaults(
                    propertyWithNullDefault = 99,
                    propertyWithNonNullDefault = 0
                )
            else -> fail("Unexpected Micronaut major version $micronautVersion")
        })
    }

}
