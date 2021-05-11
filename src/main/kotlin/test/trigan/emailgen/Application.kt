package test.trigan.emailgen

import de.nielsfalk.ktor.swagger.SwaggerSupport
import de.nielsfalk.ktor.swagger.version.shared.Contact
import de.nielsfalk.ktor.swagger.version.shared.Information
import de.nielsfalk.ktor.swagger.version.v2.Swagger
import de.nielsfalk.ktor.swagger.version.v3.OpenApi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException
import mjs.ktor.features.zipkin.ZipkinIds
import mjs.ktor.features.zipkin.zipkinMdc

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(Locations)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(DefaultHeaders)
    install(ZipkinIds)
    install(CallLogging) {
        zipkinMdc()
    }
    install(StatusPages) {
        exception<SerializationException> { cause ->
            call.respondText(text = cause.message ?: "Serialization error", status = HttpStatusCode.BadRequest)
            throw cause
        }
    }

    install(SwaggerSupport) {
        forwardRoot = true
        val information = Information(
            version = "0.1",
            title = "email generator",
            description = "Test for AODocs from Dmitry Solovyov ",
            contact = Contact(
                name = "Dmitry Solovyov",
                email = "trigan.sda@gmail.com"
            )
        )
        swagger = Swagger().apply {
            info = information
        }
        openApi = OpenApi().apply {
            info = information
        }
    }

    registerPersonEmailRoutes()
}
