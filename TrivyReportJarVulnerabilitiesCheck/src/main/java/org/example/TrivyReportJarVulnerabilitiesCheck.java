package org.example;

import org.example.dto.JSonRoot;
import org.example.dto.PackageDetails;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;



public class TrivyReportJarVulnerabilitiesCheck {

    private HashMap<String, PackageDetails> dependencyMap;

    public static final String TRIVY_REPORT_FILE = "report.json";

    public TrivyReportJarVulnerabilitiesCheck(ArrayList<String> POMDirectories) throws IOException, ParserConfigurationException, URISyntaxException, TransformerException, SAXException {
        BufferedReader reader = new BufferedReader(new FileReader(TRIVY_REPORT_FILE));
        JSonParser parser = new JSonParser(reader);
        JSonRoot r = parser.ParseJSON();
        reader.close();
        JarDependencies jarDependencies = new JarDependencies();
        jarDependencies.FindJarDependencies(r);
        dependencyMap = jarDependencies.getDependencyMap();
        for (int i = 0; i < POMDirectories.size(); i++) {
            CheckDependencies(POMDirectories.get(i)+"\\");
        }

    }

    private void CheckDependencies(String directory) 
            throws ParserConfigurationException, IOException, URISyntaxException, TransformerException, SAXException {
        XMLModifier modifier = new XMLModifier(directory,dependencyMap);
    }
}
