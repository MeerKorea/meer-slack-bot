import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File
import java.nio.file.Paths
import java.util.*

data class Body(val challenge: String)
data class SlackPostMessage(val channel: String, val text: String)

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
            post("/") {
                val requestBody = call.receiveText()
                if (getValueOf(requestBody, "event", "bot_id").isPresent) {
                    return@post
                }

                println(getValueOf(requestBody, "event", "text").get())

                val message =
                    SlackPostMessage(channel = "C011J0E62TF", text = getValueOf(requestBody, "event", "text").get())

                val key = File(Paths.get("src", "main", "resources", "key.txt").toUri())
                    .readLines()

                println("key" + key[0])
                val client = HttpClient()
                client.post<Unit> {
                    header("Content-Type", "application/json")
                    header("Authorization", key[0])
                    url("https://slack.com/api/chat.postMessage")
                    body = Gson().toJson(message)
                }

                call.response.status(HttpStatusCode.OK)
            }
//            // for verify
//            post("/") {
//                val receiveParameters = call.receiveText()
//                val challenge = Gson().fromJson(receiveParameters, Body::class.java).challenge
//
//                val jsonObject = JsonObject()
//                jsonObject.addProperty("challenge", challenge)
//                call.respond(Gson().toJson(jsonObject))
//            }
        }
    }.start(wait = true)
}

fun getValueOf(body: String, vararg keys: String): Optional<String> {
    val parser = JsonParser()
    var element: JsonElement = parser.parse(body)
    for (key in keys) {
        val jsonObject = element.asJsonObject
        if (!jsonObject.has(key)) {
            return Optional.empty()
        }
        element = jsonObject[key]
    }
    return Optional.of(element.asString)
}