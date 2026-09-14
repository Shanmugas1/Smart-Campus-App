package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CampusRepository
import com.example.data.SmartCampusDatabase
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.model.Role
import com.example.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: CampusRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val database = SmartCampusDatabase.getDatabase(context)
        repository = CampusRepository(database)
    }

    @Test
    fun readStringFromContext() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart Campus", appName)
    }

    @Test
    fun testAuthenticationFlow() = runBlocking {
        repository.seedIfEmpty()
        // Test authenticating existing student
        val student = repository.authenticateUser("student1@college.edu", "password123")
        assertNotNull(student)
        assertEquals(Role.STUDENT, student?.role)
        assertEquals("CSE", student?.department)

        // Test authenticating existing admin
        val admin = repository.authenticateUser("admin@college.edu", "password123")
        assertNotNull(admin)
        assertEquals(Role.ADMIN, admin?.role)

        // Test authenticating existing faculty
        val faculty = repository.authenticateUser("faculty.cse@college.edu", "password123")
        assertNotNull(faculty)
        assertEquals(Role.FACULTY, faculty?.role)

        // Test registering a new student account
        val newStudent = User(
            id = "test_student_1",
            name = "John Doe",
            email = "johndoe@college.edu",
            password = "testpass123",
            registrationNumber = "2024CSE999",
            role = Role.STUDENT,
            department = "CSE",
            year = "3rd Year",
            section = "A"
        )
        val registered = repository.registerUser(newStudent)
        assertNotNull(registered)
        assertEquals(Role.STUDENT, registered.role)

        // Verify login with new student account
        val loggedInStudent = repository.authenticateUser("johndoe@college.edu", "testpass123")
        assertNotNull(loggedInStudent)
        assertEquals(Role.STUDENT, loggedInStudent?.role)
        assertEquals("2024CSE999", loggedInStudent?.registrationNumber)
    }
}
