package com.example.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.model.Attachment;
import com.example.model.AuditLog;
import com.example.model.Bookmark;
import com.example.model.Department;
import com.example.model.Notice;
import com.example.model.NoticeCategory;
import com.example.model.NoticePriority;
import com.example.model.NoticeRead;
import com.example.model.NoticeStatus;
import com.example.model.Role;
import com.example.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class,
                Notice.class,
                NoticeRead.class,
                Bookmark.class,
                Department.class,
                AuditLog.class,
                Attachment.class
        },
        version = 4,
        exportSchema = false
)
public abstract class SmartCampusDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract NoticeDao noticeDao();
    public abstract NoticeReadDao noticeReadDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract DepartmentDao departmentDao();
    public abstract AuditLogDao auditLogDao();
    public abstract AttachmentDao attachmentDao();

    private static volatile SmartCampusDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static SmartCampusDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SmartCampusDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SmartCampusDatabase.class,
                            "smart_campus_database"
                    )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        if (INSTANCE != null) {
                                            populateInitialData(INSTANCE);
                                        }
                                    });
                                }

                                @Override
                                public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                                    super.onDestructiveMigration(db);
                                    databaseWriteExecutor.execute(() -> {
                                        if (INSTANCE != null) {
                                            populateInitialData(INSTANCE);
                                        }
                                    });
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    databaseWriteExecutor.execute(() -> {
                                        try {
                                            if (INSTANCE != null && INSTANCE.userDao().getUserCountDirect() == 0) {
                                                populateInitialData(INSTANCE);
                                            }
                                        } catch (Exception ignored) {
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void populateInitialData(SmartCampusDatabase database) {
        UserDao userDao = database.userDao();
        NoticeDao noticeDao = database.noticeDao();
        DepartmentDao departmentDao = database.departmentDao();
        AuditLogDao auditLogDao = database.auditLogDao();
        AttachmentDao attachmentDao = database.attachmentDao();

        // 1. Seed Departments
        List<Department> departments = Arrays.asList(
                new Department("1", "CSE", "Computer Science & Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("2", "CSE (Cyber Security)", "CSE - Cyber Security", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("3", "CSE (AI & ML)", "CSE - Artificial Intelligence & ML", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("4", "AI & DS", "Artificial Intelligence & Data Science", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("5", "CSBS", "Computer Science & Business Systems", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("6", "Information Technology", "Information Technology", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("7", "ECE", "Electronics & Communication Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("8", "ECE (ACT)", "ECE - Advanced Communication Tech", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("9", "EEE", "Electrical & Electronics Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("10", "VLSI", "VLSI Design & Technology", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("11", "Biomedical", "Biomedical Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("12", "Mechanical", "Mechanical Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("13", "Mechatronics", "Mechatronics Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C"),
                new Department("14", "Civil", "Civil Engineering", "1st Year,2nd Year,3rd Year,4th Year", "Section A,Section B,Section C")
        );
        departmentDao.insertDepartments(departments);

        // 2. Seed Demo Users
        long now = System.currentTimeMillis();
        List<User> demoUsers = Arrays.asList(
                new User(
                        "usr_student_1",
                        "Karthik Selvam",
                        "student1@college.edu",
                        "password123",
                        "2024CSE101",
                        Role.STUDENT,
                        "CSE",
                        "2nd Year",
                        "Section A",
                        "",
                        true,
                        now - 86400000L * 30
                ),
                new User(
                        "usr_student_2",
                        "Dharani Velmurugan",
                        "student2@college.edu",
                        "password123",
                        "2024CSE142",
                        Role.STUDENT,
                        "CSE",
                        "2nd Year",
                        "Section B",
                        "",
                        true,
                        now - 86400000L * 30
                ),
                new User(
                        "usr_student_3",
                        "Anitha Ramachandran",
                        "student3@college.edu",
                        "password123",
                        "2023CSE015",
                        Role.STUDENT,
                        "CSE",
                        "3rd Year",
                        "Section A",
                        "",
                        true,
                        now - 86400000L * 60
                ),
                new User(
                        "usr_student_4",
                        "Kavin Kumaran",
                        "student4@college.edu",
                        "password123",
                        "2024ECE009",
                        Role.STUDENT,
                        "ECE",
                        "2nd Year",
                        "Section A",
                        "",
                        true,
                        now - 86400000L * 30
                ),
                new User(
                        "usr_admin_1",
                        "Prof. S. Shanmugasundaram",
                        "admin@college.edu",
                        "password123",
                        "EMP-ADM-101",
                        Role.ADMIN,
                        "ADMIN",
                        "",
                        "",
                        "",
                        true,
                        now - 86400000L * 120
                ),
                new User(
                        "usr_faculty_1",
                        "Dr. K. Priyadharshini",
                        "faculty.cse@college.edu",
                        "password123",
                        "EMP-CSE-204",
                        Role.FACULTY,
                        "CSE",
                        "",
                        "",
                        "",
                        true,
                        now - 86400000L * 100
                ),
                new User(
                        "usr_superadmin_1",
                        "Dr. R. Thirunavukkarasu",
                        "superadmin@college.edu",
                        "password123",
                        "EMP-DIR-001",
                        Role.SUPER_ADMIN,
                        "ADMIN",
                        "",
                        "",
                        "",
                        true,
                        now - 86400000L * 365
                )
        );
        userDao.insertUsers(demoUsers);

        // 3. Seed Demo Notices
        List<Notice> demoNotices = Arrays.asList(
                new Notice(
                        "not_001",
                        "Internal Assessment II - Timetable & Seating Matrix",
                        "Internal Assessment Examination II for CSE 2nd Year Section A will commence from next Monday at 09:30 AM in Hall LH-201. Students are strictly instructed to bring their institutional identity cards. Electronic smart watches and mobile phones are strictly barred.",
                        NoticeCategory.EXAMINATION,
                        NoticePriority.URGENT,
                        "Prof. S. Shanmugasundaram (Exam Cell)",
                        "usr_admin_1",
                        "[\"CSE|2nd Year|Section A\"]",
                        NoticeStatus.ACTIVE,
                        true,
                        true,
                        now - 3600000L * 2,
                        now - 3600000L * 2,
                        null,
                        null,
                        "[{\"id\":\"att_1\",\"fileName\":\"IA_II_Timetable_CSE2A.pdf\",\"fileType\":\"PDF\",\"fileSize\":\"1.2 MB\"}]",
                        true
                ),
                new Notice(
                        "not_002",
                        "Campus Heavy Rainfall Advisory & Hybrid Instruction Mode",
                        "Due to the regional meteorological alert for severe rainfall and waterlogging risks across the district, all undergraduate and postgraduate lectures scheduled for today will transition to hybrid mode. Hostel transport shuttles will operate on a 20-minute loop until 06:00 PM.",
                        NoticeCategory.EMERGENCY,
                        NoticePriority.URGENT,
                        "Dr. R. Thirunavukkarasu (Dean Academic Affairs)",
                        "usr_superadmin_1",
                        "[\"ALL\"]",
                        NoticeStatus.ACTIVE,
                        true,
                        true,
                        now - 3600000L * 5,
                        now - 3600000L * 5,
                        null,
                        null,
                        "[]",
                        true
                ),
                new Notice(
                        "not_003",
                        "Google Cloud & Smart Campus AI Hackathon 2026",
                        "The Department of Computer Science & Engineering is organizing the Annual Smart Campus Hackathon in collaboration with Google Developer Groups. Teams of 2 to 4 students can register their prototype abstract. Cash prizes worth Rs 1,00,000 + cloud credits.",
                        NoticeCategory.EVENT,
                        NoticePriority.IMPORTANT,
                        "Dr. K. Priyadharshini (CSE Dept)",
                        "usr_faculty_1",
                        "[\"CSE\"]",
                        NoticeStatus.ACTIVE,
                        false,
                        true,
                        now - 3600000L * 12,
                        now - 3600000L * 12,
                        null,
                        null,
                        "[{\"id\":\"att_2\",\"fileName\":\"Hackathon_Brochure_Rules.pdf\",\"fileType\":\"PDF\",\"fileSize\":\"3.4 MB\"}]",
                        true
                ),
                new Notice(
                        "not_004",
                        "Data Structures & Algorithms Laboratory Venue Relocation",
                        "All 2nd Year CSE practical sessions scheduled for DS Lab will take place in the newly commissioned Cloud Computing Lab (Block C, 3rd Floor, Lab 304). Please ensure your GitHub classroom assignments are pushed prior to lab entry.",
                        NoticeCategory.ACADEMIC,
                        NoticePriority.NORMAL,
                        "Prof. S. Shanmugasundaram",
                        "usr_admin_1",
                        "[\"CSE|2nd Year\"]",
                        NoticeStatus.ACTIVE,
                        false,
                        true,
                        now - 3600000L * 24,
                        now - 3600000L * 24,
                        null,
                        null,
                        "[]",
                        true
                ),
                new Notice(
                        "not_005",
                        "Section 2-B Database Management Systems Review Schedule",
                        "The mini-project evaluation for CSE 2nd Year Section B will be conducted on Thursday between 01:30 PM and 04:30 PM in Seminar Hall 2. Ensure your ER diagrams, normalization proofs, and SQL query scripts are compiled into the official record booklet.",
                        NoticeCategory.ACADEMIC,
                        NoticePriority.IMPORTANT,
                        "Dr. K. Priyadharshini",
                        "usr_faculty_1",
                        "[\"CSE|2nd Year|Section B\"]",
                        NoticeStatus.ACTIVE,
                        false,
                        true,
                        now - 3600000L * 30,
                        now - 3600000L * 30,
                        null,
                        null,
                        "[{\"id\":\"att_3\",\"fileName\":\"DBMS_Rubrics_Evaluation.pdf\",\"fileType\":\"PDF\",\"fileSize\":\"850 KB\"}]",
                        true
                ),
                new Notice(
                        "not_006",
                        "TCS Digital & Prime Campus Placement Drive - Registration Open",
                        "Tata Consultancy Services (TCS) invites applications for Digital & Prime Cadre software engineering positions. Eligible branches: All B.Tech disciplines with CGPA >= 7.0 and no active backlogs. Online registration deadline is this Friday, 05:00 PM.",
                        NoticeCategory.PLACEMENT,
                        NoticePriority.URGENT,
                        "Corporate Relations & Placement Cell",
                        "usr_admin_1",
                        "[\"ALL\"]",
                        NoticeStatus.ACTIVE,
                        false,
                        true,
                        now - 3600000L * 48,
                        now - 3600000L * 48,
                        null,
                        null,
                        "[{\"id\":\"att_4\",\"fileName\":\"TCS_Eligibility_JD.pdf\",\"fileType\":\"PDF\",\"fileSize\":\"2.1 MB\"}]",
                        true
                ),
                new Notice(
                        "not_007",
                        "ECE Embedded Systems Design Workshop & Hardware Kit Allotment",
                        "Hands-on workshop on ARM Cortex-M4 and ESP32 IoT microcontrollers for all ECE department students will be held in the Communication Systems Lab. Hardware toolkits will be distributed at 09:00 AM on Saturday.",
                        NoticeCategory.ACADEMIC,
                        NoticePriority.NORMAL,
                        "Dr. S. Manikandan (HOD ECE)",
                        "usr_admin_1",
                        "[\"ECE\"]",
                        NoticeStatus.ACTIVE,
                        false,
                        true,
                        now - 3600000L * 72,
                        now - 3600000L * 72,
                        null,
                        null,
                        "[]",
                        true
                ),
                new Notice(
                        "not_008",
                        "Annual Academic Calendar & Institutional Holiday List 2026-27",
                        "The Office of the Dean of Academic Affairs has officially gazetted the semester academic calendar, internal exam schedules, convocation dates, and government gazetted holidays for the academic year 2026-2027.",
                        NoticeCategory.GENERAL,
                        NoticePriority.NORMAL,
                        "Dr. R. Thirunavukkarasu (Dean Academic Affairs)",
                        "usr_superadmin_1",
                        "[\"ALL\"]",
                        NoticeStatus.ACTIVE,
                        true,
                        true,
                        now - 86400000L * 5,
                        now - 86400000L * 5,
                        null,
                        null,
                        "[{\"id\":\"att_5\",\"fileName\":\"Academic_Calendar_2026_2027.pdf\",\"fileType\":\"PDF\",\"fileSize\":\"1.8 MB\"}]",
                        true
                )
        );
        noticeDao.insertNotices(demoNotices);

        // 4. Seed Attachments
        List<Attachment> demoAttachments = Arrays.asList(
                new Attachment(
                        "att_1",
                        "not_001",
                        "IA_II_Timetable_CSE2A.pdf",
                        "f8a92b3c-1234-4567-89ab-ia2timetable.pdf",
                        "application/pdf",
                        "pdf",
                        1258291L,
                        "1.2 MB",
                        "vault/notices/not_001/f8a92b3c-1234-4567-89ab-ia2timetable.pdf",
                        "usr_admin_1",
                        now - 3600000L * 2,
                        14
                ),
                new Attachment(
                        "att_2",
                        "not_003",
                        "Hackathon_Brochure_Rules.pdf",
                        "c3d4e5f6-7890-1234-5678-hackathonbrochure.pdf",
                        "application/pdf",
                        "pdf",
                        3565158L,
                        "3.4 MB",
                        "vault/notices/not_003/c3d4e5f6-7890-1234-5678-hackathonbrochure.pdf",
                        "usr_faculty_1",
                        now - 3600000L * 12,
                        38
                ),
                new Attachment(
                        "att_3",
                        "not_005",
                        "DBMS_Rubrics_Evaluation.pdf",
                        "a1b2c3d4-5678-9012-3456-dbmsrubrics.pdf",
                        "application/pdf",
                        "pdf",
                        870400L,
                        "850 KB",
                        "vault/notices/not_005/a1b2c3d4-5678-9012-3456-dbmsrubrics.pdf",
                        "usr_faculty_1",
                        now - 3600000L * 30,
                        9
                ),
                new Attachment(
                        "att_4",
                        "not_006",
                        "TCS_Eligibility_JD.pdf",
                        "e5f6a7b8-9012-3456-7890-tcsjd.pdf",
                        "application/pdf",
                        "pdf",
                        2202009L,
                        "2.1 MB",
                        "vault/notices/not_006/e5f6a7b8-9012-3456-7890-tcsjd.pdf",
                        "usr_admin_1",
                        now - 3600000L * 48,
                        112
                ),
                new Attachment(
                        "att_5",
                        "not_008",
                        "Academic_Calendar_2026_2027.pdf",
                        "d9e0f1a2-3456-7890-1234-academiccalendar.pdf",
                        "application/pdf",
                        "pdf",
                        1887436L,
                        "1.8 MB",
                        "vault/notices/not_008/d9e0f1a2-3456-7890-1234-academiccalendar.pdf",
                        "usr_superadmin_1",
                        now - 86400000L * 5,
                        345
                )
        );
        attachmentDao.insertAttachments(demoAttachments);

        // 5. Initial Audit Log
        AuditLog auditLog = new AuditLog(
                "aud_init_01",
                "usr_superadmin_1",
                "Dr. R. Thirunavukkarasu (Dean Academic Affairs)",
                "SYSTEM_INITIALIZATION",
                "Institution",
                "COLLEGE_ROOT",
                "ALL",
                now,
                "Smart Campus hierarchy initialized with 14 academic departments and default demo cohorts."
        );
        auditLogDao.insertAuditLog(auditLog);
    }
}
