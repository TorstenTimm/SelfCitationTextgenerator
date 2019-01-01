package de.voynich.text.morph;

import de.voynich.text.SelfCitationTextGenerator;
import de.voynich.text.Constants;
import de.voynich.text.GlyphGroup;
import de.voynich.text.canfollow.CurveLineCanFollow;
import de.voynich.text.canfollow.I_CanFollow;
import de.voynich.text.util.random.I_RandomNumberGenerator;
import de.voynich.text.util.random.PseudoRandomNumberGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * test class with test cases for modifying glyph groups
 * @see SlimGroupMorpher
 */
public class MorphTester {

    public static void main(String[] args) {
        I_RandomNumberGenerator randomNumberGenerator = new PseudoRandomNumberGenerator(Constants.RANDOM_SEED_DEFAULT);
        I_CanFollow canFollow = new CurveLineCanFollow();
        I_GroupMorpher groupMorpher = new SlimGroupMorpher(SelfCitationTextGenerator.CURRIER.B, canFollow, 0);
        groupMorpher.setRandomNumberGenerator(randomNumberGenerator);

        //((SlimGroupMorpher) groupMorpher).isReplacable(new Substitution(new String[] {"ol"}, 50),new GlyphGroup("ar", GlyphGroup.GENERATE_TYPE.INITIAL),0,false);
        test(groupMorpher);
    }

    private static void test(I_GroupMorpher groupMorpher) {
        if (groupMorpher instanceof SlimGroupMorpher) {
            SlimGroupMorpher slimGroupMorpher = (SlimGroupMorpher) groupMorpher;
            testAdd(slimGroupMorpher);
            testRemove(slimGroupMorpher);

            testCombine(slimGroupMorpher);
            testSplit(slimGroupMorpher);

            testReplace(slimGroupMorpher);
            testGallow(slimGroupMorpher);
            System.exit(0);
        }
    }

