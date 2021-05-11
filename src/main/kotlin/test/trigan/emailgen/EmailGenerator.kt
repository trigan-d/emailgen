package test.trigan.emailgen

class EmailGenerator(val emailAddressDao: EmailAddressDao) {
    fun generateAndSaveEmailAddress(personalName: String, domain: String): String =
        getPossibleAddresses(personalName)
            .map { "$it@$domain" }
            .first { emailAddressDao.saveIfAvailable(EmailAddress(it, personalName)) }

    private fun getPossibleAddresses(personalName: String) = sequence {
        val nameChunks = personalName.trim().replace("-", " ").replace("[^a-zA-Z ]".toRegex(), "").toLowerCase().split("\\s+".toRegex())

        if (nameChunks.isEmpty()) throw IllegalArgumentException("Invalid personal name: $personalName")

        val lastName = nameChunks.takeLast(1)
        val initials = nameChunks.dropLast(1).map { it.take(1) }
        val shortForm = initials.plus(lastName).joinToString(separator = ".")

        // possible addresses for "Jean-Michel Jarre"

        yield(shortForm) // j.m.jarre (preferred)

        yield(nameChunks.joinToString(separator = ".")) // jean.michel.jarre

        yield(lastName.plus(initials).joinToString(separator = ".")) // jarre.j.m

        yieldAll(generateSequence(2) { it + 1 }.map { "$shortForm.$it" }) // j.m.jarre.2, j.m.jarre.3, etc
    }
}