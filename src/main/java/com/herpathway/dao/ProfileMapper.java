package com.herpathway.dao;

import com.herpathway.model.PersonalProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Maps the columns shared by form_Mentee and form_Mentor. */
final class ProfileMapper {

    /** Column order used by {@link #bindCommon}; keep the two in sync. */
    static final String COMMON_COLUMNS = "displayName, realName, phoneNumber, email, address, instagram, linkedIn, "
            + "lastEducationLevel, educationalInstitution, fieldOfStudy, graduationYear, currentJob, companyName, "
            + "position, workExperience";

    static final String COMMON_ASSIGNMENTS = "displayName = ?, realName = ?, phoneNumber = ?, email = ?, address = ?, "
            + "instagram = ?, linkedIn = ?, lastEducationLevel = ?, educationalInstitution = ?, fieldOfStudy = ?, "
            + "graduationYear = ?, currentJob = ?, companyName = ?, position = ?, workExperience = ?";

    static final int COMMON_COUNT = 15;

    private ProfileMapper() {
    }

    static void readCommon(ResultSet rs, PersonalProfile p) throws SQLException {
        p.setUserId(rs.getLong("userId"));
        p.setDisplayName(rs.getString("displayName"));
        p.setRealName(rs.getString("realName"));
        p.setPhoneNumber(rs.getString("phoneNumber"));
        p.setEmail(rs.getString("email"));
        p.setAddress(rs.getString("address"));
        p.setInstagram(rs.getString("instagram"));
        p.setLinkedIn(rs.getString("linkedIn"));
        p.setLastEducationLevel(rs.getString("lastEducationLevel"));
        p.setEducationalInstitution(rs.getString("educationalInstitution"));
        p.setFieldOfStudy(rs.getString("fieldOfStudy"));
        p.setGraduationYear(rs.getInt("graduationYear"));
        p.setCurrentJob(rs.getString("currentJob"));
        p.setCompanyName(rs.getString("companyName"));
        p.setPosition(rs.getString("position"));
        p.setWorkExperience(rs.getString("workExperience"));
    }

    /**
     * Binds the {@link #COMMON_COUNT} shared fields starting at parameter {@code start}.
     *
     * @return the next free parameter index
     */
    static int bindCommon(PreparedStatement ps, int start, PersonalProfile p) throws SQLException {
        int i = start;
        ps.setString(i++, p.getDisplayName());
        ps.setString(i++, p.getRealName());
        ps.setString(i++, p.getPhoneNumber());
        ps.setString(i++, p.getEmail());
        ps.setString(i++, p.getAddress());
        ps.setString(i++, p.getInstagram());
        ps.setString(i++, p.getLinkedIn());
        ps.setString(i++, p.getLastEducationLevel());
        ps.setString(i++, p.getEducationalInstitution());
        ps.setString(i++, p.getFieldOfStudy());
        ps.setInt(i++, p.getGraduationYear());
        ps.setString(i++, p.getCurrentJob());
        ps.setString(i++, p.getCompanyName());
        ps.setString(i++, p.getPosition());
        ps.setString(i++, p.getWorkExperience());
        return i;
    }

    /** "?, ?, ?" with {@code n} placeholders. */
    static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        return sb.toString();
    }
}
