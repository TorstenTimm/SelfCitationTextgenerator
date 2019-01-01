package de.voynich.text.util;

import de.voynich.text.Constants;
import de.voynich.text.GlyphGroup;

import java.util.*;

/**
 * Class to store one gephi node providing
 */
public class GephiNode {

    public String name;
    public String compareName;
    public int    id;
    public int    count;

    public GephiNode(final String name, final int id, final int count) {
        this.name = name;
        this.compareName = Config.useVoynichSimilarities ? escapeVmsTokens(name) : name;
        this.id =  id;
        this.count = count;
    }

    public static String escapeVmsTokens(final String source) {
        String result = source;
        result = StringUtil.substitute(result, "ch", "1");
        result = StringUtil.substitute(result, "sh", "2");
        result = StringUtil.substitute(result, "ckh", "1k");
        result = StringUtil.substitute(result, "cth", "1t");
        result = StringUtil.substitute(result, "cph", "1p");
        result = StringUtil.substitute(result, "cfh", "1f");
        return result;
    }

    public static Map<String, GephiNode> convertGephiNode(Map<GlyphGroup, Long> generatedGroupMap) {
        Map<String, GephiNode> nodeMap = new HashMap<>(generatedGroupMap.size());
        Iterator<Map.Entry<GlyphGroup, Long>> it = generatedGroupMap.entrySet().iterator();
        int id = 1;
        while (it.hasNext()) {
            Map.Entry<GlyphGroup, Long> entry = it.next();
            String name = entry.getKey().glyphGroup;
            Long count = entry.getValue();
            nodeMap.put(name, new GephiNode(name, id, count.intValue()));
            id++;
        }

        return nodeMap;
    }

    public static String calcType(final Map<String, GephiNode> groupMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("id;label;count;type").append(Constants.ENTER);
        for (String key : groupMap.keySet()) {
            GephiNode group1 = groupMap.get(key);
            String type = "n";
            if (key.contains("i")) {
                type = "i";
            } else if (key.endsWith("d") || key.endsWith("y")) {
                type = "y";
            } else if (key.contains("ol") || key.contains("or") || key.contains("ar") || key.contains("al") || key.endsWith("am") || key.endsWith("os")) {
                type = "o";
            }
            sb.append("" + group1.id + ";" + group1.name + ";" + group1.count + ";" + type).append(Constants.ENTER);
        }

        return sb.toString();
    }

    public static String calcSimilarities(final Map<String, GephiNode> xMap) {
        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> compareMap = new HashMap<>();
        sb.append("Source;Target;Type;id;label;timeset;weight").append(Constants.ENTER);
        int i = 1;
        for (String key : xMap.keySet()) {
            GephiNode group1 = xMap.get(key);
            if (group1.id == 7540) {
                System.out.println("group1.name: " + group1.name + " id=" + group1.id);
            }
            for (String compareGroup : xMap.keySet()) {
                GephiNode group2 = xMap.get(compareGroup);
                int distanceLevenshtein = distance(group1.compareName, group2.compareName, Config.useVoynichSimilarities);
                if (distanceLevenshtein == 1 && key.length() > 1 && compareGroup.length() > 1) {
                    Set<String> compareSet = compareMap.get(group1.name);
                    if (compareSet == null || !compareSet.contains(group2.name)) {
                        sb.append("" + group1.id + ";" + group2.id + ";Undirected;" + i + ";;;1.0").append(Constants.ENTER);
                        i++;
                        compareSet = compareMap.get(group2.name);
                        if (compareSet == null) {
                            compareSet = new HashSet<>();
                        }
                        compareSet.add(group1.name);
                        compareMap.put(group2.name, compareSet);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static int distance(final String s, final String t, final boolean voynich) {
        return Levenshtein.getDistance(s.trim().toLowerCase(), t.trim().toLowerCase(), true, voynich);
    }
}
