package com.example.services

import com.example.models.Example

open class FakeExampleService : ExampleService {

    private val mutableExamples = mutableListOf<Example>()
    val examples: List<Example> = mutableExamples

    override fun save(example: Example) {
        mutableExamples.add(example)
    }

}