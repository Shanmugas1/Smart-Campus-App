# Smart Campus Notice Board System

## Overview

**Smart Campus Notice Board System** is a secure digital campus
communication platform that provides a centralized way for colleges to
publish and manage official announcements.

Authorized Faculty and Administrators can create notices, choose the
target audience, and attach files such as PDFs, images, Word documents,
PowerPoint presentations, and Excel files. Students receive
announcements relevant to their **department, year, and section**
through a dedicated dashboard.

The project aims to provide a structured alternative to relying only on
physical notice boards, WhatsApp groups, and general-purpose classroom
communication platforms.

## Problem Statement

Important college announcements are often distributed through multiple
channels. This can cause:

-   Important notices to get buried among unrelated messages
-   Students to receive irrelevant announcements
-   Difficulty finding older notices
-   Poor categorization and priority management
-   Difficulty managing official attachments
-   Weak control over who can publish or access information
-   Device-dependent data when local storage is used

The Smart Campus Notice Board System addresses these problems using
targeted communication, role-based access, and cloud-based storage.

## Objectives

-   Create a centralized digital notice board
-   Deliver announcements to the correct students
-   Support Department → Year → Section targeting
-   Provide separate Student, Faculty, and Admin dashboards
-   Support secure Login, Register, and Logout
-   Allow authorized users to publish announcements
-   Support PDF, image, DOCX, PPTX, and XLSX attachments
-   Move shared application data from local storage to cloud storage
-   Provide search, categories, notifications, and saved notices
-   Prevent unauthorized access to protected dashboards and files

## Main Features

### Student

Students can:

-   Login securely
-   View relevant announcements
-   View urgent and important notices
-   Search announcements
-   Filter notices by category
-   Save/bookmark notices
-   View and download authorized attachments
-   View their profile
-   Receive targeted campus information

Students must not be able to access Faculty or Admin functionality.

### Faculty

Authorized Faculty can:

-   Login securely
-   Access the Faculty dashboard
-   Create announcements according to assigned permissions
-   Select the target audience
-   Attach documents and images
-   Manage announcements within their permitted scope

### Admin

Administrators can:

-   Access the Admin dashboard
-   Create and manage official announcements
-   Select departments, years, and sections
-   Upload announcement attachments
-   Manage users and departments
-   Access administrative features according to their permissions

## Target Audience System

One of the core features is hierarchical announcement targeting.

``` text
College
└── Department
    └── Year
        └── Section
```

Example:

``` text
CSE
└── 2nd Year
    ├── Section A
    ├── Section B
    └── Section C
```

An announcement can therefore be targeted specifically to:

``` text
CSE | 2nd Year | Section A
```

Only authorized students belonging to that audience should receive and
access the announcement.

## Announcement System

An announcement can contain:

-   Title
-   Description/content
-   Category
-   Priority
-   Posted-by information
-   Target audience
-   Creation date and time
-   Optional schedule/expiry
-   Attachments

Example:

``` text
Title: Internal Examination Schedule
Category: Academic
Priority: Important
Target: CSE | 2nd Year | Section A
Attachment: Internal_Exam_Timetable.pdf
```

## Attachment Support

Supported attachment types can include:

-   PDF
-   JPG / JPEG
-   PNG / WEBP
-   DOC / DOCX
-   PPT / PPTX
-   XLS / XLSX

A single announcement may contain multiple attachments.

``` text
Internal Examination Schedule

Attachments:
- Internal_Exam_Timetable.pdf
- Seating_Arrangement.png
- Examination_Instructions.docx
```

Protected attachments should only be accessible to users who are
authorized to view the associated announcement.

## Cloud Architecture

The project is being migrated from device-dependent local storage to
cloud-based storage.

### Cloud Database

Structured application data can be stored in Firebase Cloud Firestore or
the finalized cloud database.

Examples:

``` text
users
notices
departments
noticeTargets
bookmarks
notifications
attachmentMetadata
```

### Cloud File Storage

Actual files should be stored in private cloud object/file storage.

Examples:

``` text
PDF
Images
DOCX
PPTX
XLSX
```

Conceptually:

``` text
Smart Campus Application
        |
        +----------------------+
        |                      |
        v                      v
 Cloud Database          Cloud File Storage
        |                      |
 Users                  PDF
 Notices                Images
 Departments            DOCX
 Audience Data          PPTX
 Metadata               XLSX
```

Large documents should not be stored as Base64 inside ordinary database
records.

## Firebase Integration

The project includes Firebase Cloud Firestore support for cloud-based
structured data.

Firestore can store:

-   User profiles
-   Notices
-   Departments
-   Categories
-   Priorities
-   Target audiences
-   Attachment metadata

Actual uploaded files should be stored separately in secure cloud
file/object storage.

## Authentication and Authorization

### Authentication

Authentication answers:

> Who is the user?

Users authenticate using their email and password.

### Authorization

Authorization answers:

