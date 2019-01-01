package de.voynich.text.canfollow.vms;

import de.voynich.text.Constants;
import de.voynich.text.GlyphGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FollowMatrix {

    private static Map<String, Map<String, Long>> countMatrix = new HashMap<>();
    private static Map<String, Map<String, Long>> percentMatrix = new HashMap<>();

    public static void main(String[] args) {
        StringBuilder sb = build();
        System.out.println(sb.toString());
    }

    public static Map<String, Map<String, Long>> getPercentMatrix() {
        build();
        return percentMatrix;
    }

    public static StringBuilder build() {
        StringBuilder sb = new StringBuilder();
        for (String[] group : VoynichGroups.GROUPS) {
            GlyphGroup glyphGroup = new GlyphGroup(group[0], GlyphGroup.GENERATE_TYPE.INITIAL);
            count(glyphGroup, group[1]);
        }
        for (String[] group : VoynichGroupsTwo.GROUPS) {
            GlyphGroup glyphGroup = new GlyphGroup(group[0], GlyphGroup.GENERATE_TYPE.INITIAL);
            count(glyphGroup, group[1]);
        }
        for (String[] group : VoynichGroupsThree.GROUPS) {
            GlyphGroup glyphGroup = new GlyphGroup(group[0], GlyphGroup.GENERATE_TYPE.INITIAL);
            count(glyphGroup, group[1]);
        }

        Iterator<String> currentIterator = countMatrix.keySet().iterator();
        while (currentIterator.hasNext()) {
            long overallCount = 0;
            String currentToken = currentIterator.next();
            Map<String, Long> countMap = countMatrix.get(currentToken);
            Iterator<Map.Entry<String, Long>> nextIterator = countMap.entrySet().iterator();
            while (nextIterator.hasNext()) {
                Map.Entry<String, Long> entry = nextIterator.next();
                overallCount += entry.getValue();
            }

            nextIterator = countMap.entrySet().iterator();
            while (nextIterator.hasNext()) {
                Map.Entry<String, Long> entry = nextIterator.next();
                String nextToken = entry.getKey();
                Long count = entry.getValue();
                float percent =  (float) 100.0 * (float) count / (float) overallCount;
                if (percent >= (float) 5.0) {
                    sb.append(currentToken + ":" + nextToken + "=" + count + "/" + overallCount + " (" + percent + ")/");
                }

                remember(currentToken, nextToken, percent);
            }

            sb.append(Constants.ENTER);
        }

        return sb;
    }

    private static void remember(String currentToken, String nextToken, Float percent) {
        Map<String, Long> percentMap = percentMatrix.get(currentToken);
        if (percentMap == null) {
            percentMap = new HashMap<>();
            percentMatrix.put(currentToken, percentMap);
        }
        percentMap.put(nextToken, percent.longValue());

    }

    private static void count(GlyphGroup glyphGroup, String countString) {
        long count = Long.parseLong(countString);
        for (int i = -1; i < glyphGroup.getTokenCount(); i++) {
            String currentToken = i == -1 ? "." : glyphGroup.getToken(i);
            String nextToken = i + 1 >= glyphGroup.getTokenCount() ? "." : glyphGroup.getToken(i+1);
            String next = nextToken;

            Map<String, Long> countMap = countMatrix.get(currentToken);
            if (countMap == null) {
                countMap = new HashMap<>();
                countMatrix.put(currentToken, countMap);
            }
            Long value = countMap.get(next);
            if (value == null) {
                value = 0L;
            }
            value += count;
            countMap.put(next, value);
        }
    }
}
