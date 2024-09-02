package org.example;

import org.example.dto.JSonHeader;
import org.example.dto.JSonRoot;
import org.example.dto.PackageDetails;
import org.example.dto.VulnerabilityPlatform;

import java.awt.*;
import java.util.*;

public class JarDependencies {

    private HashMap<String, PackageDetails> dependencyMap;

    private final String springBootDependenciesFixedVersion = "3.3.2";

    public JarDependencies() {
        this.dependencyMap = new HashMap<>();
    }

    public HashMap<String, PackageDetails> getDependencyMap() {
        return dependencyMap;
    }

    public void setDependencyMap(HashMap<String, PackageDetails> dependencyMap) {
        this.dependencyMap = dependencyMap;
    }

    public void FindJarDependencies(JSonRoot root){
        ArrayList<JSonHeader> headerList = root.getRoot();
        for (int i = 0; i < headerList.size(); i++) {
            JSonHeader header = headerList.get(i);
            IterateHeader(header);
        }
        //Create comparator object for sorting the version descending.
        Comparator<String> versionComparator = new Comparator<String>() {
            @Override
            public int compare(String v1, String v2) {
                String[] version1 = v1.split("\\.");
                String[] version2 = v2.split("\\.");
                int len = Math.max(version1.length, version2.length);
                for (int i = 0; i < len; i++) {
                    Integer num1 = 0;
                    if(i< version1.length)
                        num1 = Integer.parseInt(version1[i]);
                    Integer num2 = 0;
                    if(i< version2.length)
                        num2 = Integer.parseInt(version2[i]);
                    int comparison = num2.compareTo(num1);
                    if(comparison != 0)
                        return comparison;
                }
                return 0;
            }
        };
        AddSpringDependencyManagementEntry(springBootDependenciesFixedVersion);
        SortFixedVersionEntries(versionComparator);
    }

    public void IterateHeader(JSonHeader header){
        ArrayList<VulnerabilityPlatform> vulnerabilityPlatformList = header.getResults();
        for (int i = 0; i < vulnerabilityPlatformList.size(); i++) {
            VulnerabilityPlatform platform = vulnerabilityPlatformList.get(i);
            if(platform.getType() == null)
                continue;
            if(platform.getType().equalsIgnoreCase("jar")){
                IterateVulnerabilityEntries(platform);
            }
        }
    }

    private void IterateVulnerabilityEntries(VulnerabilityPlatform vulnerabilityPlatform){
        ArrayList<PackageDetails> vulnerabilityList = vulnerabilityPlatform.getVulnerabilities();
        for (int i = 0; i < vulnerabilityList.size(); i++) {
            PackageDetails packageDetails = vulnerabilityList.get(i);
            packageDetails.SetNameNode();
            //If we have vulnerability entry before.
            if(dependencyMap.containsKey(packageDetails.getPkgName())){
                dependencyMap.get(packageDetails.getNameNode().getArtifactID())
                        .setFixedVersion(MergeVersionStrings(packageDetails.getPkgName(), packageDetails.getFixedVersion()));
            }
            else{
                dependencyMap.put(packageDetails.getNameNode().getArtifactID(), packageDetails);
            }
        }
    }

    private String MergeVersionStrings(String key, String versionString){
        String existingString = dependencyMap.get(key).getFixedVersion();
        String[] versions = versionString.split(", ");
        for (int i = 0; i < versions.length; i++) {
            if(!existingString.contains(versions[i])){
                //Adds the "," punctuation mark
                existingString = existingString + ", ";
                //Adds the missing fixed version.
                existingString = existingString + versions[i];
            }
        }
        return existingString;
    }


    private void SortFixedVersionEntries(Comparator<String> comparator){
       Collection<PackageDetails> packageDetailsCollection = dependencyMap.values();
       PackageDetails[] packageDetailsList = packageDetailsCollection.toArray(new PackageDetails[0]);
        for (int i = 0; i < packageDetailsList.length; i++) {
            String[] versions = packageDetailsList[i].getFixedVersion().split(", ");
            Arrays.sort(versions, comparator);
            dependencyMap.get(packageDetailsList[i].getNameNode().getArtifactID()).setFixedVersion(ArrayToString(versions));
            dependencyMap.get(packageDetailsList[i].getNameNode().getArtifactID()).setFixedVersionList(versions);

        }
    }

    private void AddSpringDependencyManagementEntry(String fixedVersionInput){
        String packageName = "org.springframework.boot:spring-boot-dependencies";
        String installedVersion = "3.2.0";
        String fixedVersion = fixedVersionInput;
        String description = "Configuration for Dependency management's element: spring-boot-dependencies";
        PackageDetails packageDetails = new PackageDetails(packageName,installedVersion,fixedVersion,description);
        dependencyMap.put(packageDetails.getNameNode().getArtifactID(),packageDetails);

    }

    private String ArrayToString(String[] strArray){
        int i = 0;
        String string = "";
        while (i < strArray.length){
            string = string + strArray[i];
            if(i<strArray.length-1)
                string = string +", ";
            i ++;
        }
        return string;
    }
}
