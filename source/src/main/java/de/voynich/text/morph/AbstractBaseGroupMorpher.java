package de.voynich.text.morph;

import de.voynich.text.*;
import de.voynich.text.canfollow.I_CanFollow;
import de.voynich.text.canfollow.VoynichCanFollow;
import de.voynich.text.util.Config;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract base class for SlimGroupMorpher
 * @see SlimGroupMorpher
 */
abstract class AbstractBaseGroupMorpher implements I_GroupMorpher {

    int combineSplitProbability = 10;
    int addRemoveProbability    = 30;
    private int replaceProbability      = 60;

    private int morphReusedProbability  = 50;
    private int minReplaceProbbility = 20;

    private int methodCanFollowErrorRate;

    private I_CanFollow canFollow;
    I_RandomNumberGenerator randomNumberGenerator;
    SelfCitationTextGenerator.CURRIER currierType;

    /**
     * Constructor
     * @param currierType
     * @param canFollow instance of a I_CanFollow to check which token can follow to which other token
     * @param methodCanFollowErrorRate an error rate for allowing weird glyph groups like 'doiin
     */
    public AbstractBaseGroupMorpher(SelfCitationTextGenerator.CURRIER currierType, I_CanFollow canFollow, int methodCanFollowErrorRate) {
        this.currierType = currierType;
        this.canFollow = canFollow;
        this.methodCanFollowErrorRate = methodCanFollowErrorRate;
    }

    @Override
    public void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setProbabilities(int addRemoveProbability, int combineSplitProbability) {
        int maxAddRemoveProbability = 100 - minReplaceProbbility;
        this.addRemoveProbability = Math.min(addRemoveProbability, maxAddRemoveProbability);
        if ((this.addRemoveProbability + combineSplitProbability) > maxAddRemoveProbability){
            this.combineSplitProbability = Math.min(combineSplitProbability, maxAddRemoveProbability - this.addRemoveProbability);
        } else {
            this.combineSplitProbability = combineSplitProbability;
        }
        this.replaceProbability = 100 - this.addRemoveProbability - this.combineSplitProbability;
    }

    public String getReplacePropability() {
        return "" + replaceProbability;
    }

    public String getAddRemovePropability() {
        return "" + addRemoveProbability;
    }

    public String getCombineSplitPropability() {
        return "" + combineSplitProbability;
    }

