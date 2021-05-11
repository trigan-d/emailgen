package test.trigan.emailgen

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.Test
import kotlin.test.assertEquals

class EmailGeneratorModuleTest {
    @Test
    fun testValidEmailsGeneration() {
        withTestApplication({
            install(ContentNegotiation) { json() }
            (environment.config as MapApplicationConfig).put("emailgen.domain", "revevol.it")
            emailGeneratorModule(emailAddressDao = InMemoryEmailAddressDao())
        }) {
            handleRequest(HttpMethod.Post, "/persons/replace") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    listOf(
                        "John Doe", "Jane Doe", "Jim Doe", "Jim Doe", "Jim Doe", "Alice", "Patrick O'Connel", "Jean-Claude Van Damme"
                    ).toJson()
                )
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }

            handleRequest(HttpMethod.Get, "/emails/all").apply {
                assertEquals(
                    listOf(
                        "Alice <alice@revevol.it>",
                        "Jim Doe <doe.j@revevol.it>",
                        "Jean-Claude Van Damme <j.c.v.damme@revevol.it>",
                        "Jim Doe <j.doe.2@revevol.it>",
                        "John Doe <j.doe@revevol.it>",
                        "Jane Doe <jane.doe@revevol.it>",
                        "Jim Doe <jim.doe@revevol.it>",
                        "Patrick O'Connel <p.oconnel@revevol.it>"
                    ).toJson(),
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}

inline fun <reified T> T.toJson() = Json.encodeToString(serializer(), this)