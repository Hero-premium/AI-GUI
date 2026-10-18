package io.github.hero_premium.tools

import com.ezylang.evalex.EvaluationException
import com.ezylang.evalex.Expression
import com.ezylang.evalex.parser.ParseException

class Calculator :
    Tool(
        "Calculator",
        """
            this tool can help you with math, from basic math to complex problems, always use it for anything involving math,
             and do not use it for anything the user does it mention math in for
            """,
        Param("expression", PropertiesType.STRING, "the expression you wish to calculate", true)
    ) {


    override fun useTool(arguments: Map<String?, Any>): String {
        val expression = arguments["expression"] as String
        return try {
            Expression(expression).evaluate().value.toString()
        } catch (e: ParseException) {
            "Invalid expression: ${e.message}"
        } catch (e: EvaluationException) {
            "Cannot evaluate expression: ${e.message}"
        }
    }
}