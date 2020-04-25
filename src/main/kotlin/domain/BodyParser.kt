package domain

import com.google.gson.JsonElement
import com.google.gson.JsonParser

class BodyParser constructor(private val body: String) {
    private val parser = JsonParser()

    fun getValueOf(vararg keys: String): String? {
        var element: JsonElement = parser.parse(body)
        for (key in keys) {
            val jsonObject = element.asJsonObject
            if (!jsonObject.has(key)) {
                return null
            }
            element = jsonObject[key]
        }
        return element.asString
    }

}