// ktlint-disable
@file:Suppress("ALL")
import org.junit.Assert.*
import org.junit.Test

class LoginTest {

    fun login(email: String, password: String): Boolean {
        val validEmail = "user@example.com"
        val validPassword = "1234"
        return email == validEmail && password == validPassword
    }

    @Test
    fun `login success with correct credentials`() {
        val result = login("user@example.com", "1234")
        assertTrue(result)
    }

    @Test
    fun `login fails with incorrect password`() {
        val result = login("user@example.com", "wrongpass")
        assertFalse(result)
    }

    @Test
    fun `login fails with incorrect email`() {
        val result = login("wrong@example.com", "1234")
        assertFalse(result)
    }
}