    private static void testAdd(SlimGroupMorpher groupMorpher) {

        System.out.println("### ADD ####");
        GlyphGroup darGroup = new GlyphGroup("dar", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup cholGroup = new GlyphGroup("chol", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup chedyGroup = new GlyphGroup("chedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup daiinGroup = new GlyphGroup("daiin", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, cholGroup, true, true);

        List<GlyphGroup> ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add paragraphInitial", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, cholGroup, false, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add lineInitial", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, cholGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add inText", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, chedyGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add afterChedy", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, chedyGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add afterChedy", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, chedyGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add afterChedy", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(daiinGroup, chedyGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add afterChedy", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(darGroup, cholGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add lineInitial", Arrays.asList(darGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.addRandomGlyph(darGroup, cholGroup, false, false);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("add lineInitial", Arrays.asList(darGroup), ret, I_GroupMorpher.MorphMethod.add_remove);
    }

    private static void testRemove(SlimGroupMorpher groupMorpher) {
        System.out.println("### REMOVE ####");

        GlyphGroup darGroup = new GlyphGroup("dar", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup qokolGroup = new GlyphGroup("qokol", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup olcheedyGroup = new GlyphGroup("olcheedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup okeedyGroup = new GlyphGroup("okeedy", GlyphGroup.GENERATE_TYPE.INITIAL);

        GlyphGroup glyphGroup = groupMorpher.tryToDeletePrefix(qokolGroup);
        List<GlyphGroup> ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("remove", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.tryToDeletePrefix(olcheedyGroup);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("remove", Arrays.asList(olcheedyGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.tryToDeletePrefix(okeedyGroup);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("remove", Arrays.asList(okeedyGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        glyphGroup = groupMorpher.tryToDeletePrefix(darGroup);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("remove", Arrays.asList(darGroup), ret, I_GroupMorpher.MorphMethod.add_remove);
    }

    private static void testCombine(SlimGroupMorpher groupMorpher) {
        System.out.println("### COMBINE ####");

        GlyphGroup olGroup = new GlyphGroup("ol", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup chedyGroup = new GlyphGroup("chedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        List<GlyphGroup> sourceGroups = Arrays.asList(olGroup, chedyGroup);
        GlyphGroup glyphGroup = groupMorpher.combineGlyphgroups(sourceGroups);
        List<GlyphGroup> ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("combine", sourceGroups, ret, I_GroupMorpher.MorphMethod.combine_split);

        GlyphGroup okolGroup = new GlyphGroup("okol", GlyphGroup.GENERATE_TYPE.INITIAL);
        sourceGroups = Arrays.asList(okolGroup, chedyGroup);
        glyphGroup = groupMorpher.combineGlyphgroups(sourceGroups);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("combine", sourceGroups, ret, I_GroupMorpher.MorphMethod.combine_split);

        GlyphGroup shorGroup = new GlyphGroup("shor", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup chdyGroup = new GlyphGroup("chdy", GlyphGroup.GENERATE_TYPE.INITIAL);
        sourceGroups = Arrays.asList(shorGroup, chdyGroup);
        glyphGroup = groupMorpher.combineGlyphgroups(sourceGroups);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("combine", sourceGroups, ret, I_GroupMorpher.MorphMethod.combine_split);
    }

    private static void testSplit(SlimGroupMorpher groupMorpher) {
        System.out.println("### SPLIT ####");

        GlyphGroup glyphGroup = new GlyphGroup("shelkar", GlyphGroup.GENERATE_TYPE.INITIAL);
        List<GlyphGroup> ret = groupMorpher.splitGlyphgroup(glyphGroup);
        groupMorpher.debugOutput("test", Arrays.asList(glyphGroup), ret, I_GroupMorpher.MorphMethod.combine_split);

        glyphGroup = new GlyphGroup("olcheedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = groupMorpher.splitGlyphgroup(glyphGroup);
        groupMorpher.debugOutput("split", Arrays.asList(glyphGroup), ret, I_GroupMorpher.MorphMethod.combine_split);

        glyphGroup = new GlyphGroup("shorchdy", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = groupMorpher.splitGlyphgroup(glyphGroup);
        groupMorpher.debugOutput("split", Arrays.asList(glyphGroup), ret, I_GroupMorpher.MorphMethod.combine_split);

        glyphGroup = new GlyphGroup("aram", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = groupMorpher.splitGlyphgroup(glyphGroup);
        groupMorpher.debugOutput("split", Arrays.asList(glyphGroup), ret, I_GroupMorpher.MorphMethod.combine_split);

        glyphGroup = new GlyphGroup("ykear", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = groupMorpher.splitGlyphgroup(glyphGroup);
        groupMorpher.debugOutput("split", Arrays.asList(glyphGroup), ret, I_GroupMorpher.MorphMethod.combine_split);
    }

    private static void testReplace(SlimGroupMorpher groupMorpher) {
        System.out.println("### REPLACE ####");

        GlyphGroup qokolGroup = new GlyphGroup("qokol", GlyphGroup.GENERATE_TYPE.INITIAL);

        GlyphGroup glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, false);
        List<GlyphGroup> ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);

        glyphGroup = groupMorpher.replaceRandomToken(qokolGroup, true);
        ret = Arrays.asList(glyphGroup);
        groupMorpher.debugOutput("replace", Arrays.asList(qokolGroup), ret, I_GroupMorpher.MorphMethod.replace);
    }

    private static void testGallow(SlimGroupMorpher groupMorpher) {
        System.out.println("### Gallow ####");
        GlyphGroup chedyGroup = new GlyphGroup("chedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        GlyphGroup daiinGroup = new GlyphGroup("daiin", GlyphGroup.GENERATE_TYPE.INITIAL);
        List<GlyphGroup> ret = Arrays.asList(chedyGroup);
        groupMorpher.handleGallows(true, true, ret);
        groupMorpher.debugOutput("handleGallows", Arrays.asList(chedyGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        ret = Arrays.asList(daiinGroup);
        groupMorpher.handleGallows(true, true, ret);
        groupMorpher.debugOutput("handleGallows", Arrays.asList(daiinGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        GlyphGroup arGroup = new GlyphGroup("ar", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = Arrays.asList(arGroup);
        groupMorpher.handleGallows(true, true, ret);
        groupMorpher.debugOutput("handleGallows", Arrays.asList(arGroup), ret, I_GroupMorpher.MorphMethod.add_remove);

        GlyphGroup eedyGroup = new GlyphGroup("eedy", GlyphGroup.GENERATE_TYPE.INITIAL);
        ret = Arrays.asList(eedyGroup);
        groupMorpher.handleGallows(true, true, ret);
        groupMorpher.debugOutput("handleGallows", Arrays.asList(eedyGroup), ret, I_GroupMorpher.MorphMethod.add_remove);
    }
}
