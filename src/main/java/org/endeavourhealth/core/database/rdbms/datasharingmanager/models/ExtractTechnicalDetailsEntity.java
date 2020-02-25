package org.endeavourhealth.core.database.rdbms.datasharingmanager.models;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonExtractTechnicalDetails;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "extract_technical_details", schema = "data_sharing_manager")
public class ExtractTechnicalDetailsEntity {

    private String uuid;
    private String name;
    private String sftpHostName;
    private String sftpHostDirectory;
    private String sftpHostPort;
    private String sftpClientUsername;
    private String sftpClientPrivateKeyPassword;
    private String sftpHostPublicKeyFilename;
    private String sftpHostPublicKeyFileData;
    private String sftpClientPrivateKeyFilename;
    private String sftpClientPrivateKeyFileData;
    private String pgpCustomerPublicKeyFilename;
    private String pgpCustomerPublicKeyFileData;
    private String pgpInternalPublicKeyFilename;
    private String pgpInternalPublicKeyFileData;
    private Short outputFormat;
    private Short securityInfrastructure;
    private Short securityArchitecture;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "sftp_host_name")
    public String getSftpHostName() {
        return sftpHostName;
    }

    public void setSftpHostName(String sftpHostName) {
        this.sftpHostName = sftpHostName;
    }

    @Basic
    @Column(name = "sftp_host_directory")
    public String getSftpHostDirectory() {
        return sftpHostDirectory;
    }

    public void setSftpHostDirectory(String sftpHostDirectory) {
        this.sftpHostDirectory = sftpHostDirectory;
    }

    @Basic
    @Column(name = "sftp_host_port")
    public String getSftpHostPort() {
        return sftpHostPort;
    }

    public void setSftpHostPort(String sftpHostPort) {
        this.sftpHostPort = sftpHostPort;
    }

    @Basic
    @Column(name = "sftp_client_username")
    public String getSftpClientUsername() {
        return sftpClientUsername;
    }

    public void setSftpClientUsername(String sftpClientUsername) {
        this.sftpClientUsername = sftpClientUsername;
    }

    @Basic
    @Column(name = "sftp_client_private_key_password")
    public String getSftpClientPrivateKeyPassword() {
        return sftpClientPrivateKeyPassword;
    }

    public void setSftpClientPrivateKeyPassword(String sftpClientPrivateKeyPassword) {
        this.sftpClientPrivateKeyPassword = sftpClientPrivateKeyPassword;
    }

    @Basic
    @Column(name = "sftp_host_public_key_filename")
    public String getSftpHostPublicKeyFilename() {
        return sftpHostPublicKeyFilename;
    }

    public void setSftpHostPublicKeyFilename(String sftpHostPublicKeyFilename) {
        this.sftpHostPublicKeyFilename = sftpHostPublicKeyFilename;
    }

    @Basic
    @Column(name = "sftp_host_public_key_fileData")
    public String getSftpHostPublicKeyFileData() {
        return sftpHostPublicKeyFileData;
    }

    public void setSftpHostPublicKeyFileData(String sftpHostPublicKeyFileData) {
        this.sftpHostPublicKeyFileData = sftpHostPublicKeyFileData;
    }

    @Basic
    @Column(name = "sftp_client_private_key_filename")
    public String getSftpClientPrivateKeyFilename() {
        return sftpClientPrivateKeyFilename;
    }

    public void setSftpClientPrivateKeyFilename(String sftpClientPrivateKeyFilename) {
        this.sftpClientPrivateKeyFilename = sftpClientPrivateKeyFilename;
    }

    @Basic
    @Column(name = "sftp_client_private_key_fileData")
    public String getSftpClientPrivateKeyFileData() {
        return sftpClientPrivateKeyFileData;
    }

    public void setSftpClientPrivateKeyFileData(String sftpClientPrivateKeyFileData) {
        this.sftpClientPrivateKeyFileData = sftpClientPrivateKeyFileData;
    }

    @Basic
    @Column(name = "pgp_customer_public_key_filename")
    public String getPgpCustomerPublicKeyFilename() {
        return pgpCustomerPublicKeyFilename;
    }

    public void setPgpCustomerPublicKeyFilename(String pgpCustomerPublicKeyFilename) {
        this.pgpCustomerPublicKeyFilename = pgpCustomerPublicKeyFilename;
    }

    @Basic
    @Column(name = "pgp_customer_public_key_fileData")
    public String getPgpCustomerPublicKeyFileData() {
        return pgpCustomerPublicKeyFileData;
    }

    public void setPgpCustomerPublicKeyFileData(String pgpCustomerPublicKeyFileData) {
        this.pgpCustomerPublicKeyFileData = pgpCustomerPublicKeyFileData;
    }

    @Basic
    @Column(name = "pgp_internal_public_key_filename")
    public String getPgpInternalPublicKeyFilename() {
        return pgpInternalPublicKeyFilename;
    }

    public void setPgpInternalPublicKeyFilename(String pgpInternalPublicKeyFilename) {
        this.pgpInternalPublicKeyFilename = pgpInternalPublicKeyFilename;
    }

    @Basic
    @Column(name = "pgp_internal_public_key_fileData")
    public String getPgpInternalPublicKeyFileData() {
        return pgpInternalPublicKeyFileData;
    }

    public void setPgpInternalPublicKeyFileData(String pgpInternalPublicKeyFileData) {
        this.pgpInternalPublicKeyFileData = pgpInternalPublicKeyFileData;
    }

    @Basic
    @Column(name = "output_format")
    public Short getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(Short outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Basic
    @Column(name = "security_infrastructure")
    public Short getSecurityInfrastructure() {
        return securityInfrastructure;
    }

    public void setSecurityInfrastructure(Short securityInfrastructure) {
        this.securityInfrastructure = securityInfrastructure;
    }

    @Basic
    @Column(name = "security_architecture")
    public Short getSecurityArchitecture() {
        return securityArchitecture;
    }

    public void setSecurityArchitecture(Short securityArchitecture) {
        this.securityArchitecture = securityArchitecture;
    }

    public boolean equals(JsonExtractTechnicalDetails that) {
        if (that == null) return false;
        return Objects.equals(uuid, that.getUuid()) &&
                Objects.equals(name, that.getName()) &&
                Objects.equals(sftpHostName, that.getSftpHostName()) &&
                Objects.equals(sftpHostDirectory, that.getSftpHostDirectory()) &&
                Objects.equals(sftpHostPort, that.getSftpHostPort()) &&
                Objects.equals(sftpClientUsername, that.getSftpClientUsername()) &&
                Objects.equals(sftpClientPrivateKeyPassword, that.getSftpClientPrivateKeyPassword()) &&
                Objects.equals(sftpHostPublicKeyFilename, that.getSftpHostPublicKeyFilename()) &&
                Objects.equals(sftpHostPublicKeyFileData, that.getSftpHostPublicKeyFileData()) &&
                Objects.equals(sftpClientPrivateKeyFilename, that.getSftpClientPrivateKeyFilename()) &&
                Objects.equals(sftpClientPrivateKeyFileData, that.getSftpClientPrivateKeyFileData()) &&
                Objects.equals(pgpCustomerPublicKeyFilename, that.getPgpCustomerPublicKeyFilename()) &&
                Objects.equals(pgpCustomerPublicKeyFileData, that.getPgpCustomerPublicKeyFileData()) &&
                Objects.equals(pgpInternalPublicKeyFilename, that.getPgpInternalPublicKeyFilename()) &&
                Objects.equals(outputFormat, that.getOutputFormat()) &&
                Objects.equals(securityInfrastructure, that.getSecurityInfrastructure()) &&
                Objects.equals(securityArchitecture, that.getSecurityArchitecture());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtractTechnicalDetailsEntity that = (ExtractTechnicalDetailsEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(sftpHostName, that.sftpHostName) &&
                Objects.equals(sftpHostDirectory, that.sftpHostDirectory) &&
                Objects.equals(sftpHostPort, that.sftpHostPort) &&
                Objects.equals(sftpClientUsername, that.sftpClientUsername) &&
                Objects.equals(sftpClientPrivateKeyPassword, that.sftpClientPrivateKeyPassword) &&
                Objects.equals(sftpHostPublicKeyFilename, that.sftpHostPublicKeyFilename) &&
                Objects.equals(sftpHostPublicKeyFileData, that.sftpHostPublicKeyFileData) &&
                Objects.equals(sftpClientPrivateKeyFilename, that.sftpClientPrivateKeyFilename) &&
                Objects.equals(sftpClientPrivateKeyFileData, that.sftpClientPrivateKeyFileData) &&
                Objects.equals(pgpCustomerPublicKeyFilename, that.pgpCustomerPublicKeyFilename) &&
                Objects.equals(pgpCustomerPublicKeyFileData, that.pgpCustomerPublicKeyFileData) &&
                Objects.equals(pgpInternalPublicKeyFilename, that.pgpInternalPublicKeyFilename) &&
                Objects.equals(pgpInternalPublicKeyFileData, that.pgpInternalPublicKeyFileData) &&
                Objects.equals(outputFormat, that.outputFormat) &&
                Objects.equals(securityInfrastructure, that.securityInfrastructure) &&
                Objects.equals(securityArchitecture, that.securityArchitecture);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, sftpHostName, sftpHostDirectory, sftpHostPort, sftpClientUsername, sftpClientPrivateKeyPassword,
                sftpHostPublicKeyFilename, sftpHostPublicKeyFileData, sftpClientPrivateKeyFilename, sftpClientPrivateKeyFileData,
                pgpCustomerPublicKeyFilename, pgpCustomerPublicKeyFileData, pgpInternalPublicKeyFilename, pgpInternalPublicKeyFileData,
                outputFormat, securityInfrastructure, securityArchitecture);
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