    /**
     *
     * @param glyphToAdd
     * @param group2
     * @param resultingGlyphGroup
     * @return
     */
    boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        boolean canFollowBefore = canFollow.canFollowEachOtherBefore(glyphToAdd, group2, resultingGlyphGroup != null ? resultingGlyphGroup : new GlyphGroup(glyphToAdd + group2, GlyphGroup.GENERATE_TYPE.COMBINE));
        if (!canFollowBefore && methodCanFollowErrorRate > 0) {
            int rand = randomNumberGenerator.rand(100);
            if (rand <= methodCanFollowErrorRate) {
                return true;
            }
        }
        return canFollowBefore;
    }

    /**
     *
     * @param group1
     * @param glyphToAdd
     * @param resultingGlyphGroup
     * @return
     */
    boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup) {
        boolean canFollowAfter = this.canFollow.canFollowEachOtherAfter(group1, glyphToAdd, resultingGlyphGroup != null ? resultingGlyphGroup : new GlyphGroup(group1 + glyphToAdd, GlyphGroup.GENERATE_TYPE.COMBINE));
        if (!canFollowAfter && methodCanFollowErrorRate > 0) {
            int rand = randomNumberGenerator.rand(100);
            if (rand <= methodCanFollowErrorRate) {
                return true;
            }
        }
        return canFollowAfter;
    }

    /**
     *
     * @param glyphToAdd
     * @param group2
     * @param resultingGlyphGroup
     * @return
     */
    boolean canFollowToInitalGlyph(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {
        return canFollow.canFollowToInitalGlyph(glyphToAdd, group2, resultingGlyphGroup != null ? resultingGlyphGroup : new GlyphGroup(glyphToAdd + group2, GlyphGroup.GENERATE_TYPE.COMBINE));
    }

    /**
     * determine if the `token` at `pos` is replacable by `newToken`
     * @param substitution
     * @param glyphGroup
     * @param pos
     * @param removeNext
     * @return
     */
    boolean isReplacable(Substitution substitution, GlyphGroup glyphGroup, int pos, boolean removeNext) {

        String last = pos == 0 ? "" : glyphGroup.getToken(pos-1);
        int nextPos = removeNext ? pos+2 : pos+1;
        String next = nextPos >= glyphGroup.getTokenCount() ? "" : glyphGroup.getToken(nextPos);
        boolean lastOk = canFollowEachOtherAfter(last, substitution.first() /* glyphToAdd */, new GlyphGroup(last + substitution.first() , GlyphGroup.GENERATE_TYPE.COMBINE));
        boolean nextOk = canFollowEachOtherBefore(substitution.last() /* glyphToAdd */, next, new GlyphGroup(substitution.last()  + next , GlyphGroup.GENERATE_TYPE.COMBINE));
        //System.out.println("isReplacable() last=" + last + " next=" + next + " lastOk=" + lastOk + " nextOk=" + nextOk + " substitution.last()=" + substitution.last() + " substitution.count()=" + substitution.count() + " contains(substitution)=" + glyphGroup.contains(substitution.first()));
        // multiple instances off the same token within a group are rare
        if (lastOk && nextOk && glyphGroup.contains(substitution.first()) && !Glyph.isCombinableLigature(substitution.first())) {
            int compareValue = 80;
            if (substitution.first().equals(last) || substitution.last().equals(next)) {
                compareValue = (100 - methodCanFollowErrorRate);
            }
            int rand = randomNumberGenerator.rand(100);
            if (rand < compareValue) {
                return false;
            }
        }

        return lastOk && nextOk;
    }

    /**
     *
     * @param sourceGlyphGroups
     * @param ret
     * @param method
     */
    static void debugOutput(String methodName, List<GlyphGroup> sourceGlyphGroups, List<GlyphGroup> ret, MorphMethod method) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName + " method=" + method + " source:");
        for (int i = 0; i < sourceGlyphGroups.size(); i++) {
            sb.append("i=").append(i).append(") ").append(sourceGlyphGroups.get(i).glyphGroup).append(" ").append(sourceGlyphGroups.get(i).generateType).append(" ");
        }
        sb.append("result:");
        for (int i = 0; i < ret.size(); i++) {
            sb.append("i=").append(i).append(") ").append(ret.get(i).glyphGroup).append(" ").append(ret.get(i).generateType).append(" ");
        }
        System.out.println(sb.toString());
    }

    /**
     *
     * @param isParagraphInitial
     * @param isLineInitial
     * @param ret
     */
    void handleGallows(boolean isParagraphInitial, boolean isLineInitial, List<GlyphGroup> ret) {
        int rand;
        if  (canFollow instanceof VoynichCanFollow) {
            if (isLineInitial && isParagraphInitial) {
                VoynichCanFollow voynichCanFollow = (VoynichCanFollow) canFollow;
                ret.set(0, voynichCanFollow.getGallowVariant(ret.get(0), randomNumberGenerator));
            }
        } else {
            if (isLineInitial) {
                rand = randomNumberGenerator.rand(100);
                if (isParagraphInitial && rand < Constants.PARAGRAPH_STARTS_WITH_GALLOW_PROPABILITY) {
                    GlyphGroup gg = addGallow(ret.get(0), true /* isParagraphInitial */, true /* isLineInitial */);
                    //System.out.println("paragraphInitial " + ret.get(0) + " -> " + gg);
                    ret.set(0, gg);
                } else {
                    ret.set(0, addLineInitalGlyph(ret.get(0)));
                }
            } else {
                if (ret.get(0).startsWithLineInitialGlyph()) {
                    rand = randomNumberGenerator.rand(Glyph.prefixGlyphs.size());

                    if (rand < 3 && ret.get(0).getTokenCount() > 3) {
                        ret.set(0, removeLineInitalGlyph(ret.get(0)));
                    }
                }
            }

            if (ret.get(0).containsGallow()) {
                if (!isParagraphInitial) {
                    rand = randomNumberGenerator.rand(100);
                    if (rand < Constants.PARAGRAPH_STARTS_WITH_GALLOW_PROPABILITY) {
                        ret.set(0, replaceFirstLineGallows(ret.get(0)));
                    }
                } else {
                    ret.set(0, replaceWithFirstLineGallows(ret.get(0)));
                }
            }
        }
    }

    /**
     * prefer an lineInitalGlyph [o,y,d,s] to start a line
     * @param glyphGroup
     * @return
     */
    GlyphGroup addLineInitalGlyph(GlyphGroup glyphGroup) {
        if (!glyphGroup.startsWithLineInitialGlyph()) {
            int rand = randomNumberGenerator.rand(100);
            if (rand < 30) {
                int length = glyphGroup.getTokenCount();
                String glyph = Glyph.randomLineInitalGlyph(randomNumberGenerator);
                List<String> newTokens = glyphGroup.copyTokens();
                // o + daiin --> okaiin / o + chol --> okol
                if (length > 2 && (glyph.equals("o") || glyph.equals("y")) && (glyphGroup.getToken(0).equals("d") || glyphGroup.getToken(0).equals("ch"))) {
                    rand = randomNumberGenerator.rand(100);
                    if (rand < 90) {
                        String gallow = Glyph.randomGallow(false /* firstLine */, randomNumberGenerator);
                        if (canFollowEachOtherBefore(gallow /* glyphToAdd */, newTokens.get(1), null)) {
                            newTokens.set(0, gallow);
                        }
                    }
                }
                newTokens.add(0, glyph); // #insert 0
                GlyphGroup g= new GlyphGroup(newTokens, glyphGroup.generateType);
                if (canFollowToInitalGlyph(glyph /* glyphToAdd */, newTokens.get(0), g)) {
                    // System.out.println("addLineInitalGlyph glyphGroup=" + glyphGroup.glyphGroup + " -> g=" + g.glyphGroup );
                    return g;
                }
            }
        }

        return glyphGroup;
    }

    protected abstract GlyphGroup removeLineInitalGlyph(GlyphGroup glyphGroup);

    protected abstract GlyphGroup addGallow(GlyphGroup glyphGroup, boolean b, boolean b1);

    /**
     * generate a new glyphgroup by combining two `sourceGroups`
     * @param sourceGroups
     * @return
     */
    GlyphGroup combineGlyphgroups(List<GlyphGroup> sourceGroups) {
        GlyphGroup glyphGroup1 = sourceGroups.get(0);
        GlyphGroup glyphGroup2 = sourceGroups.get(1);

        if (glyphGroup1.getTokenCount() == 2 || (glyphGroup1.getTokenCount()) == 1 && Glyph.isCombinableLigature(glyphGroup1.glyphGroup)) {
            int rand = randomNumberGenerator.rand(100);
            if (rand < 60) {
                GlyphGroup newGlyphGroup = selfCombine(glyphGroup1);
                if (!newGlyphGroup.equals(glyphGroup1)) {
                    return newGlyphGroup;
                }
            }
        }

        List<String> newTokens2 = chooseSubgroups(glyphGroup2);
        if (newTokens2.size() > 0) {
            List<String> newTokens1 = glyphGroup1.copyTokens();
            String lastToken1 = newTokens1.get(newTokens1.size()-1);

            // check if it is necessary to remove the first token to prevent 'ol' in front of 'l'
            if (Glyph.isCombinableLigature(lastToken1)) {
                List<String> tokensToRemove = Glyph.allCombinableLigature.get(lastToken1);
                if (tokensToRemove != null) {
                    for (String token : tokensToRemove) {
                        if (newTokens2.size() > 0 && token.equals(newTokens2.get(0))) {
                            newTokens2.remove(0);
                        }
                    }
                }
            }

            // check length
            int newLength = newTokens1.size() + newTokens2.size();
            if (newTokens2.size() > 0 && newLength < 9) {
                for (int i = 0; i < newTokens2.size(); i++) {
                    newTokens1.add(newTokens2.get(i));
                }
                GlyphGroup gg = new GlyphGroup(newTokens1, GlyphGroup.GENERATE_TYPE.COMBINE);
                // check if `newTokens2` is allowed in front of `newTokens1`
                if (canFollowEachOtherBefore(lastToken1 /* glyphToAdd */, newTokens2.get(0), gg) && canFollowEachOtherAfter(newTokens2.get(newTokens2.size()-1), "", gg)) {
                    //System.out.println("combineGlyphgroups glyphGroup1=" + glyphGroup1.glyphGroup + " glyphGroup2=" + glyphGroup2.glyphGroup + " -> g=" + gg.glyphGroup + " tokensToRemove=" + tokensToRemove);
                    return gg;
                }
            }
        }

        if (glyphGroup1.getTokenCount() <= 2) {
            return selfCombine(glyphGroup1);
        }

        return glyphGroup1;
    }

    /**
     * use this version with longer subgroups to generate rare glyphGroups
     * @param glyphGroup
     * @return
     */
    List<String> chooseSubgroups(GlyphGroup glyphGroup) {

        int rand = randomNumberGenerator.rand(100);
        if (rand <= 39) {
            List<String> ret = new ArrayList<String>();
            if (glyphGroup.getTokenCount() > 1) {
                rand = randomNumberGenerator.rand(glyphGroup.getTokenCount() - 1) + 1;
                for (int i = 0; i < rand; i++) { // var i = 0; i < rand; i += 1 {
                    ret.add(glyphGroup.getToken(i));
                }
            }
            return ret;
        } else if (rand <= 59) {
            List<String> newTokens = glyphGroup.copyTokens();
            newTokens.remove(glyphGroup.getTokenCount() - 1); // removeLast()
            return newTokens;
        } else {
            List<String> ret = new ArrayList<>();
            String subgroup = glyphGroup.getFirstCombinableLigature();
            if (subgroup != null) {
                ret.add(subgroup);
                return ret;
            } else {
                List<String> newTokens = glyphGroup.copyTokens();
                newTokens.remove(glyphGroup.getTokenCount() - 1); // removeLast()
                return newTokens;
            }
        }
    }

    /**
     * generate self similar groups like "chochy" or "olol"
     * @param glyphGroup
     * @return
     */
    GlyphGroup selfCombine(GlyphGroup glyphGroup) {
        if (glyphGroup.getTokenCount() > 0) {
            String firstToken = glyphGroup.getToken(0);
            String lastToken = glyphGroup.getToken(glyphGroup.getTokenCount()-1);
            if (canFollowEachOtherBefore(lastToken /* glyphToAdd */, firstToken, null)) {
                List<String> newTokens = glyphGroup.copyTokens();
                List<String> newTokens1 = glyphGroup.copyTokens();

                String lastTokenNew = newTokens.get(newTokens.size()-1);
                String inGroupReplacement = Glyph.replaceIngroupGlyph(lastTokenNew);
                String finalReplacement = Glyph.replaceGroupFinalGlyph(lastTokenNew);

                if (!lastTokenNew.equals(finalReplacement)) {
                    int rand = randomNumberGenerator.rand(100);
                    if (rand < 80) {
                        newTokens.set(newTokens.size()-1, finalReplacement); // newTokens[newTokens.count-1] = finalReplacement
                    }
                }
                if (!lastTokenNew.equals(inGroupReplacement)) {
                    newTokens1.set(newTokens.size()-1, inGroupReplacement);
                }

                if (canFollowEachOtherBefore(Glyph.gallowGlyphs[0] /* glyphToAdd */, firstToken, null)
                        && canFollowEachOtherBefore(lastTokenNew /* glyphToAdd */, Glyph.gallowGlyphs[0], null)) {
                    int rand = randomNumberGenerator.rand(100);
                    if (rand < 30) {
                        String gallow = Glyph.randomGallow(false /* firstLine */, randomNumberGenerator);
                        newTokens.add(1, gallow); // #insert 0
                    }
                }
                for (int i = 0; i < newTokens1.size(); i++) {
                    newTokens.add(i, newTokens1.get(i)); // #insert i // newTokens.insert(newTokens1[i], at: i)
                }
                GlyphGroup g = new GlyphGroup(newTokens, GlyphGroup.GENERATE_TYPE.COMBINE);
                //System.out.println("selfCombine glyphGroup=" + glyphGroup.glyphGroup + " -> g=" + g.glyphGroup );
                return g;
            }
        }

        return glyphGroup;
    }

    /**
     * split `glyphGroup` into two groups
     * @param glyphGroup
     * @return
     */
    List<GlyphGroup> splitGlyphgroup(GlyphGroup glyphGroup) {
        List<GlyphGroup> ret = new ArrayList<>();
        int pos = calcSplitPosition(glyphGroup);
        if (pos >= 0) {
            ret = splitAtPosition(glyphGroup, pos);
        }

        if (ret.size() == 0) {
            ret.add(glyphGroup);
        }

        return ret;
    }

    int calcSplitPosition(GlyphGroup glyphGroup) {
        for (int i=1; i < glyphGroup.getTokenCount(); i++) {
            String last = glyphGroup.getToken(i-1);
            String next = glyphGroup.getToken(i);

            if (!canFollowEachOtherBefore(last, next, null) || Glyph.isCombinableLigature(last) || Glyph.isGallow(next)) {
                if (i > 1 || last.length() > 1) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * delete the first token
     * @param glyphGroup
     * @return
     */
    GlyphGroup tryToDeletePrefix(GlyphGroup glyphGroup) {
        int groupLength = glyphGroup.getTokenCount();
        String startToken = glyphGroup.getToken(0);
        List<String> newTokens = glyphGroup.copyTokens();
        newTokens.remove(0);
        if (groupLength > 2 && canFollowEachOtherBefore("" /* glyphToAdd */, startToken, new GlyphGroup(newTokens, glyphGroup.generateType))) {
            // check if the group now starts with a gallow
            String firstToken = newTokens.get(0);
            String secondToken = newTokens.get(1);
            if (Glyph.isGallow(firstToken)) {
                int rand = randomNumberGenerator.rand(100);
                // kain --> dain
                if (secondToken.startsWith(Glyph.aGlyph) && rand < 50) {
                    newTokens.set(0, Glyph.dGlyph);
                }
                // kedy --> chedy, kol --> chol
                if ((secondToken.startsWith(Glyph.eGlyph) || (secondToken.startsWith(Glyph.oGlyph)))&& rand < 50) {
                    newTokens.set(0, "ch");
                }
            }

            return new GlyphGroup(newTokens, GlyphGroup.GENERATE_TYPE.DELETE);
        }

        return glyphGroup;
    }

    /**
     * try to split glyphGroup at pos
     * @param glyphGroup
     * @return
     */
    List<GlyphGroup> splitAtPosition(GlyphGroup glyphGroup, int pos) {
        List<String> newTokens = glyphGroup.copyTokens();
        List<String> newTokens1 = new ArrayList<>();
        List<String> newTokens2 = new ArrayList<>();
        for (int i=0; i < newTokens.size(); i++) {
            if (i < pos) {
                newTokens1.add(newTokens.get(i));
            } else {
                newTokens2.add(newTokens.get(i));
            }
        }
        List<GlyphGroup> ret = new ArrayList<>();
        GlyphGroup group1 = new GlyphGroup(newTokens1, GlyphGroup.GENERATE_TYPE.SPLIT);
        if ((group1.getTokenCount() > 1 || Glyph.isCombinableLigature(group1.glyphGroup)) && canFollowEachOtherAfter(group1.getToken(group1.getTokenCount()-1), "", group1)) {
            ret.add(group1);
        }
        GlyphGroup group2 = new GlyphGroup(newTokens2, GlyphGroup.GENERATE_TYPE.SPLIT);
        if (group2.getTokenCount() > 1) {
            ret.add(group2);
        }

        return ret;
    }

    /**
     * replace gallow glyphs
     * @param glyphGroup
     * @return
     */
    GlyphGroup replaceFirstLineGallows(GlyphGroup glyphGroup) {
        List<String> newTokens = glyphGroup.copyTokens();
        for (int i = 0; i < newTokens.size(); i++) {
            String token = newTokens.get(i);
            if (Glyph.containsFirstLineGallow(token)) {
                String newGallow = Glyph.randomGallow(false, randomNumberGenerator);
                token = Glyph.replaceFirstLineGallow(token, newGallow);
                newTokens.set(i, token);
            }
        }
        return new GlyphGroup(newTokens, glyphGroup.generateType);
    }

    /**
     * replace gallow glyphs
     * @param glyphGroup
     * @return
     */
    GlyphGroup replaceWithFirstLineGallows(GlyphGroup glyphGroup) {
        List<String>  newTokens = glyphGroup.copyTokens();
        for (int i = 0; i < newTokens.size(); i++) {
            String token = newTokens.get(i);
            if (Glyph.containsGallow(token)) {
                String newGallow = Glyph.randomGallow(true /* firstLine */, randomNumberGenerator);
                token = Glyph.replaceGallows(token, newGallow);
                newTokens.set(i, token);
            }
        }
        return new GlyphGroup( newTokens, glyphGroup.generateType);
    }

    void reuseLastMorphedGroup(boolean isParagraphInitial, List<GlyphGroup> ret) {
        if (ret.size() > 1) {
            return;
        }

        int rand = randomNumberGenerator.rand(100);
        if (rand < Config.methodReuseLastProbability) {
            GlyphGroup lastGroup = ret.get(0);
            if (lastGroup.containsCombinableLigature()) {
                String ligature = lastGroup.getFirstCombinableLigature();
                GlyphGroup groupToAdd = new GlyphGroup(ligature, GlyphGroup.GENERATE_TYPE.SPLIT);
                rand = randomNumberGenerator.rand(100);
                if (rand < morphReusedProbability) {
                    groupToAdd = replaceRandomToken(groupToAdd, isParagraphInitial);
                }
                ret.add(groupToAdd);
            } else {
                GlyphGroup morphedGroup = tryToDeletePrefix(lastGroup);
                if (!lastGroup.equals(morphedGroup)) {
                    rand = randomNumberGenerator.rand(100);
                    if (rand < morphReusedProbability) {
                        morphedGroup = replaceRandomToken(morphedGroup, isParagraphInitial);
                    }
                    ret.add(morphedGroup);
                }
            }
        }
    }

    abstract GlyphGroup replaceRandomToken(GlyphGroup glyphGroup, boolean isParagraphInitial);

}
