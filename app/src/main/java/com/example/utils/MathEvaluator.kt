package com.example.utils

import kotlin.math.*

object MathEvaluator {

    fun evaluate(expression: String, isRadians: Boolean = false): Result<Double> {
        return try {
            val sanitized = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("π", Math.PI.toString())
                .replace("e", Math.E.toString())
                .replace("—", "-")

            val result = evaluateExpression(sanitized, isRadians)
            if (result.isNaN() || result.isInfinite()) {
                Result.failure(ArithmeticException("Undefined result"))
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun evaluateExpression(expr: String, isRadians: Boolean): Double {
        val tokens = tokenize(expr)
        val postfix = infixToPostfix(tokens)
        return evaluatePostfix(postfix, isRadians)
    }

    private sealed class Token {
        data class Num(val value: Double) : Token()
        data class Op(val symbol: String, val precedence: Int, val isRightAssoc: Boolean = false) : Token()
        data class Func(val name: String) : Token()
        object LParen : Token()
        object RParen : Token()
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = expr.length
        while (i < len) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(Token.Num(sb.toString().toDouble()))
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < len && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(Token.Func(sb.toString().lowercase()))
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%' -> {
                    if (c == '-' && (tokens.isEmpty() || tokens.last() is Token.LParen || (tokens.last() is Token.Op))) {
                        i++
                        if (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                            val sb = StringBuilder("-")
                            while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                                sb.append(expr[i])
                                i++
                            }
                            tokens.add(Token.Num(sb.toString().toDouble()))
                        } else {
                            tokens.add(Token.Op("u-", 4, true))
                        }
                    } else {
                        val prec = when (c) {
                            '+', '-' -> 1
                            '*', '/' -> 2
                            '^', '%' -> 3
                            else -> 1
                        }
                        tokens.add(Token.Op(c.toString(), prec))
                        i++
                    }
                }
                c == '(' -> {
                    tokens.add(Token.LParen)
                    i++
                }
                c == ')' -> {
                    tokens.add(Token.RParen)
                    i++
                }
                else -> i++
            }
        }
        return tokens
    }

    private fun infixToPostfix(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = mutableListOf<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Num -> output.add(token)
                is Token.Func -> stack.add(token)
                is Token.Op -> {
                    while (stack.isNotEmpty() && stack.last() is Token.Op) {
                        val top = stack.last() as Token.Op
                        if ((!token.isRightAssoc && token.precedence <= top.precedence) ||
                            (token.isRightAssoc && token.precedence < top.precedence)) {
                            output.add(stack.removeAt(stack.size - 1))
                        } else {
                            break
                        }
                    }
                    stack.add(token)
                }
                is Token.LParen -> stack.add(token)
                is Token.RParen -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LParen) {
                        output.add(stack.removeAt(stack.size - 1))
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.LParen) {
                        stack.removeAt(stack.size - 1)
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.Func) {
                        output.add(stack.removeAt(stack.size - 1))
                    }
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.removeAt(stack.size - 1))
        }
        return output
    }

    private fun evaluatePostfix(postfix: List<Token>, isRadians: Boolean): Double {
        val stack = mutableListOf<Double>()

        for (token in postfix) {
            when (token) {
                is Token.Num -> stack.add(token.value)
                is Token.Op -> {
                    if (token.symbol == "u-") {
                        if (stack.isEmpty()) throw IllegalArgumentException("Invalid expression")
                        val a = stack.removeAt(stack.size - 1)
                        stack.add(-a)
                    } else if (token.symbol == "%") {
                        if (stack.isEmpty()) throw IllegalArgumentException("Invalid expression")
                        val a = stack.removeAt(stack.size - 1)
                        stack.add(a / 100.0)
                    } else {
                        if (stack.size < 2) throw IllegalArgumentException("Invalid expression")
                        val b = stack.removeAt(stack.size - 1)
                        val a = stack.removeAt(stack.size - 1)
                        val res = when (token.symbol) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> {
                                if (b == 0.0) throw ArithmeticException("Division by zero")
                                a / b
                            }
                            "^" -> a.pow(b)
                            else -> 0.0
                        }
                        stack.add(res)
                    }
                }
                is Token.Func -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeAt(stack.size - 1)
                    val angle = if (isRadians) a else Math.toRadians(a)
                    val res = when (token.name) {
                        "sin" -> sin(angle)
                        "cos" -> cos(angle)
                        "tan" -> tan(angle)
                        "asin" -> {
                            val r = asin(a)
                            if (isRadians) r else Math.toDegrees(r)
                        }
                        "acos" -> {
                            val r = acos(a)
                            if (isRadians) r else Math.toDegrees(r)
                        }
                        "atan" -> {
                            val r = atan(a)
                            if (isRadians) r else Math.toDegrees(r)
                        }
                        "sqrt" -> {
                            if (a < 0) throw ArithmeticException("Square root of negative number")
                            sqrt(a)
                        }
                        "log" -> {
                            if (a <= 0) throw ArithmeticException("Logarithm of non-positive number")
                            log10(a)
                        }
                        "ln" -> {
                            if (a <= 0) throw ArithmeticException("Natural log of non-positive number")
                            ln(a)
                        }
                        "abs" -> abs(a)
                        "fact" -> {
                            if (a < 0 || a != a.toInt().toDouble()) throw ArithmeticException("Factorial of non-integer")
                            factorial(a.toInt())
                        }
                        else -> throw IllegalArgumentException("Unknown function ${token.name}")
                    }
                    stack.add(res)
                }
                else -> {}
            }
        }
        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        return stack[0]
    }

    private fun factorial(n: Int): Double {
        if (n > 170) return Double.POSITIVE_INFINITY
        var f = 1.0
        for (i in 1..n) {
            f *= i
        }
        return f
    }
}
