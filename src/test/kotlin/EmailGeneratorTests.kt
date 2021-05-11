import org.junit.Test
import test.trigan.emailgen.EmailGenerator
import test.trigan.emailgen.InMemoryEmailAddressDao
import kotlin.test.assertEquals


class EmailGeneratorTests {
    private val emailAddressDao = InMemoryEmailAddressDao()
    private val emailGenerator = EmailGenerator(emailAddressDao)

    @Test
    fun testValidGeneration() {
        listOf("John Doe", "Jane Doe", "Jim Doe", "Jim Doe", "Jim Doe", "Alice", "Patrick O'Connel", "Jean-Claude Van Damme")
            .forEach { emailGenerator.generateAndSaveEmailAddress(it, "revevol.it") }

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
            ),
            emailAddressDao.getAll().sortedBy { it.address }.map { it.toString() }
        )
    }
}
