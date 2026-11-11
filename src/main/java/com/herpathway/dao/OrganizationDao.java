package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.OrganizationProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Organization profiles (table {@code form_Organization}). */
public class OrganizationDao {

    /** Text columns, in the order {@link #bindText} sets them. */
    private static final String[] TEXT_COLUMNS = {
            "organizationName", "address", "visionMision", "service", "contact", "phoneNumber", "email", "website",
            "description", "target", "methodology", "programOutcomes", "organizationRegistrationNumber"};

    public void insert(OrganizationProfile o) throws SQLException {
        String sql = "INSERT INTO form_Organization (" + String.join(", ", TEXT_COLUMNS)
                + ", foundingDocumentPath, financialReportPath, userId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bindText(ps, o);
            ps.setString(i++, o.getFoundingDocumentPath());
            ps.setString(i++, o.getFinancialReportPath());
            ps.setLong(i, o.getUserId());
            ps.executeUpdate();
        }
    }

    /** Updates the text fields only; uploaded documents are left untouched. */
    public void update(OrganizationProfile o) throws SQLException {
        String sql = "UPDATE form_Organization SET " + String.join(" = ?, ", TEXT_COLUMNS) + " = ? WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bindText(ps, o);
            ps.setLong(i, o.getUserId());
            ps.executeUpdate();
        }
    }

    /** @return the profile, or {@code null} if the organization hasn't filled it in */
    public OrganizationProfile findByUserId(long userId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM form_Organization WHERE userId = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                OrganizationProfile o = new OrganizationProfile();
                o.setUserId(userId);
                o.setOrganizationName(rs.getString("organizationName"));
                o.setAddress(rs.getString("address"));
                o.setVisionMision(rs.getString("visionMision"));
                o.setService(rs.getString("service"));
                o.setContact(rs.getString("contact"));
                o.setPhoneNumber(rs.getString("phoneNumber"));
                o.setEmail(rs.getString("email"));
                o.setWebsite(rs.getString("website"));
                o.setDescription(rs.getString("description"));
                o.setTarget(rs.getString("target"));
                o.setMethodology(rs.getString("methodology"));
                o.setProgramOutcomes(rs.getString("programOutcomes"));
                o.setOrganizationRegistrationNumber(rs.getString("organizationRegistrationNumber"));
                o.setFoundingDocumentPath(rs.getString("foundingDocumentPath"));
                o.setFinancialReportPath(rs.getString("financialReportPath"));
                return o;
            }
        }
    }

    /** @return the next free parameter index */
    private static int bindText(PreparedStatement ps, OrganizationProfile o) throws SQLException {
        int i = 1;
        ps.setString(i++, o.getOrganizationName());
        ps.setString(i++, o.getAddress());
        ps.setString(i++, o.getVisionMision());
        ps.setString(i++, o.getService());
        ps.setString(i++, o.getContact());
        ps.setString(i++, o.getPhoneNumber());
        ps.setString(i++, o.getEmail());
        ps.setString(i++, o.getWebsite());
        ps.setString(i++, o.getDescription());
        ps.setString(i++, o.getTarget());
        ps.setString(i++, o.getMethodology());
        ps.setString(i++, o.getProgramOutcomes());
        ps.setString(i++, o.getOrganizationRegistrationNumber());
        return i;
    }
}