> What is the user allowed to do?

``` text
STUDENT → Student Dashboard
FACULTY → Faculty Dashboard
ADMIN   → Admin Dashboard
```

A Student must not gain Faculty or Admin privileges by clicking another
user's profile, changing a URL, modifying frontend state, changing
locally stored role information, or directly calling a protected API.

## Security Requirements

-   Secure password handling
-   Role-based access control
-   Protected routes
-   Server-side authorization
-   Input validation
-   Protection against privilege escalation
-   Protection against unauthorized attachment access
-   File type and size validation
-   Safe server-generated storage names
-   Private storage for protected documents
-   No plaintext password storage
-   No hard-coded production secrets
-   Restricted Admin and Faculty operations

## Technology Stack

### Backend / Core Application

-   Java
-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   REST API
-   Jakarta Validation

### Android

-   Android Studio
-   Java as the target academic implementation language
-   XML layouts if a completely Java-based Android UI is required

### Cloud

-   Firebase Cloud Firestore
-   Firebase Authentication where applicable
-   Private cloud file/object storage for attachments

The final implementation should maintain one clear source of truth for
authentication, user roles, notices, and user data.

## Suggested Java Project Structure

``` text
src/main/java/com/smartcampus/noticeboard/

├── config/
├── controller/
├── dto/
├── model/
├── repository/
├── security/
├── service/
├── exception/
└── util/
```

Important components may include:

``` text
AuthController
AuthService
NoticeController
NoticeService
NoticeRepository
AttachmentController
AttachmentService
AttachmentRepository
FileStorageService
AudienceService
SecurityConfig
JwtAuthFilter
JwtUtil
```

## Basic System Workflow

``` text
User Opens Application
        |
        v
Login
        |
        v
Authenticate User
        |
        v
Load Trusted User Profile
        |
        v
Determine Role
        |
   +----+----+
   |    |    |
   v    v    v
Student Faculty Admin
   |    |    |
   v    v    v
Respective Dashboard
        |
        v
Retrieve Authorized Notices
        |
        v
Read / Search / Save / View Attachments
```

## Announcement Publishing Workflow

``` text
Admin / Authorized Faculty
        |
        v
Create Announcement
        |
        v
Enter Title and Content
        |
        v
Select Category and Priority
        |
        v
Select Target Audience
        |
        v
Add PDF / Image / Document
        |
        v
Validate and Upload File
        |
        v
Store Notice + Attachment Metadata
        |
        v
Publish
        |
        v
Authorized Students Receive Notice
```

## Local Storage to Cloud Migration

### Before

``` text
Android App
    |
    v
Local Storage
    |
SQLite / Room / Local Files
```

### After

``` text
Android App
    |
    v
Cloud Services
   / \
  /   \
 v     v
Cloud  Cloud File
DB     Storage
```

Benefits:

-   Access across devices
-   Centralized information
-   Easier synchronization
-   Better scalability
-   Easier backup and management
-   Improved campus-wide availability

Local caching may still be retained for performance or offline access
while the cloud remains the primary shared source.

## Example Use Case

The Examination Cell publishes:

``` text
Internal Examination Schedule

The second internal examination begins on Monday.

Target:
CSE | 2nd Year | Section A

Attachment:
Internal_Exam_Timetable.pdf
```

A CSE second-year Section A student logs in and sees the announcement.

The student can securely open the timetable PDF. A user outside the
permitted target audience should not be able to access the protected
notice or attachment.

## Testing

Important tests include:

-   Student login
-   Faculty login
-   Admin login
-   Registration
-   Logout
-   Student dashboard isolation
-   Faculty dashboard isolation
-   Admin dashboard isolation
-   Direct URL protection
-   Backend/API authorization
-   Notice creation, editing, and deletion
-   Department/year/section targeting
-   PDF and image upload
-   DOCX/PPTX/XLSX upload
-   Multiple attachment upload
-   Unauthorized attachment access
-   Invalid file rejection
-   Search
-   Saved notices
-   Cloud database read/write
-   Logout access protection

## Future Enhancements

-   Push notifications
-   Read/unread tracking
-   Notice acknowledgement
-   Scheduled announcements
-   Automatic notice expiry
-   Admin analytics
-   Offline synchronization
-   Faculty approval workflow
-   Audit logs
-   College email verification
-   Multilingual notices
-   Accessibility improvements
-   Malware scanning for uploaded documents

## Project Goal

> **The right announcement reaches the right student at the right
> time.**

The Smart Campus Notice Board System combines targeted communication,
role-based security, cloud accessibility, and document sharing into a
dedicated institutional platform.

## Project Status

The project is under active development and migration.

Current areas include:

-   Cloud database integration
-   Cloud attachment storage
-   Secure authentication
-   Role-based dashboard isolation
-   Announcement targeting
-   Document sharing
-   Kotlin-to-Java migration where required by the academic
    specification

## License

This project is developed for educational and academic purposes.
