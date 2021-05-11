package test.trigan.emailgen

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.emailGeneratorModule(
    emailAddressDao: EmailAddressDao = InMemoryEmailAddressDao()
) {
    val emailGenerator = EmailGenerator(emailAddressDao)
    val domain = environment.config.property("emailgen.domain").getString()

    fun generateAddresses(personalNames: List<String>) {
        personalNames.forEach { personalName ->
            val generatedAddress = emailGenerator.generateAndSaveEmailAddress(personalName, domain)
            log.info("Generated email address $generatedAddress for '$personalName'")
        }
    }

    routing {
        get("emails/all") {
            call.respond(
                HttpStatusCode.OK,
                emailAddressDao.getAll().sortedBy { it.address }.map { it.toString() }
            )
        }
        post("persons/append") {
            generateAddresses(call.receive())
            call.respondText(text = "new persons have been added", status = HttpStatusCode.Created)
        }
        post("persons/replace") {
            emailAddressDao.clear()
            generateAddresses(call.receive())
            call.respondText(text = "old persons have been replaced with new ones", status = HttpStatusCode.Created)
        }
    }
}
