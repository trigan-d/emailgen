package test.trigan.emailgen

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.SerializationException
import mjs.ktor.features.zipkin.ZipkinIds
import mjs.ktor.features.zipkin.zipkinMdc

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) { json() }
    install(DefaultHeaders)
    install(ZipkinIds)
    install(CallLogging) {
        filter { call -> !call.request.path().startsWith("/apidocs") }
        zipkinMdc()
    }
    install(StatusPages) {
        exception<SerializationException> { cause ->
            call.respondText(text = cause.message ?: "Serialization error", status = HttpStatusCode.BadRequest)
            throw cause
        }
    }

    registerPersonEmailRoutes()

    routing {
        static("apidocs") { resources("swagger-ui") }
    }
}
