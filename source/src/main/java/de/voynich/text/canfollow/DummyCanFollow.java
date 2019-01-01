package de.voynich.text.canfollow;

import de.voynich.text.GlyphGroup;

/**
 * Dummy class to deactivate the can follow checks. This check only prevents that the samce two tokens can follow each other.
 *
 * set method.canFollow=none in conf.properties to activate this class
 */
public class DummyCanFollow implements I_CanFollow {

    /**
     * @param glyphToAdd
     * @param group2
     * @return true if `glyphToAdd` can be used in front of `group2`
     */
    @Override
    public boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return !group2.isEmpty() && !glyphToAdd.equals(group2);
    }

    /**
     * @param group1
     * @param glyphToAdd
     * @return true if `glyphToAdd` can follow to `group1`
     */
    @Override
    public boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup) {
        return !group1.isEmpty() && !glyphToAdd.equals(group1);
    }

    /**
     * @param glyphToAdd
     * @param group2
     * @return true if `glyphToAdd` can follow to `group2` as initial glyph
     */
    @Override
    public boolean canFollowToInitalGlyph(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return !group2.isEmpty() && !glyphToAdd.equals(group2);
    }

    @Override
    public boolean isValid(GlyphGroup glyphGroup) {
        return glyphGroup.getTokenCount() > 0;
    }

    /**
     * @param glyphGroup
     * @return true if the first token is valid
     */
    @Override
    public boolean hasValidStartGlyph(GlyphGroup glyphGroup) {
        return glyphGroup.getTokenCount() > 0;
    }
}
