package org.endeavourhealth.core.database.dal.datasharingmanager.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonExtractTechnicalDetails {

    private String uuid = null;
    private String name = null;
    private String sftpHostName = null;
    private String sftpHostDirectory = null;
    private String sftpHostPort = null;
    private String sftpClientUsername = null;
    private String sftpClientPrivateKeyPassword = null;
    private String sftpHostPublicKeyFilename = null;
    private String sftpHostPublicKeyFileData = null;
    private String sftpClientPrivateKeyFilename = null;
    private String sftpClientPrivateKeyFileData = null;
    private String pgpCustomerPublicKeyFilename = null;
    private String pgpCustomerPublicKeyFileData = null;
    private String pgpInternalPublicKeyFilename = null;
    private String pgpInternalPublicKeyFileData = null;
    private Short outputFormat;
    private Short securityInfrastructure;
    private Short securityArchitecture;

    public JsonExtractTechnicalDetails() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSftpHostName() {
        return sftpHostName;
    }

    public void setSftpHostName(String sftpHostName) {
        this.sftpHostName = sftpHostName;
    }

    public String getSftpHostDirectory() {
        return sftpHostDirectory;
    }

    public void setSftpHostDirectory(String sftpHostDirectory) {
        this.sftpHostDirectory = sftpHostDirectory;
    }

    public String getSftpHostPort() {
        return sftpHostPort;
    }

    public void setSftpHostPort(String sftpHostPort) {
        this.sftpHostPort = sftpHostPort;
    }

    public String getSftpClientUsername() {
        return sftpClientUsername;
    }

    public void setSftpClientUsername(String sftpClientUsername) {
        this.sftpClientUsername = sftpClientUsername;
    }

    public String getSftpClientPrivateKeyPassword() {
        return sftpClientPrivateKeyPassword;
    }

    public void setSftpClientPrivateKeyPassword(String sftpClientPrivateKeyPassword) {
        this.sftpClientPrivateKeyPassword = sftpClientPrivateKeyPassword;
    }

    public String getSftpHostPublicKeyFilename() {
        return sftpHostPublicKeyFilename;
    }

    public void setSftpHostPublicKeyFilename(String sftpHostPublicKeyFilename) {
        this.sftpHostPublicKeyFilename = sftpHostPublicKeyFilename;
    }

    public String getSftpHostPublicKeyFileData() {
        return sftpHostPublicKeyFileData;
    }

    public void setSftpHostPublicKeyFileData(String sftpHostPublicKeyFileData) {
        this.sftpHostPublicKeyFileData = sftpHostPublicKeyFileData;
    }

    public String getSftpClientPrivateKeyFilename() {
        return sftpClientPrivateKeyFilename;
    }

    public void setSftpClientPrivateKeyFilename(String sftpClientPrivateKeyFilename) {
        this.sftpClientPrivateKeyFilename = sftpClientPrivateKeyFilename;
    }

    public String getSftpClientPrivateKeyFileData() {
        return sftpClientPrivateKeyFileData;
    }

    public void setSftpClientPrivateKeyFileData(String sftpClientPrivateKeyFileData) {
        this.sftpClientPrivateKeyFileData = sftpClientPrivateKeyFileData;
    }

    public String getPgpCustomerPublicKeyFilename() {
        return pgpCustomerPublicKeyFilename;
    }

    public void setPgpCustomerPublicKeyFilename(String pgpCustomerPublicKeyFilename) {
        this.pgpCustomerPublicKeyFilename = pgpCustomerPublicKeyFilename;
    }

    public String getPgpCustomerPublicKeyFileData() {
        return pgpCustomerPublicKeyFileData;
    }

    public void setPgpCustomerPublicKeyFileData(String pgpCustomerPublicKeyFileData) {
        this.pgpCustomerPublicKeyFileData = pgpCustomerPublicKeyFileData;
    }

    public String getPgpInternalPublicKeyFilename() {
        return pgpInternalPublicKeyFilename;
    }

    public void setPgpInternalPublicKeyFilename(String pgpInternalPublicKeyFilename) {
        this.pgpInternalPublicKeyFilename = pgpInternalPublicKeyFilename;
    }

    public String getPgpInternalPublicKeyFileData() {
        return pgpInternalPublicKeyFileData;
    }

    public void setPgpInternalPublicKeyFileData(String pgpInternalPublicKeyFileData) {
        this.pgpInternalPublicKeyFileData = pgpInternalPublicKeyFileData;
    }

    public Short getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(Short outputFormat) {
        this.outputFormat = outputFormat;
    }

    public Short getSecurityInfrastructure() {
        return securityInfrastructure;
    }

    public void setSecurityInfrastructure(Short securityInfrastructure) {
        this.securityInfrastructure = securityInfrastructure;
    }

    public Short getSecurityArchitecture() {
        return securityArchitecture;
    }

    public void setSecurityArchitecture(Short securityArchitecture) {
        this.securityArchitecture = securityArchitecture;
    }

    //TODO: Improve formatting here!
    @Override
    public String toString() {
        return "Uuid: " + uuid +
                ", Name: " + name +
                ", SFTP Host Name: " + sftpHostName +
                ", SFTP Host Directory: " + sftpHostDirectory +
                ", SFTP Host Port: " + sftpHostPort +
                ", SFTP Client Username: " + sftpClientUsername +
                ", SFTP Client Private Key Password: " + sftpClientPrivateKeyPassword +
                ", SFTP Host Public Key: " + sftpHostPublicKeyFilename +
                ", SFTP Client Private Key: " + sftpClientPrivateKeyFilename +
                ", PGP Customer Public Key: " + pgpCustomerPublicKeyFilename +
                ", PGP Internal Public Key: " + pgpInternalPublicKeyFilename +
                ", Output Format: " + outputFormat +
                ", Security Infrastructure: " + securityInfrastructure +
                ", Security Architecture: " + securityArchitecture;
    }
}
