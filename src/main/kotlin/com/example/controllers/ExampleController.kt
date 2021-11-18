package com.example.controllers

import com.example.models.Example
import com.example.services.ExampleService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.coroutineScope

@Controller("/examples")
class ExampleController(
    val exampleService: ExampleService
) {

    @Post
    suspend fun createExample(
        @Body body: Example
    ): HttpStatus = coroutineScope {
        exampleService.save(body)
        HttpStatus.CREATED
    }

}