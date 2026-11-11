package com.herpathway.model;

/** A row of form_Organization. */
public class OrganizationProfile {

    private long userId;
    private String organizationName;
    private String address;
    private String visionMision;
    private String service;
    private String contact;
    private String phoneNumber;
    private String email;
    private String website;
    private String description;
    private String target;
    private String methodology;
    private String programOutcomes;
    private String organizationRegistrationNumber;
    private String foundingDocumentPath;
    private String financialReportPath;

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getVisionMision() { return visionMision; }
    public void setVisionMision(String visionMision) { this.visionMision = visionMision; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getProgramOutcomes() { return programOutcomes; }
    public void setProgramOutcomes(String programOutcomes) { this.programOutcomes = programOutcomes; }

    public String getOrganizationRegistrationNumber() { return organizationRegistrationNumber; }
    public void setOrganizationRegistrationNumber(String n) { this.organizationRegistrationNumber = n; }

    public String getFoundingDocumentPath() { return foundingDocumentPath; }
    public void setFoundingDocumentPath(String foundingDocumentPath) { this.foundingDocumentPath = foundingDocumentPath; }

    public String getFinancialReportPath() { return financialReportPath; }
    public void setFinancialReportPath(String financialReportPath) { this.financialReportPath = financialReportPath; }
}
