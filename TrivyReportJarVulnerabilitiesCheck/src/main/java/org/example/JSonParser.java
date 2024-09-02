package org.example;
import com.google.gson.*;
import org.example.dto.JSonRoot;

import java.io.BufferedReader;
import java.io.IOException;

public class JSonParser {
   private Gson gson;
   private BufferedReader fileReader;

    public JSonParser(BufferedReader fileReader) {
        this.gson = new Gson();
        this.fileReader = fileReader;
    }


    //Parses the JSON file into JSonRoot class with GSon
    public JSonRoot ParseJSON() throws IOException {
        JSonRoot root = null;
        try{
            root = gson.fromJson(fileReader, JSonRoot.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }
}
