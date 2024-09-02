package org.example.dto;

import java.util.ArrayList;

public class JSonHeader {
    private String ArtifactName;
    private ArrayList<VulnerabilityPlatform> Results;

    public String getArtifactName() {
        return ArtifactName;
    }

    public void setArtifactName(String artifactName) {
        ArtifactName = artifactName;
    }

    public ArrayList<VulnerabilityPlatform> getResults() {
        return Results;
    }

    public void setResults(ArrayList<VulnerabilityPlatform> results) {
        Results = results;
    }
}
