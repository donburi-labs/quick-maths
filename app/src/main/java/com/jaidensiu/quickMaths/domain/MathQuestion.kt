package com.jaidensiu.quickMaths.domain

import kotlin.random.Random

enum class Operation(val symbol: String) {
    ADDITION(symbol = "+"),
    SUBTRACTION(symbol = "−"),
    MULTIPLICATION(symbol = "×"),
    DIVISION(symbol = "÷"),
}

data class MathQuestion(
    val left: Int,
    val right: Int,
    val operation: Operation,
) {
    val answer: Int = when (operation) {
        Operation.ADDITION -> left + right
        Operation.SUBTRACTION -> left - right
        Operation.MULTIPLICATION -> left * right
        Operation.DIVISION -> left / right
    }

    val text: String = "$left ${operation.symbol} $right ="

    companion object {
        fun random(random: Random = Random.Default): MathQuestion =
            when (Operation.entries.random(random)) {
                Operation.ADDITION -> MathQuestion(
                    left = random.nextInt(from = 1, until = 21),
                    right = random.nextInt(from = 1, until = 21),
                    operation = Operation.ADDITION,
                )

                Operation.SUBTRACTION -> {
                    // Keep the result non-negative
                    val a = random.nextInt(from = 1, until = 21)
                    val b = random.nextInt(from = 1, until = 21)
                    MathQuestion(
                        left = maxOf(a, b),
                        right = minOf(a, b),
                        operation = Operation.SUBTRACTION,
                    )
                }

                Operation.MULTIPLICATION -> MathQuestion(
                    left = random.nextInt(from = 2, until = 10),
                    right = random.nextInt(from = 2, until = 10),
                    operation = Operation.MULTIPLICATION,
                )

                Operation.DIVISION -> {
                    // Build from divisor × quotient so the answer is a whole number
                    val divisor = random.nextInt(from = 2, until = 10)
                    val quotient = random.nextInt(from = 2, until = 10)
                    MathQuestion(
                        left = divisor * quotient,
                        right = divisor,
                        operation = Operation.DIVISION,
                    )
                }
            }
    }
}
