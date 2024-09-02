package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        try{
            File pomDirectories = new File("pom_directories.txt");
            Scanner sc = new Scanner(pomDirectories);
            ArrayList<String> directoriesList = new ArrayList<>();
            while(sc.hasNextLine()){
                String directory = sc.nextLine();
                directoriesList.add(directory);
            }
            sc.close();
            TrivyReportJarVulnerabilitiesCheck checking =
                    new TrivyReportJarVulnerabilitiesCheck(directoriesList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}