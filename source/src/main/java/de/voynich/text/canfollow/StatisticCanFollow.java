package de.voynich.text.canfollow;

import de.voynich.text.GlyphGroup;
import de.voynich.text.canfollow.vms.FollowMatrix;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.Map;

/**
 * class to decide which tokens can follow after each other.
 *
 * set method.canFollow=statistic in conf.properties to activate this class
 */
public class StatisticCanFollow implements I_CanFollow {

    /** use the list of all VMS words to build a matrix with statistics for which token can follow eacht other */
    Map<String, Map<String, Long>> percentMatrix = FollowMatrix.getPercentMatrix();
    I_RandomNumberGenerator randomNumberGenerator;

    public StatisticCanFollow() {
    }

    public void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    /**
     * @param glyphToAdd
     * @param group2
     * @param resultingGlyphGroup
     * @return true if `glyphToAdd` can be used in front of `group2`
     */
    @Override
    public boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        Map<String, Long> percentMap = percentMatrix.get(glyphToAdd.equals("") ? "." : glyphToAdd);
        if (percentMap == null) {
            return false;
        }
        Long percent = percentMap.get(group2.equals("") ? "." : group2);
        if (percent == null) {
            return false;
        }
        if (percent >= 50) {
            return true;
        }
        int rand = randomNumberGenerator.rand(100);
        if (percent >= 40) {
            return rand <= 50;
        }
        if (percent >= 30) {
            return rand <= 40;
        }
        if (percent >= 20) {
            return rand <= 30;
        }
        if (percent >= 10) {
            return rand <= 20;
        }
        if (percent >= 1) {
            return rand <= 10;
        }
        return false;
    }

    /**
     * @param group1
     * @param glyphToAdd
     * @param resultingGlyphGroup
     * @return true if `glyphToAdd` can follow to `group1`
     */
    @Override
    public boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup) {
        Map<String, Long> percentMap = percentMatrix.get(group1.equals("") ? "." : group1);
        if (percentMap == null) {
            return false;
        }
        Long percent = percentMap.get(glyphToAdd.equals("") ? "." : glyphToAdd);
        if (percent == null) {
            return false;
        }
        return true;
    }

    /**
     * @param glyphToAdd
     * @param group2
     * @param resultingGlyphGroup
     * @return true if `glyphToAdd` can follow to `group1` as initial glyph
     */
    @Override
    public boolean canFollowToInitalGlyph(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return canFollowEachOtherBefore(glyphToAdd, group2, resultingGlyphGroup);
    }

    /**
     * @param glyphGroup
     * @return true if all tokens can follow to each other
     */
    @Override
    public boolean isValid(GlyphGroup glyphGroup) {
        for (int i = -1; i < glyphGroup.getTokenCount(); i++) {
            String currentToken = i == -1 ? "." : glyphGroup.getToken(i);
            String nextToken = i + 1 >= glyphGroup.getTokenCount() ? "." : glyphGroup.getToken(i+1);
            Map<String, Long> percentMap = percentMatrix.get(currentToken);
            if (percentMap == null) {
                return false;
            }
            Long percent = percentMap.get(nextToken);
            if (percent == null || percent < 1) {
                return false;
            }

        }
        return true;
    }

    /**
     * @param glyphGroup
     * @return true if the first token is valid
     */
    @Override
    public boolean hasValidStartGlyph(GlyphGroup glyphGroup) {
        return canFollowEachOtherBefore("", glyphGroup.getToken(0), glyphGroup);
    }
}
