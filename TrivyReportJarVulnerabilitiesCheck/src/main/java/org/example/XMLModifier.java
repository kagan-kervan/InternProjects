package org.example;

import lombok.extern.java.Log;
import org.example.dto.PackageDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;

@Log
public class XMLModifier {
    public static final String POM_FILENAME = "pom.xml";


    private String fileName;
    private Document parentDocument;
    private DocumentBuilder builder;
    private Node parentPropertiesNode;
    private HashMap<String, PackageDetails> packageDetailsHashMap;



    

    public XMLModifier(String fileDirectory, HashMap packageDetailsHashMap)
            throws ParserConfigurationException, IOException, SAXException, TransformerException, URISyntaxException {
        this.fileName=fileDirectory+ POM_FILENAME;
        this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.packageDetailsHashMap = packageDetailsHashMap;
        log.info("Parsing XML  document: " + fileName);
        parentDocument = builder.parse(fileName);
        parentDocument.getDocumentElement().normalize();
        log.info("Getting Properties Node of the parent..");
        parentPropertiesNode = FindPropertiesFromDocument(parentDocument);
        log.info("Extracting modules from parent pom...");
        NodeList modules = parentDocument.getElementsByTagName("module");
        System.out.println();
        //If it has modules, first check dependency vulnerabilities for them.
        for (int j = 0; j < modules.getLength(); j++) {
            Node moduleNode = modules.item(j);
            String moduleDirectory = fileDirectory+moduleNode.getFirstChild().getNodeValue();
            CheckModulePOMDependency(moduleDirectory);
        }
        //Version control for parent xml file.
        log.info("Start version vulnerability control for parent document...");
        NodeList dependencies = FindDependenciesFromDocument(parentDocument);
        VersionControlForDocument(dependencies,parentPropertiesNode,false);
        SaveXMLFile(parentDocument);

    }


    private void CheckModulePOMDependency(String moduleName)
            throws IOException, SAXException, TransformerException, URISyntaxException, ParserConfigurationException {
        log.info("Checking"+moduleName+" module's POM document..");
        Document moduleDoc = builder.parse(moduleName+"\\"+ POM_FILENAME);
        NodeList modules = moduleDoc.getElementsByTagName("module");
        //Nested Checking for grant children.
        if(modules.getLength()>0){
            log.info("Found child modules in "+moduleName+" module...");
            //If there is a child module that is not connected to the parent,
            // the program create a new instance to check for its dependencies.
            if(moduleDoc.getElementsByTagName("parent").item(0)==null){
                log.info("The module: "+moduleName+" does not related to the parent POM");
                log.info("Instancing the new XML modifier..");
                XMLModifier newModifier = new XMLModifier(moduleName+"\\",this.packageDetailsHashMap);
            }
            else{
                for (int i = 0; i < modules.getLength(); i++) {
                    CheckModulePOMDependency(moduleName+"\\"+modules.item(i).getFirstChild().getNodeValue());
                }
            }
        }
        log.info("Extracting dependencies from "+moduleName+" document..");
        NodeList dependencies = FindDependenciesFromDocument(moduleDoc);
        Node properties = FindPropertiesFromDocument(moduleDoc);
        VersionControlForDocument(dependencies,properties, true);

        log.info("Finished "+moduleName+" module vulnerability check...");
        SaveXMLFile(moduleDoc);
    }

    private void VersionControlForDocument(NodeList dependencies, Node properties, boolean isModule) {
        log.info("Starting version control for document...");
        for (int i = 0; i < dependencies.getLength(); i++) {
            VersionControlForDependency(dependencies.item(i), properties, isModule);
        }
    }


