package org.example;

import org.example.dto.JSonHeader;
import org.example.dto.JSonRoot;
import org.example.dto.PackageDetails;
import org.example.dto.VulnerabilityPlatform;

import java.util.*;

public class JarDependencies {

    private HashMap<String, PackageDetails> dependencyMap;

    private final String springBootDependenciesFixedVersion = "3.3.2";
    private final String guavaDependencyFixedVersion = "33.3.0-jre";

    //Create comparator object for sorting the version descending.
    public static Comparator<String> versionComparator = new Comparator<String>() {
        @Override
        public int compare(String v1, String v2) {
            String[] version1 = v1.split("\\.");
            String[] version2 = v2.split("\\.");
            int len = Math.max(version1.length, version2.length);

            for (int i = 0; i < len; i++) {
                String part1 = i < version1.length ? version1[i] : "0";
                String part2 = i < version2.length ? version2[i] : "0";

                String[] part1Tokens = part1.split("-|\\+");
                String[] part2Tokens = part2.split("-|\\+");

                for (int j = 0; j < Math.max(part1Tokens.length, part2Tokens.length); j++) {
                    String token1 = j < part1Tokens.length ? part1Tokens[j] : "0";
                    String token2 = j < part2Tokens.length ? part2Tokens[j] : "0";

                    try {
                        int num1 = Integer.parseInt(token1);
                        int num2 = Integer.parseInt(token2);
                        int comparison = Integer.compare(num1, num2);
                        if (comparison != 0) {
                            return comparison;
                        }
                    } catch (NumberFormatException e) {
                        int comparison = token1.compareTo(token2);
                        if (comparison != 0) {
                            return comparison;
                        }
                    }
                }
            }
            return 0;
        }
    };


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

        AddExtraDependenciesToManagementEntry();
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

    private void AddExtraDependenciesToManagementEntry(){
        String packageName = "org.springframework.boot:spring-boot-dependencies";
        String installedVersion = "3.2.0";
        String description = "Configuration for Dependency management's element: spring-boot-dependencies";
        PackageDetails packageDetails = new PackageDetails(packageName,installedVersion,springBootDependenciesFixedVersion,description);
        dependencyMap.put(packageDetails.getNameNode().getArtifactID(),packageDetails);
        packageName = "com.google.guava:guava";
        installedVersion="32.0.0";
        description = "Guava dependency vulnerability entry";
        PackageDetails guavaPackageDetails = new PackageDetails(packageName,installedVersion,guavaDependencyFixedVersion,description);
        dependencyMap.put(guavaPackageDetails.getNameNode().getArtifactID(),guavaPackageDetails);

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
