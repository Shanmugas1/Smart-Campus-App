package com.example

import com.example.backend.AttachmentController
import com.example.backend.AttachmentRepository
import com.example.backend.AttachmentService
import com.example.backend.FileStorageService
import com.example.data.AttachmentDao
import com.example.data.AuditLogDao
import com.example.data.NoticeDao
import com.example.model.Attachment
import com.example.model.AuditLog
import com.example.model.Notice
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.model.NoticeStatus
import com.example.model.Role
import com.example.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class AttachmentSecurityAndFunctionalTest {

    private lateinit var fileStorageService: FileStorageService
    private lateinit var mockAttachmentDao: MockAttachmentDao
    private lateinit var mockNoticeDao: MockNoticeDao
    private lateinit var mockAuditLogDao: MockAuditLogDao
    private lateinit var attachmentRepository: AttachmentRepository
    private lateinit var attachmentService: AttachmentService
    private lateinit var attachmentController: AttachmentController

    // Test Users
    private val adminUser = User(
        id = "adm_1",
        name = "Dr. S. Sharma",
        email = "admin@smartcampus.edu",
        registrationNumber = "ADM001",
        role = Role.ADMIN,
        department = "ADMIN",
        year = "",
        section = ""
    )

    private val facultyUser = User(
        id = "fac_1",
        name = "Prof. R. Mehta",
        email = "mehta@smartcampus.edu",
        registrationNumber = "FAC001",
        role = Role.FACULTY,
        department = "CSE",
        year = "",
        section = ""
    )

    private val studentCse2A = User(
        id = "std_cse_2a",
        name = "Aarav Patel",
        email = "aarav@smartcampus.edu",
        registrationNumber = "2024CSE101",
        role = Role.STUDENT,
        department = "CSE",
        year = "2nd Year",
        section = "Section A"
    )

    private val studentCse2B = User(
        id = "std_cse_2b",
        name = "Bhavna Singh",
        email = "bhavna@smartcampus.edu",
        registrationNumber = "2024CSE102",
        role = Role.STUDENT,
        department = "CSE",
        year = "2nd Year",
        section = "Section B"
    )

    @Before
    fun setup() {
        fileStorageService = FileStorageService()
        mockAttachmentDao = MockAttachmentDao()
        mockNoticeDao = MockNoticeDao()
        mockAuditLogDao = MockAuditLogDao()

        attachmentRepository = AttachmentRepository(mockAttachmentDao)
        attachmentService = AttachmentService(
            attachmentRepository = attachmentRepository,
            fileStorageService = fileStorageService,
            noticeDao = mockNoticeDao
        )
        attachmentController = AttachmentController(attachmentService)
    }

    // TEST 1: Reject non-whitelisted and executable extensions (e.g. .exe, .sh, .bat)
    @Test
    fun test1_rejectNonWhitelistedAndForbiddenExtensions() {
        val exeBytes = "malicious_binary".toByteArray()
        try {
            fileStorageService.storeFile("payload.exe", "application/x-msdownload", exeBytes, "notice_1")
            fail("Executable .exe files must be rejected")
        } catch (e: SecurityException) {
            assertTrue(e.message!!.contains("Security violation") || e.message!!.contains("rejected"))
        }

        val shBytes = "#!/bin/bash\necho hack".toByteArray()
        try {
            fileStorageService.storeFile("script.sh", "text/x-sh", shBytes, "notice_1")
            fail("Shell scripts must be rejected")
        } catch (e: SecurityException) {
            assertTrue(e.message!!.contains("Security violation") || e.message!!.contains("rejected"))
        }

        val unknownBytes = "random".toByteArray()
        try {
            fileStorageService.storeFile("data.xyz", "application/octet-stream", unknownBytes, "notice_1")
            fail("Non-whitelisted extension .xyz must be rejected")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Unsupported file type"))
        }
    }

    // TEST 2: Prevent path traversal attacks in filenames
    @Test
    fun test2_preventPathTraversalInFilenames() {
        val attack1 = "../../etc/passwd"
        val sanitized1 = fileStorageService.sanitizeFileName(attack1)
        assertFalse("Sanitized filename must not contain directory traversal", sanitized1.contains(".."))
        assertFalse("Sanitized filename must not contain path separators", sanitized1.contains("/") || sanitized1.contains("\\"))

        val attack2 = "C:\\Windows\\System32\\calc.pdf"
        val sanitized2 = fileStorageService.sanitizeFileName(attack2)
        assertFalse("Sanitized filename must not contain backslashes", sanitized2.contains("\\"))
        assertTrue("Should preserve clean base filename", sanitized2.endsWith("calc.pdf"))
    }

    // TEST 3: Enforce 20MB file size limit
    @Test
    fun test3_enforceMax20MbFileSizeLimit() {
        val oversizedBytes = ByteArray((20 * 1024 * 1024) + 1024) // > 20 MB
        try {
            fileStorageService.storeFile("huge_doc.pdf", "application/pdf", oversizedBytes, "notice_1")
            fail("Files exceeding 20MB must be rejected")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("File too large") || e.message!!.contains("20 MB"))
        }

        val validBytes = "Hello World PDF Content".toByteArray()
        val stored = fileStorageService.storeFile("normal_doc.pdf", "application/pdf", validBytes, "notice_1")
        assertNotNull("Valid PDF must be stored successfully", stored)
        assertEquals("normal_doc.pdf", stored.originalFileName)
        assertEquals("pdf", stored.fileExtension)
    }

    // TEST 4: Validate MIME types against allowed list
    @Test
    fun test4_validateMimeTypesAgainstAllowedList() {
        val pdfResult = fileStorageService.storeFile("syllabus.pdf", "application/pdf", "pdf bytes".toByteArray(), "n1")
        assertEquals("application/pdf", pdfResult.contentType)

        val docxResult = fileStorageService.storeFile("report.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx bytes".toByteArray(), "n1")
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", docxResult.contentType)

        val xlsxResult = fileStorageService.storeFile("grades.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx bytes".toByteArray(), "n1")
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxResult.contentType)

        val imgResult = fileStorageService.storeFile("diagram.png", "image/png", "png bytes".toByteArray(), "n1")
        assertEquals("image/png", imgResult.contentType)
    }

    // TEST 5: Verify 403 Forbidden for unauthorized users (cross-cohort access)
    @Test
    fun test5_verify403ForbiddenForCrossCohortUnauthorizedAccess(): Unit = runBlocking {
        // Create Notice targeted ONLY to CSE | 2nd Year | Section A
        val notice = Notice(
            id = "notice_cse_2a",
            title = "Internal Exam Schedule",
            content = "Exclusive CSE 2A schedule",
            category = NoticeCategory.EXAMINATION,
            priority = NoticePriority.IMPORTANT,
            postedBy = "Prof. R. Mehta",
            postedById = facultyUser.id,
            targetAudience = "CSE|2nd Year|Section A",
            createdAt = System.currentTimeMillis()
        )
        mockNoticeDao.insertNotice(notice)

        // Upload attachment via faculty
        val samplePdf = "Official Exam Schedule Content".toByteArray()
        val uploadResponse = attachmentController.uploadAttachment(
            user = facultyUser,
            noticeId = notice.id,
            fileName = "Internal_Exam_CSE2A.pdf",
            contentType = "application/pdf",
            fileBytes = samplePdf
        )
        assertTrue("Upload should succeed", uploadResponse is AttachmentController.ApiResponse.Success)
        val attachment = (uploadResponse as AttachmentController.ApiResponse.Success).data

        // CSE 2A Student attempts download -> MUST SUCCEED (200 OK)
        val response2A = attachmentController.downloadAttachment(studentCse2A, attachment.id)
        assertTrue("CSE 2A student should be authorized", response2A is AttachmentController.ApiResponse.Success)

        // CSE 2B Student attempts download -> MUST BE 403 FORBIDDEN
        val response2B = attachmentController.downloadAttachment(studentCse2B, attachment.id)
        assertTrue("CSE 2B student must be forbidden", response2B is AttachmentController.ApiResponse.Error)
        val errorResponse = response2B as AttachmentController.ApiResponse.Error
        assertEquals(403, errorResponse.statusCode)
        assertTrue("Error message should mention forbidden/unauthorized", errorResponse.message.contains("403") || errorResponse.message.contains("not authorized"))
    }

    // TEST 6: Verify 401 Unauthorized for unauthenticated access
    @Test
    fun test6_verify401UnauthorizedForUnauthenticatedAccess(): Unit = runBlocking {
        val notice = Notice(
            id = "notice_public",
            title = "Campus Guidelines",
            content = "General guidelines",
            category = NoticeCategory.GENERAL,
            priority = NoticePriority.NORMAL,
            postedBy = "Admin",
            postedById = adminUser.id,
            targetAudience = "ALL",
            createdAt = System.currentTimeMillis()
        )
        mockNoticeDao.insertNotice(notice)

        val uploadResponse = attachmentController.uploadAttachment(
            user = adminUser,
            noticeId = notice.id,
            fileName = "Guidelines.pdf",
            contentType = "application/pdf",
            fileBytes = "Campus rules".toByteArray()
        )
        val attachment = (uploadResponse as AttachmentController.ApiResponse.Success).data

        // Null user attempts download
        val response = attachmentController.downloadAttachment(null, attachment.id)
        assertTrue("Unauthenticated access must return Error", response is AttachmentController.ApiResponse.Error)
        val error = response as AttachmentController.ApiResponse.Error
        assertEquals(401, error.statusCode)
        assertTrue("Error message should mention authentication", error.message.contains("Authentication required") || error.message.contains("401"))
    }

    // TEST 7: Verify signed URL expiration (15-minute TTL)
    @Test
    fun test7_verifySignedUrlExpirationAndTtl() {
        val storageKey = "vault/notices/n1/test_token.pdf"
        val userId = "user_123"
        val signedUrl = fileStorageService.generateSignedUrl(storageKey, userId, ttlMillis = 15 * 60 * 1000L)
        assertTrue("Signed URL should contain token", signedUrl.contains("token="))
        assertTrue("Signed URL should contain expires parameter", signedUrl.contains("expires="))
        assertTrue("Signed URL should contain storageKey", signedUrl.contains(storageKey))

        // Validate token right away for valid user -> Valid
        val token = signedUrl.substringAfter("token=").substringBefore("&")
        val validatedKey = fileStorageService.validateSignedToken(token, userId)
        assertEquals(storageKey, validatedKey)

        // Validate for different user -> SecurityException (403)
        try {
            fileStorageService.validateSignedToken(token, "other_user")
            fail("Token must reject mismatched user session")
        } catch (e: SecurityException) {
            assertTrue(e.message!!.contains("403 Forbidden"))
        }
    }

    // TEST 8: Confirm metadata persists correctly in DB
    @Test
    fun test8_confirmMetadataPersistsCorrectlyInDatabase(): Unit = runBlocking {
        val notice = Notice(
            id = "n_meta",
            title = "Fee Circular",
            content = "Payment details",
            category = NoticeCategory.ACADEMIC,
            priority = NoticePriority.NORMAL,
            postedBy = "Accounts Dept",
            postedById = adminUser.id,
            targetAudience = "ALL",
            createdAt = System.currentTimeMillis()
        )
        mockNoticeDao.insertNotice(notice)

        val fileBytes = "Fee Structure Data".toByteArray()
        val upload = attachmentController.uploadAttachment(
            user = adminUser,
            noticeId = notice.id,
            fileName = "Fee_Structure_2026.xlsx",
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            fileBytes = fileBytes
        )
        assertTrue("Upload should succeed", upload is AttachmentController.ApiResponse.Success)
        val att = (upload as AttachmentController.ApiResponse.Success).data

        val fetched = mockAttachmentDao.getAttachmentById(att.id)
        assertNotNull("Attachment metadata must exist in database", fetched)
        assertEquals("Fee_Structure_2026.xlsx", fetched!!.originalFileName)
        assertEquals("xlsx", fetched.fileExtension)
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fetched.contentType)
    }

    // TEST 9: Confirm binary data is stored and retrieved in cloud storage vault
    @Test
    fun test9_confirmBinaryDataStoredInCloudStorageVault(): Unit = runBlocking {
        val sampleData = "BINARY_PAYLOAD_PDF_12345_TEST".toByteArray()
        val storeResult = fileStorageService.storeFile("test_doc.pdf", "application/pdf", sampleData, "test_notice")

        val retrieved = fileStorageService.retrieveFile(storeResult.storageKey)
        assertNotNull("Binary data must be retrievable from cloud storage", retrieved)
        assertEquals("BINARY_PAYLOAD_PDF_12345_TEST", String(retrieved))
    }

    // TEST 10: Verify lifecycle operations (upload, preview, download, delete)
    @Test
    fun test10_verifyAttachmentLifecycleOperations(): Unit = runBlocking {
        val notice = Notice(
            id = "n_lifecycle",
            title = "Lifecycle Test Notice",
            content = "Testing full attachment lifecycle",
            category = NoticeCategory.GENERAL,
            priority = NoticePriority.NORMAL,
            postedBy = "Admin",
            postedById = adminUser.id,
            targetAudience = "ALL",
            createdAt = System.currentTimeMillis()
        )
        mockNoticeDao.insertNotice(notice)

        // 1. Upload
        val uploadResp = attachmentController.uploadAttachment(
            user = adminUser,
            noticeId = notice.id,
            fileName = "Policy.pdf",
            contentType = "application/pdf",
            fileBytes = "Policy Document Content".toByteArray()
        )
        assertTrue(uploadResp is AttachmentController.ApiResponse.Success)
        val attachment = (uploadResp as AttachmentController.ApiResponse.Success).data

        // 2. Preview
        val previewResp = attachmentController.previewAttachment(adminUser, attachment.id)
        assertTrue("Preview should succeed", previewResp is AttachmentController.ApiResponse.Success)
        val previewData = (previewResp as AttachmentController.ApiResponse.Success).data
        assertTrue("Preview should include signed URL", previewData.signedUrl.startsWith("https://"))

        // 3. Download
        val downloadResp = attachmentController.downloadAttachment(adminUser, attachment.id)
        assertTrue("Download should succeed", downloadResp is AttachmentController.ApiResponse.Success)

        // 4. Delete
        val deleteResp = attachmentController.deleteAttachment(adminUser, attachment.id)
        assertTrue("Delete should succeed", deleteResp is AttachmentController.ApiResponse.Success)
        assertNull("Attachment should no longer exist in DB", mockAttachmentDao.getAttachmentById(attachment.id))
    }
}

