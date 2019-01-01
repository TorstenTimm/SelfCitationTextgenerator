package de.voynich.text.canfollow;

import de.voynich.text.GlyphGroup;

/**
 * interface for
 * @see CurveLineCanFollow
 * @see StatisticCanFollow
 * @see VoynichCanFollow
 * @see DummyCanFollow
 */
public interface I_CanFollow {

    /**
     *
     * @param glyphToAdd
     * @param group2
     * @return true if `glyphToAdd` can be used in front of `group2`
     */
    boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup);

    /**
     *
     * @param group1
     * @param glyphToAdd
     * @return true if `glyphToAdd` can follow to `group1`
     */
    boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup);

    /**
     *
     * @return true if `glyphToAdd` can follow to `group1` as initial glyph
     */
    boolean canFollowToInitalGlyph(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup);

    /**
     *
     * @return true if all tokens can follow to each other
     */
    boolean isValid(GlyphGroup glyphGroup);

    /**
     *
     * @return true if the first token is valid
     */
    boolean hasValidStartGlyph(GlyphGroup glyphGroup);
}
