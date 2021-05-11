package test.trigan.emailgen

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.listPersonEmails() {
    get("emails/all") {
        call.respond(
            HttpStatusCode.OK,
            emailAddressDao.getAll().sortedBy { it.address }.map { it.toString() }
        )
    }
}

fun Route.appendPersonEmails() {
    post("persons/append") {
        call.generateAddresses(call.receive())
        call.respondText(text = "new persons have been added", status = HttpStatusCode.Created)
    }
}

fun Route.replacePersonEmails() {
    post("persons/replace") {
        emailAddressDao.clear()
        call.generateAddresses(call.receive())
        call.respondText(text = "old persons have been replaced with new ones", status = HttpStatusCode.Created)
    }
}

private fun ApplicationCall.generateAddresses(personalNames: List<String>) {
    val domain = application.environment.config.property("emailgen.domain").getString()
    personalNames.forEach { personalName ->
        val generatedAddress = emailGenerator.generateAndSaveEmailAddress(personalName, domain)
        application.log.info("Generated email address $generatedAddress for '$personalName'")
    }
}

fun Application.registerPersonEmailRoutes() {
    routing {
        listPersonEmails()
        appendPersonEmails()
        replacePersonEmails()
    }
}
