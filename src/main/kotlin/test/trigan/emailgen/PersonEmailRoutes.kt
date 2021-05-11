package test.trigan.emailgen

import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Group("email addresses")
@Location("/emails/all")
class AllEmails

@Group("persons")
@Location("/persons/append")
class AppendPersons

@Group("persons")
@Location("/persons/replace")
class ReplacePersons

data class EmailsResponse(val emailAddresses: List<String>)

data class PersonsRequest(val personalNames: List<String>)

val examplePersonsRequest = example(
    "test",
    PersonsRequest(
        listOf("John Doe", "Jane Doe", "Jim Doe", "Jim Doe", "Jim Doe", "Alice", "Patrick O'Connel", "Jean-Claude Van Damme")
    )
)


fun Route.listPersonEmails() {
    get<AllEmails>("get all emails".responds(ok<EmailsResponse>())) {
        call.respond(
            HttpStatusCode.OK,
            EmailsResponse(emailAddressDao.getAll().sortedBy { it.address }.map { it.toString() })
        )
    }
}

fun Route.appendPersonEmails() {
    post<AppendPersons, PersonsRequest>(
        "append new persons".responds(ok<String>()).examples(examplePersonsRequest)
    ) { _, personNames ->
        call.generateAddresses(personNames.personalNames)
        call.respondText(text = "new persons have been added", status = HttpStatusCode.Created)
    }
}

fun Route.replacePersonEmails() {
    post<ReplacePersons, PersonsRequest>(
        "replace all persons".responds(ok<String>()).examples(examplePersonsRequest)
    ) { _, personNames ->
        emailAddressDao.clear()
        call.generateAddresses(personNames.personalNames)
        call.respondText(text = "old persons have been replaced with new ones", status = HttpStatusCode.Created)
    }
}

private fun ApplicationCall.generateAddresses(personalNames: List<String>) {
    val domain = application.environment.config.property("emailgen.domain").getString()
    personalNames.forEach { personalName ->
        val generatedAddress = generateAndSaveEmailAddress(personalName, domain)
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
