-- =====================================================================
-- HerPathway database schema
-- Dijalankan OTOMATIS oleh SchemaMigrator setiap aplikasi start (semua CREATE ... IF NOT EXISTS),
-- jadi cukup buat database kosong `herpath`. Bisa juga dijalankan manual di SQLyog.
-- Aturan: komentar hanya di awal baris dan tanpa titik koma di dalamnya.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS herpath
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE herpath;

-- Akun login ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS User (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    userType ENUM('mentee', 'mentor', 'organization') NOT NULL
);

-- Profil mentee (diisi saat login pertama) ------------------------------
CREATE TABLE IF NOT EXISTS form_Mentee (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId                 BIGINT NOT NULL UNIQUE,
    displayName            VARCHAR(255),
    realName               VARCHAR(255),
    phoneNumber            VARCHAR(20),
    email                  VARCHAR(100),
    address                VARCHAR(255),
    instagram              VARCHAR(255),
    linkedIn               VARCHAR(255),
    lastEducationLevel     VARCHAR(255),
    educationalInstitution VARCHAR(255),
    fieldOfStudy           VARCHAR(255),
    graduationYear         INT,
    currentJob             VARCHAR(255),
    companyName            VARCHAR(255),
    position               VARCHAR(255),
    workExperience         TEXT,
    mainGoal               TEXT,
    fieldOfStudyGoal       TEXT,
    expectations           TEXT,
    profilePicturePath     VARCHAR(255),
    status                 VARCHAR(20),
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

-- Profil mentor; foto & dokumen disimpan langsung sebagai BLOB (maks 1 MB per file)
CREATE TABLE IF NOT EXISTS form_Mentor (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId                 BIGINT NOT NULL UNIQUE,
    displayName            VARCHAR(255),
    realName               VARCHAR(255),
    phoneNumber            VARCHAR(20),
    email                  VARCHAR(100),
    address                VARCHAR(255),
    instagram              VARCHAR(255),
    linkedIn               VARCHAR(255),
    lastEducationLevel     VARCHAR(255),
    educationalInstitution VARCHAR(255),
    fieldOfStudy           VARCHAR(255),
    graduationYear         INT,
    currentJob             VARCHAR(255),
    companyName            VARCHAR(255),
    position               VARCHAR(255),
    workExperience         TEXT,
    profilePicture         MEDIUMBLOB,
    idCard                 MEDIUMBLOB,
    cv                     MEDIUMBLOB,
    certificate            MEDIUMBLOB,
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

-- Profil organisasi -----------------------------------------------------
CREATE TABLE IF NOT EXISTS form_Organization (
    id                             BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId                         BIGINT NOT NULL UNIQUE,
    organizationName               VARCHAR(255),
    address                        VARCHAR(255),
    visionMision                   TEXT,
    service                        TEXT,
    contact                        VARCHAR(255),
    phoneNumber                    VARCHAR(20),
    email                          VARCHAR(100),
    website                        VARCHAR(255),
    description                    TEXT,
    target                         TEXT,
    methodology                    TEXT,
    programOutcomes                TEXT,
    organizationRegistrationNumber VARCHAR(255),
    foundingDocumentPath           VARCHAR(255),
    financialReportPath            VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

-- Kuisioner / lamaran mentee ke mentor. status: pending | accepted | declined
CREATE TABLE IF NOT EXISTS kuisioner (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    userIdMentee            BIGINT NOT NULL,
    userIdMentor            BIGINT NOT NULL,
    experience_level_mentee VARCHAR(50) NOT NULL,
    beneficial_aspects      TEXT,
    suggestions             TEXT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'pending',
    createdAt               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userIdMentee) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (userIdMentor) REFERENCES User(id) ON DELETE CASCADE
);

-- Mentor yang ditugaskan ke mentee ---------------------------------------
CREATE TABLE IF NOT EXISTS mentor_assignment (
    menteeId BIGINT NOT NULL PRIMARY KEY,
    mentorId BIGINT NOT NULL,
    FOREIGN KEY (menteeId) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (mentorId) REFERENCES User(id) ON DELETE CASCADE
);

-- Riwayat chat mentee <-> mentor ----------------------------------------
CREATE TABLE IF NOT EXISTS ChatSession (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    menteeId   BIGINT NOT NULL,
    mentorId   BIGINT NOT NULL,
    senderId   BIGINT NOT NULL,
    receiverId BIGINT NOT NULL,
    message    TEXT NOT NULL,
    timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menteeId)   REFERENCES form_Mentee(userId) ON DELETE CASCADE,
    FOREIGN KEY (mentorId)   REFERENCES form_Mentor(userId) ON DELETE CASCADE,
    FOREIGN KEY (senderId)   REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (receiverId) REFERENCES User(id) ON DELETE CASCADE,
    INDEX idx_chat_pair (menteeId, mentorId, timestamp)
);

-- Forum: thread, balasan, dan like ---------------------------------------
CREATE TABLE IF NOT EXISTS forum_thread (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    authorId  BIGINT NOT NULL,
    category  VARCHAR(40) NOT NULL,
    title     VARCHAR(200) NOT NULL,
    body      TEXT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorId) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS forum_reply (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    threadId  BIGINT NOT NULL,
    authorId  BIGINT NOT NULL,
    body      TEXT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (threadId) REFERENCES forum_thread(id) ON DELETE CASCADE,
    FOREIGN KEY (authorId) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS forum_like (
    threadId BIGINT NOT NULL,
    userId   BIGINT NOT NULL,
    PRIMARY KEY (threadId, userId),
    FOREIGN KEY (threadId) REFERENCES forum_thread(id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);

-- Konten belajar. type: article | video | book. authorId NULL = konten bawaan HerPathway
CREATE TABLE IF NOT EXISTS learning_content (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    authorId  BIGINT NULL,
    type      VARCHAR(20) NOT NULL,
    title     VARCHAR(200) NOT NULL,
    summary   VARCHAR(500),
    body      MEDIUMTEXT,
    url       VARCHAR(500),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorId) REFERENCES User(id) ON DELETE CASCADE
);
