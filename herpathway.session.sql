-- Buat database herpath jika belum ada
CREATE DATABASE IF NOT EXISTS herpath;

-- Gunakan database herpath
USE herpath;

CREATE TABLE IF NOT EXISTS User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    userType ENUM('mentee', 'mentor', 'organization') NOT NULL
);

USE herpath;
CREATE TABLE IF NOT EXISTS form_Mentee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    displayName VARCHAR(255),
    realName VARCHAR(255),
    phoneNumber VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    instagram VARCHAR(255),
    linkedIn VARCHAR(255),
    lastEducationLevel VARCHAR(255),
    educationalInstitution VARCHAR(255),
    fieldOfStudy VARCHAR(255),
    graduationYear INT,
    currentJob VARCHAR(255),
    companyName VARCHAR(255),
    position VARCHAR(255),
    workExperience TEXT,
    mainGoal TEXT,
    fieldOfStudyGoal TEXT,
    expectations TEXT,
    profilePicturePath VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE
);


USE herpath;


CREATE TABLE IF NOT EXISTS form_Mentor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    displayName VARCHAR(255),
    realName VARCHAR(255),
    phoneNumber VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    instagram VARCHAR(255),
    linkedIn VARCHAR(255),
    lastEducationLevel VARCHAR(255),
    educationalInstitution VARCHAR(255),
    fieldOfStudy VARCHAR(255),
    graduationYear INT,
    currentJob VARCHAR(255),
    companyName VARCHAR(255),
    position VARCHAR(255),
    workExperience VARCHAR(255),
    profilePicturePath VARCHAR(255),
    idCardPath VARCHAR(255),
    cvPath VARCHAR(255),
    certificatePath VARCHAR(255),
    FOREIGN KEY (UserId) REFERENCES User(id) ON DELETE CASCADE
);

USE herpath;

CREATE TABLE IF NOT EXISTS form_Organization (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    organizationName VARCHAR(255),
    address VARCHAR(255),
    visionMision TEXT,
    service TEXT,
    contact VARCHAR(255),
    phoneNumber VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(255),
    description TEXT,
    target TEXT,
    methodology TEXT,
    programOutcomes TEXT,
    organizationRegistrationNumber VARCHAR(255),
    foundingDocumentPath VARCHAR(255),
    financialReportPath VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES User(id)
);




