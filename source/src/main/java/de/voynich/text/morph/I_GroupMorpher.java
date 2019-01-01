package de.voynich.text.morph;

import de.voynich.text.GlyphGroup;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.List;

/**
 * interface for implications of GroupMorpher
 * @see SlimGroupMorpher
 */
public interface I_GroupMorpher {

    enum MorphMethod {
        replace, combine_split, add_remove
    }

    void setProbabilities(int addRemoveProbability, int combineSplitProbability);

    String getReplacePropability();

    String getAddRemovePropability();

    String getCombineSplitPropability();

    List<GlyphGroup> morphGroup(List<GlyphGroup> sourceGlyphGroups, GlyphGroup previousGroup, boolean isParagraphInitial, boolean isLineInitial);

    void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator);
}
