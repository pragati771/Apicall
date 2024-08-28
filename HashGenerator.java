package com.app.main;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <jar-file-name> <PRN Number> <JSON file path>");
            return;
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            // Parse JSON file
            Object obj = new JSONParser().parse(new FileReader(jsonFilePath));
            JSONObject jsonObject = (JSONObject) obj;

            // Get the "destination" value
            String destinationValue = findDestinationValue(jsonObject);

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in JSON.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Concatenate PRN number, destination value, and random string
            String inputString = prnNumber + destinationValue + randomString;

            // Generate MD5 hash
            String hashValue = generateMD5Hash(inputString);

            // Print the output in the specified format
            System.out.println(hashValue + ";" + randomString);

        } catch (IOException | ParseException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    // Traverse JSON to find the first instance of the key "destination"
    private static String findDestinationValue(JSONObject jsonObject) {
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                for (Object item : (JSONArray) value) {
                    if (item instanceof JSONObject) {
                        String result = findDestinationValue((JSONObject) item);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null; // Key "destination" not found
    }

    // Generate a random alphanumeric string of a given length
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    // Generate MD5 hash from a string
    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available.", e);
        }
    }
}