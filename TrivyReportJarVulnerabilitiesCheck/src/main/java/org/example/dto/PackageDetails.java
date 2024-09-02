package org.example.dto;

public class PackageDetails {
    public String VulnerabilityID;
    public String PkgName;
    private String InstalledVersion;
    private String FixedVersion;
    private String[] fixedVersionList;
    public String Status;
    public String Severity;

    public String Description;

    public PackageNameNode nameNode;

    public String getVulnerabilityID() {
        return VulnerabilityID;
    }

    public void setVulnerabilityID(String vulnerabilityID) {
        VulnerabilityID = vulnerabilityID;
    }

    public String getPkgName() {
        return PkgName;
    }

    public void setPkgName(String pkgName) {
        PkgName = pkgName;
    }

    public String getInstalledVersion() {
        return InstalledVersion;
    }

    public void setInstalledVersion(String installedVersion) {
        InstalledVersion = installedVersion;
    }

    public String getFixedVersion() {
        return FixedVersion;
    }

    public void setFixedVersion(String fixedVersion) {
        FixedVersion = fixedVersion;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getSeverity() {
        return Severity;
    }

    public void setSeverity(String severity) {
        Severity = severity;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String[] getFixedVersionList() {
        return fixedVersionList;
    }

    public void setFixedVersionList(String[] fixedVersionList) {
        this.fixedVersionList = fixedVersionList;
    }

    public void SetNameNode(){
        String[] str = getPkgName().split(":");
        nameNode = new PackageNameNode(str[0],str[1]);
    }

    public PackageNameNode getNameNode() {
        return nameNode;
    }

    public PackageDetails(String pkgName, String installedVersion, String fixedVersion, String description) {
        PkgName = pkgName;
        InstalledVersion = installedVersion;
        FixedVersion = fixedVersion;
        Description = description;
        SetNameNode();
    }
}
