package de.voynich.text.sourcechooser;

import de.voynich.text.GlyphGroup;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ChooserHelper {

    static List<GlyphGroup> chooseRandomly(Map<GlyphGroup, Long> groupDictionary, I_RandomNumberGenerator randomNumberGenerator) {
        List<GlyphGroup> ret = new ArrayList<>();

        //
        ArrayList<GlyphGroup> arrayList = new ArrayList<>(groupDictionary.size());
        arrayList.addAll(groupDictionary.keySet());

        // pick first source group
        int rand = randomNumberGenerator.rand(groupDictionary.size());
        ret.add(arrayList.get(rand));

        // pick second source group
        rand = randomNumberGenerator.rand(groupDictionary.size());
        ret.add(arrayList.get(rand));

        return ret;
    }

    /**
     * calc pos in sourceLineList
     * @param actualLineList
     * @param sourceLineList
     * @return
     */
    public static int calcLinePosition(List<GlyphGroup> actualLineList, List<GlyphGroup> sourceLineList) {

        // calc writing position in characters
        int writingPos = 0;
        for (int i = 0; i < actualLineList.size(); i++) {
            GlyphGroup group = actualLineList.get(i);
            writingPos = writingPos + group.length() + 1;
        }

        // calc position in source line
        int sourcePos = 0;
        for (int i = 0; i < sourceLineList.size(); i++) {
            GlyphGroup group = sourceLineList.get(i);
            sourcePos += group.length() + 1;
            if (writingPos <= sourcePos) {
                return i;
            }
        }

        return Math.min(sourceLineList.size()-1, actualLineList.size());
    }

    /**
     * remove initial gallow glyph if there is any
     * @param ret
     */
    static void removeInitialGallow(List<GlyphGroup> ret) {
        for (int i = 0; i < ret.size(); i++) {
            if (ret.get(i).startsWithGallow()) {
                List<String> newTokens = ret.get(i).copyTokens();
                newTokens.remove(0);
                if (newTokens.size() > 0) {
                    ret.set(i, new GlyphGroup(newTokens, ret.get(i).generateType));
                }
            }
        }
    }
}
