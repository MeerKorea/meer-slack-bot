import com.google.gson.Gson
import com.google.gson.JsonObject
import domain.BodyParser
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

data class Body(val challenge: String)
data class SlackPostMessage(val channel: String, val text: String)

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
            // for verify
//            post("/") {
//                val receiveParameters = call.receiveText()
//                val challenge = Gson().fromJson(receiveParameters, Body::class.java).challenge
//
//                val jsonObject = JsonObject()
//                jsonObject.addProperty("challenge", challenge)
//                call.respond(Gson().toJson(jsonObject))
//            }
            post("/") {
                val bodyParser = BodyParser(call.receiveText())
                if (bodyParser.getValueOf("event", "bot_id") != null) {
                    return@post
                }

                val text = bodyParser.getValueOf("event", "text")

                val channel = File(Paths.get("src", "main", "resources", "channel.txt").toUri())
                    .readLines()[0]
                val message =
                    SlackPostMessage(channel = channel, text = text ?: "텍스트가 없습니다")

                val key = File(Paths.get("src", "main", "resources", "key.txt").toUri())
                    .readLines()[0]

                println("key" + key[0])
                val client = HttpClient()
                client.post<Unit> {
                    header("Content-Type", "application/json")
                    header("Authorization", key)
                    url("https://slack.com/api/chat.postMessage")
                    body = Gson().toJson(message)
                }

                call.response.status(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}