    private boolean VersionControlForDependency(Node dependencyNode, Node properties, boolean isModule){
        boolean isVulnerable = false;
        Node artifactNode = GetArtifactNodeFromDependencyNode(dependencyNode);
        String artifact = artifactNode.getFirstChild().getNodeValue();
        log.info("Found the artifact ----> "+artifact);
        if(artifact.equalsIgnoreCase("mysql-connector-j")){
            Node groupNode = GetGroupNodeFromDependencyNode(dependencyNode);
            ChangeGroupNameFromNode(groupNode,"com.mysql");
        }

        if(!(packageDetailsHashMap.containsKey(artifact))){
            log.info("Package "+artifact+" is invulnerable...");
            return false;
        }

        log.info("Artifact "+artifact+" is in the vulnerabilities list..");
        PackageDetails packageDetails = packageDetailsHashMap.get(artifact);
        Node versionNode = GetVersionNodeFromDependencyNode(dependencyNode);
        if(versionNode == null){
            log.info("Dependency's version is managed by another dependency...");
            return false;
        }
        String version = versionNode.getFirstChild().getNodeValue();
        String fixedVersion = packageDetails.getFixedVersionList()[0];
        //We need to go properties section.
        if(version.startsWith("$")) {
            boolean isFoundProp = false;
            log.info("artifact's version value is in properties section of the document");
            String versionPropStr = version.substring(2,version.length()-1);
            if(isModule){
                //Find the properties from parent node.
                log.info("Searching properties in parent document..");
                versionNode = FindVersionNodeFromProperties(parentPropertiesNode,versionPropStr);
                if (versionNode!= null) //Already found in the parent's prop node.
                    isFoundProp = true;
            }
            //If still can't find it.
            if(!isFoundProp){
                log.info("Search properties in its document...");
                versionNode = FindVersionNodeFromProperties(properties, versionPropStr);
            }
            //Take the version of it on parent node.
            version = versionNode.getFirstChild().getNodeValue();
        }

        log.info("Dependency version: " + version);
        //Compare
        if(JarDependencies.versionComparator.compare(fixedVersion,version)>0)
            isVulnerable = true;
        if(isVulnerable){
            log.info("The artifact **"+artifact+"** is vulnerable.");
            log.info("Change version "+version+" to ----> "+fixedVersion);
            versionNode.getFirstChild().setNodeValue(fixedVersion);
            return true;
        }
        log.info("Package is invulnerable...");
        return false;
    }




    private Node FindVersionNodeFromProperties(Node propertiesNode, String propString) {
        Node propNode = propertiesNode.getFirstChild();
        while(!propNode.getNodeName().equalsIgnoreCase(propString)){
            propNode = propNode.getNextSibling();
            if(propNode.getNextSibling()==null)
                return null;
        }
        return propNode;
    }

    private Node GetVersionNodeFromDependencyNode(Node dependencyNode) {
        Node versionNode = dependencyNode.getFirstChild();
        boolean isFound = false;
        while (versionNode.getNextSibling()!= null){
            if(versionNode.getNodeName().equalsIgnoreCase("version")){
                isFound = true;
                break;
            }
            versionNode = versionNode.getNextSibling();
        }
        if(isFound)
            return versionNode;
        return null;
    }

    private Node GetArtifactNodeFromDependencyNode(Node dependencyNode) {
        Node artifactNode = dependencyNode.getFirstChild();
        while (!artifactNode.getNodeName().equalsIgnoreCase("artifactid")){
            artifactNode = artifactNode.getNextSibling();
        }
        return artifactNode;
    }

    private Node GetGroupNodeFromDependencyNode(Node node){
        Node groupNode = node.getFirstChild();
        while(!groupNode.getNodeName().equalsIgnoreCase("groupId")){
            groupNode = groupNode.getNextSibling();
        }
        return groupNode;
    }

    private void ChangeGroupNameFromNode(Node groupNode, String newName){
        groupNode.getFirstChild().setNodeValue(newName);
    }

    private Node FindPropertiesFromDocument(Document doc) {
        Node nd = doc.getElementsByTagName("properties").item(0);
        return nd;
    }

    private NodeList FindDependenciesFromDocument(Document doc) {
        NodeList list = doc.getElementsByTagName("dependency");
        return list;
    }

    public void SaveXMLFile(Document doc) throws TransformerException, IOException, URISyntaxException {
        log.info("Saving the XML file...");
        // Get the document's URI
        String uri = doc.getDocumentURI();
        if (uri == null) {
            throw new FileNotFoundException("Document URI is null");
        }
        // Convert the URI to a File object
        URI fileUri = new URI(uri);
        File file = Paths.get(fileUri).toFile();
        if (!file.exists()) {
            throw new FileNotFoundException("The file does not exist: " + uri);
        }
        if (!file.canWrite()) {
            throw new IOException("Cannot write to the file: " + uri);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        // Overwrite the file by creating a new FileOutputStream
        try (FileOutputStream output = new FileOutputStream(file, false)) { // 'false' means overwrite the file
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        }

        log.info("File saved successfully at " + uri);

    }
}
