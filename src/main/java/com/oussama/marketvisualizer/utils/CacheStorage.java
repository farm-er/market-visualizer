package com.oussama.marketvisualizer.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CacheStorage {

    public String cacheDirectory;

    /*
     * 
     * STORES JSON STRING IN LOCAL FILESYSTEM
     */

    // public  void storeData( String name, String JsonString) throws IOException {
    //     String fileName = name + ".json";
    //     Path filePath = Paths.get(cacheDirectory, fileName);
        
    //     try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
    //         writer.write(jsonString);
    //     }
        
    //     System.out.println("JSON cached to: " + filePath.toString());
    // }

    /*
     * 
     * RETRIEVES JSON STRING FROM LOCAL FILESYSTEM
     * IF IT RETURNS IT MEANS THERE'S NO FILE IN CACHE
     */

    // public String retrieveJson(String name) throws IOException {
    //     String fileName = name + ".json";
    //     Path filePath = Paths.get(cacheDirectory, fileName);
        
    //     if (!Files.exists(filePath)) {
    //         return null;
    //     }
        
    //     return Files.readString(filePath, StandardCharsets.UTF_8);
    // }


    // public CacheStorage( String dir) {
    //     cacheDirectory = dir;
    // }

}
