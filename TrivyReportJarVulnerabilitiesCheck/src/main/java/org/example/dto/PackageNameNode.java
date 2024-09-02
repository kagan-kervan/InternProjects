package org.example.dto;

public class PackageNameNode {

    private String groupID;
    private String artifactID;

    public PackageNameNode(String groupID, String artifactID) {
        this.groupID = groupID;
        this.artifactID = artifactID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public void setArtifactID(String artifactID) {
        this.artifactID = artifactID;
    }
}
