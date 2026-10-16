package io.github.hero_premium.tools

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalculatorTest {
    @Test
    fun useTool() {
        val calculator = Calculator()
        var result = calculator.toolUse(mapOf<String, Any>("expression" to "2 + 2"))
        assertEquals("4", result)
        result = calculator.toolUse(mapOf<String, Any>("expression" to "NonSense"))
        assertEquals("Cannot evaluate expression: Variable or constant value for 'NonSense' not found", result)
        result = calculator.toolUse(mapOf<String, Any>("expression" to "10 / 0"))
        assertEquals("Cannot evaluate expression: Division by zero", result)

        // weird spaces
        result = calculator.toolUse(mapOf<String, Any>("expression" to "1+ 3 +33 *15        "))
        assertEquals("499", result)

    }

}