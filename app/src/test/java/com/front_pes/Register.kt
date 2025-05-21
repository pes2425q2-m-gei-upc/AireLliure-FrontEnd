package com.front_pes

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ManualRegisterTest {
    val registeredUsers = mutableMapOf<String, String>()
    fun register(email: String, password: String): Boolean {
        return if (registeredUsers.containsKey(email)) {
            false // ya registrado
        } else {
            registeredUsers[email] = password
            true // registro exitoso
        }
    }

    @Before
    fun setup() {
        registeredUsers.clear() // limpiamos antes de cada test
    }

    @Test
    fun `register new user successfully`() {
        val result = register("new@example.com", "mypassword")
        assertTrue(result)
        assertEquals("mypassword", registeredUsers["new@example.com"])
    }

    @Test
    fun `register fails if email already exists`() {
        register("existing@example.com", "password1")
        val result = register("existing@example.com", "password2")
        assertFalse(result)
        assertEquals("password1", registeredUsers["existing@example.com"])
    }
}
