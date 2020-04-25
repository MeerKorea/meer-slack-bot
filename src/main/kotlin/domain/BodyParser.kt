package domain

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class BodyParser constructor(private val body: String) {
    private val parser = JsonParser()

    fun getValueOf(vararg keys: String): String? {
        return try {
            getValueFrom(*keys)
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun getValueFrom(vararg keys: String): String? {
        var element: JsonElement = parser.parse(body)
        for (key in keys) {
            val jsonObject: JsonObject = element.asJsonObject
            element = jsonObject[key]
        }
        return element.asString
    }
}