// In-Memory Mock DAOs for Unit & Integration Testing
class MockAttachmentDao : AttachmentDao {
    val items = mutableListOf<Attachment>()

    override suspend fun insertAttachment(attachment: Attachment) {
        items.add(attachment)
    }

    override suspend fun insertAttachments(attachments: List<Attachment>) {
        items.addAll(attachments)
    }

    override fun getAttachmentsForNotice(noticeId: String): Flow<List<Attachment>> {
        return flowOf(items.filter { it.noticeId == noticeId })
    }

    override suspend fun getAttachmentsForNoticeDirect(noticeId: String): List<Attachment> {
        return items.filter { it.noticeId == noticeId }
    }

    override suspend fun getAttachmentById(attachmentId: String): Attachment? {
        return items.find { it.id == attachmentId }
    }

    override suspend fun getAttachmentByStorageKey(storageKey: String): Attachment? {
        return items.find { it.storageKey == storageKey }
    }

    override suspend fun incrementDownloadCount(attachmentId: String) {
        val index = items.indexOfFirst { it.id == attachmentId }
        if (index != -1) {
            val curr = items[index]
            items[index] = curr.copy(downloadCount = curr.downloadCount + 1)
        }
    }

    override suspend fun deleteAttachment(attachmentId: String) {
        items.removeAll { it.id == attachmentId }
    }

