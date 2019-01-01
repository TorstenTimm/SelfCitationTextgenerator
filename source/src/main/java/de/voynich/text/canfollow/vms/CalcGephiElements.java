package de.voynich.text.canfollow.vms;

import de.voynich.text.util.Config;
import de.voynich.text.util.FileTools;
import de.voynich.text.util.GephiNode;

import java.util.*;

public class CalcGephiElements {

    private static Map<String, GephiNode> getVoynichGroupMap() {
        Map<String, GephiNode> nodeMap = new HashMap<>(8500);
        int id = 0;
        for (String[] group : VoynichGroups.GROUPS) {
            String name = group[0];
            String countString = group[1];

            int count = Long.valueOf(countString).intValue();
            nodeMap.put(name, new GephiNode(name, id, count));
            id++;
        }

        for (String[] group : VoynichGroupsTwo.GROUPS) {
            String name = group[0];
            String countString = group[1];

            int count = Long.valueOf(countString).intValue();
            nodeMap.put(name, new GephiNode(name, id, count));
            id++;
        }

        for (String[] group : VoynichGroupsThree.GROUPS) {
            String name = group[0];
            String countString = group[1];

            int count = Long.valueOf(countString).intValue();
            nodeMap.put(name, new GephiNode(name, id, count));
            id++;
        }

        return nodeMap;
    }

    public static void main(String[] args) {
        Config config = new Config();
        Map<String, GephiNode> groupMap = getVoynichGroupMap();
        FileTools.saveFile(config.save_path + "vmsAllNodes.csv", GephiNode.calcType(groupMap));
        FileTools.saveFile(config.save_path + "vmsAllEdges.csv", GephiNode.calcSimilarities(groupMap));

    }
}
