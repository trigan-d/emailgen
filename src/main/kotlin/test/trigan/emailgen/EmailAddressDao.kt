package test.trigan.emailgen

import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class EmailAddress(val address: String, val personalName: String) {
    override fun toString() = "$personalName <$address>"
}

interface EmailAddressDao {
    fun clear()
    fun getAll(): List<EmailAddress>
    fun saveIfAvailable(EmailAddress: EmailAddress): Boolean
}

// dummy non-persistent implementation
class InMemoryEmailAddressDao : EmailAddressDao {
    private val addressToPersonalName = ConcurrentHashMap<String, String>()

    override fun clear() = addressToPersonalName.clear()

    override fun getAll() = addressToPersonalName.entries.map { EmailAddress(it.key, it.value) }

    override fun saveIfAvailable(EmailAddress: EmailAddress) =
        addressToPersonalName.putIfAbsent(EmailAddress.address, EmailAddress.personalName) == null
}

val emailAddressDao = InMemoryEmailAddressDao()