package de.voynich.text.util;

import de.voynich.text.Substitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * helper class to build different types of maps
 */
public class Converter {
    /**
     * helper function to convert an Object[][][] into a Map <String, List<Substitution>>
     * @param objectArray
     * @return
     */
    public static Map<String, List<Substitution>> constructSubstitutionMap(Object[][][] objectArray) {
        Map<String, List<Substitution>> substitutionMap = new HashMap<>(objectArray.length);
        for (Object[][] replacement : objectArray) {

            Object key = replacement[0][0];
            Object[] value = replacement[1];
            List<Substitution> valueList = new ArrayList<>(value.length);
            for (Object val : value) {
                valueList.add((Substitution) val);
            }
            substitutionMap.put((String) key, valueList);
        }
        return substitutionMap;
    }

    /**
     * helper function to convert an String[][] into a Map<String, Boolean>
     * @param stringArray
     * @return
     */
    public static Map<String, Boolean> constructBooleanMap(String[][] stringArray) {
        Map<String, Boolean> replaceMap = new HashMap<>(stringArray.length);
        for (String[] replacement : stringArray) {

            String key = replacement[0];
            String value = replacement[1];
            replaceMap.put(key, Boolean.parseBoolean(value));
        }
        return replaceMap;
    }

    /**
     * helper function to convert an String[][] into a Map<String, String>
     * @param stringArray
     * @return
     */
    public static Map<String, String> constructMap(String[][] stringArray) {
        Map<String, String> replaceMap = new HashMap<>(stringArray.length);
        for (String[] replacement : stringArray) {

            String key = replacement[0];
            String value = replacement[1];
            replaceMap.put(key, value);
        }
        return replaceMap;
    }

    public static Map<String, List<String>> constructMap(String[][][] stringArray) {
        Map<String, List<String>> replaceMap = new HashMap<>(stringArray.length);
        for (String[][] replacement : stringArray) {

            String key = replacement[0][0];
            String[] value = replacement[1];
            List<String> valueList = new ArrayList<>();
            for (String val : value) {
                valueList.add(val);
            }
            replaceMap.put(key, valueList);
        }
        return replaceMap;
    }
}
