package com.example.controllers

import com.example.models.Example
import com.example.services.FakeExampleService
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@MicronautTest
class ExampleControllerTest {

    @Inject
    lateinit var exampleController: ExampleController
    @Inject
    lateinit var fakeExampleService: FakeExampleService
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @MockBean(FakeExampleService::class)
    fun exampleService() = FakeExampleService()

    @Test
    fun dataClassDefaultValuesShouldBeIgnoredForRequestBodyDeserialisation() {

        val jsonRequestBody = """
            {
                "example": {                
                    "unrelated": 1
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
                propertyWithDefault = null,
                propertyWithoutDefault = null,
                unrelated = 1
            )
        )
    }


}