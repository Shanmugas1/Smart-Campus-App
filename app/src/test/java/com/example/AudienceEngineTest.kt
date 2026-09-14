package com.example

import com.example.model.Role
import com.example.model.User
import com.example.service.AudienceEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudienceEngineTest {

    private val studentCse2A = User(
        id = "s1",
        name = "Student 1",
        email = "s1@test.com",
        registrationNumber = "2024CSE101",
        role = Role.STUDENT,
        department = "CSE",
        year = "2nd Year",
        section = "Section A"
    )

    private val studentCse2B = User(
        id = "s2",
        name = "Student 2",
        email = "s2@test.com",
        registrationNumber = "2024CSE102",
        role = Role.STUDENT,
        department = "CSE",
        year = "2nd Year",
        section = "Section B"
    )

    private val studentCse3A = User(
        id = "s3",
        name = "Student 3",
        email = "s3@test.com",
        registrationNumber = "2023CSE101",
        role = Role.STUDENT,
        department = "CSE",
        year = "3rd Year",
        section = "Section A"
    )

    private val studentEce2A = User(
        id = "s4",
        name = "Student 4",
        email = "s4@test.com",
        registrationNumber = "2024ECE101",
        role = Role.STUDENT,
        department = "ECE",
        year = "2nd Year",
        section = "Section A"
    )

    private val adminUser = User(
        id = "admin1",
        name = "Admin 1",
        email = "admin@test.com",
        registrationNumber = "ADM001",
        role = Role.ADMIN,
        department = "ADMIN",
        year = "",
        section = ""
    )

    @Test
    fun testEntireCollegeTargeting() {
        // ALL scope matches everyone
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2A, "ALL"))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2B, "ALL"))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse3A, "ALL"))
        assertTrue(AudienceEngine.isStudentAuthorized(studentEce2A, "ALL"))
        assertTrue(AudienceEngine.isStudentAuthorized(adminUser, "ALL"))
    }

    @Test
    fun testDepartmentLevelTargeting() {
        val target = "CSE"
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2A, target))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2B, target))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse3A, target))
        // ECE student must NOT have access
        assertFalse(AudienceEngine.isStudentAuthorized(studentEce2A, target))
    }

    @Test
    fun testYearLevelTargeting() {
        val target = "CSE|2nd Year"
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2A, target))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2B, target))
        // 3rd Year CSE student must NOT have access
        assertFalse(AudienceEngine.isStudentAuthorized(studentCse3A, target))
        // 2nd Year ECE student must NOT have access
        assertFalse(AudienceEngine.isStudentAuthorized(studentEce2A, target))
    }

    @Test
    fun testSectionLevelTargeting() {
        val target = "CSE|2nd Year|Section A"
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse2A, target))
        // Section B must NOT have access
        assertFalse(AudienceEngine.isStudentAuthorized(studentCse2B, target))
        // Section A in 3rd year must NOT have access
        assertFalse(AudienceEngine.isStudentAuthorized(studentCse3A, target))
    }

    @Test
    fun testMultiTargetListMatching() {
        val targetList = "ECE|2nd Year,CSE|3rd Year|Section A"
        assertFalse(AudienceEngine.isStudentAuthorized(studentCse2A, targetList))
        assertTrue(AudienceEngine.isStudentAuthorized(studentEce2A, targetList))
        assertTrue(AudienceEngine.isStudentAuthorized(studentCse3A, targetList))
    }

    @Test
    fun testTargetCompression() {
        // Redundant children under a parent department should be dropped
        val targetsWithRedundantChildren = setOf("CSE", "CSE|2nd Year", "CSE|2nd Year|Section A")
        val compressed = AudienceEngine.compressTargets(targetsWithRedundantChildren)
        assertEquals(listOf("CSE"), compressed)

        // Redundant child sections under a parent year should be dropped
        val yearWithSection = setOf("CSE|2nd Year", "CSE|2nd Year|Section A", "CSE|2nd Year|Section B")
        val compressedYear = AudienceEngine.compressTargets(yearWithSection)
        assertEquals(listOf("CSE|2nd Year"), compressedYear)

        // Distinct scopes should both be retained
        val mixedTargets = setOf("CSE|2nd Year", "ECE|1st Year|Section A")
        val compressedMixed = AudienceEngine.compressTargets(mixedTargets)
        assertEquals(listOf("CSE|2nd Year", "ECE|1st Year|Section A").sorted(), compressedMixed)
    }
}
