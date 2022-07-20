package com.mpnp.baechelin.common;

public class DataClarification {
    public static String clarifyString(String input){
        // Trim
        input = input.trim();
        // replace all blanks
        return input.replaceAll("\\s+", " ");
    }
}
