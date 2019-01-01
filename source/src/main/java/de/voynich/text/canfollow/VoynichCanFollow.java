package de.voynich.text.canfollow;

import de.voynich.text.GlyphGroup;
import de.voynich.text.canfollow.vms.VoynichGroups;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * use the list with all words in the VMS to decide which tokens can follow after each other.
 * (the generated text will mainly contain glyph groups that exists in the VMS)
 *
 * set method.canFollow=voynich in conf.properties to activate this class
 */
public class VoynichCanFollow implements I_CanFollow {

    static Set<GlyphGroup> voynichGroups = VoynichGroups.getVoynichDictionary();

    static Set<GlyphGroup> voynichGroupsStartingWithGallows;

    static {
        Set<GlyphGroup> hashSet = new HashSet<>();
        Iterator<GlyphGroup> it = voynichGroups.iterator();
        while (it.hasNext()) {
            GlyphGroup glyphGroup = it.next();
            if (glyphGroup.startsWithGallow()) {
                hashSet.add(glyphGroup);
            }
        }

        voynichGroupsStartingWithGallows = hashSet;
    }

    /**
     * Constructor
     */
    public VoynichCanFollow() {}

    /**
     * @param glyphToAdd
     * @param group2
     * @return true if `glyphToAdd` can be used in front of `group2`
     */
    @Override
    public boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return isValid(resultingGlyphGroup);
    }

    /**
     * @param group1
     * @param glyphToAdd
     * @return true if `glyphToAdd` can follow to `group1`
     */
    @Override
    public boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup) {
        return isValid(resultingGlyphGroup);
    }

    /**
     * @param glyphToAdd
     * @param group2
     * @return true if `glyphToAdd` can follow to `group2` as initial glyph
     */
    @Override
    public boolean canFollowToInitalGlyph(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return isValid(resultingGlyphGroup);
    }

    /**
     * @param glyphGroup
     * @return true if all tokens can follow to each other
     */
    @Override
    public boolean isValid(GlyphGroup glyphGroup) {
        return voynichGroups.contains(glyphGroup);
    }

    /**
     * @param glyphGroup
     * @return true if the first token is valid
     */
    @Override
    public boolean hasValidStartGlyph(GlyphGroup glyphGroup) {
        return isValid(glyphGroup);
    }

    public GlyphGroup getGallowVariant(GlyphGroup glyphGroup, I_RandomNumberGenerator randomNumberGenerator) {
        Iterator<GlyphGroup> it = voynichGroupsStartingWithGallows.iterator();
        while (it.hasNext()) {
            GlyphGroup gallowGlyphgroup = it.next();
            if (gallowGlyphgroup.glyphGroup.contains(glyphGroup.glyphGroup)) {
                return gallowGlyphgroup;
            }
        }

        int pos = randomNumberGenerator.rand(voynichGroupsStartingWithGallows.size()-1);
        it = voynichGroupsStartingWithGallows.iterator();
        for (int i = 0; i < pos; i++) {
            it.next();
        }

        return it.next();
    }
}