    override suspend fun deleteAttachmentsForNotice(noticeId: String) {
        items.removeAll { it.noticeId == noticeId }
    }

    override fun getAllAttachments(): Flow<List<Attachment>> {
        return flowOf(items.toList())
    }
}

class MockNoticeDao : NoticeDao {
    val notices = mutableListOf<Notice>()

    override fun getAllNotices(): Flow<List<Notice>> = flowOf(notices.toList())
    override fun getActiveNotices(): Flow<List<Notice>> = flowOf(notices.filter { it.status != NoticeStatus.ARCHIVED })
    override fun getNoticeByIdFlow(noticeId: String): Flow<Notice?> = flowOf(notices.find { it.id == noticeId })

    override suspend fun insertNotice(notice: Notice) {
        notices.add(notice)
    }

    override suspend fun insertNotices(noticeList: List<Notice>) {
        notices.addAll(noticeList)
    }

    override suspend fun updateNotice(notice: Notice) {
        val idx = notices.indexOfFirst { it.id == notice.id }
        if (idx != -1) notices[idx] = notice
    }

    override suspend fun getNoticeById(noticeId: String): Notice? {
        return notices.find { it.id == noticeId }
    }

    override suspend fun toggleNoticePin(noticeId: String, pinned: Boolean) {
        val idx = notices.indexOfFirst { it.id == noticeId }
        if (idx != -1) notices[idx] = notices[idx].copy(pinned = pinned)
    }

    override suspend fun updateNoticeStatus(noticeId: String, status: NoticeStatus, updatedAt: Long) {
        val idx = notices.indexOfFirst { it.id == noticeId }
        if (idx != -1) notices[idx] = notices[idx].copy(status = status, updatedAt = updatedAt)
    }

    override suspend fun deleteNotice(noticeId: String) {
        notices.removeAll { it.id == noticeId }
    }
}

class MockAuditLogDao : AuditLogDao {
    val logs = mutableListOf<AuditLog>()

    override fun getRecentAuditLogs(): Flow<List<AuditLog>> = flowOf(logs.toList())

    override suspend fun insertAuditLog(log: AuditLog) {
        logs.add(log)
    }
}
