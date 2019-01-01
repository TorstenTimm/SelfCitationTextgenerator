package de.voynich.text.morph;

import de.voynich.text.*;
import de.voynich.text.canfollow.I_CanFollow;
import de.voynich.text.util.Config;

import java.util.*;

/**
 * Default class with methods for modifying glyph groups.
 *
 * set method.morph=slim in conf.properties to activate this class
 */
public class SlimGroupMorpher extends AbstractBaseGroupMorpher {

    private int addCounter = 0;
    private int addFailedCounter = 0;
    private int removeCounter = 0;
    private int removeFailedCounter = 0;
    private int combineCounter = 0;
    private int combineFailedCounter = 0;
    private int splitCounter = 0;
    private int splitFailedCounter = 0;
    private int replaceCounter1 = 0;
    private int replaceFailedCounter1 = 0;
    private int replaceCounter2 = 0;
    private int replaceFailedCounter2 = 0;
    private int replaceCounter3 = 0;
    private int replaceFailedCounter3 = 0;

    /**
     * Constructor
     * @param currierType
     * @param canFollow instance of a I_CanFollow to check which token can follow to which other token
     * @param methodCanFollowErrorRate an error rate for allowing weird glyph groups like 'doiin
     */
    public SlimGroupMorpher(SelfCitationTextGenerator.CURRIER currierType, I_CanFollow canFollow, int methodCanFollowErrorRate) {
        super(currierType, canFollow, methodCanFollowErrorRate);
    }

    /**
     * modify a `sourceGlyphGroup` into another glyph group by replacing, removing or adding tokens
     * @param sourceGlyphGroups
     * @param isParagraphInitial
     * @param isLineInitial
     * @return
     */
    public List<GlyphGroup> morphGroup(List<GlyphGroup> sourceGlyphGroups, GlyphGroup previousGroup, boolean isParagraphInitial, boolean isLineInitial) {
        GlyphGroup sourceGlyphGroup = sourceGlyphGroups.get(0);
        int length = sourceGlyphGroup.length();
        List<GlyphGroup> ret = new ArrayList<>();

        // choose a method for modifying the source group
        int rand = isParagraphInitial && isLineInitial  ? 0 : randomNumberGenerator.rand(100);
        MorphMethod method;
        if (rand <= addRemoveProbability && sourceGlyphGroup.generateType != GlyphGroup.GENERATE_TYPE.COMBINE) {
            method = MorphMethod.add_remove;
        } else if (rand <= (combineSplitProbability + addRemoveProbability)) {
            method = MorphMethod.combine_split;
        } else {
            method = MorphMethod.replace;
        }

        switch (method) {
            case add_remove: {
                // add & remove method
                addCounter++;
                GlyphGroup morphedGroup = (length< 6) ? addRandomGlyph(sourceGlyphGroup, previousGroup, isParagraphInitial, isLineInitial) : sourceGlyphGroup;
                // if it was not possible to add a prefix the morphedGroup is still unchanged
                if (!morphedGroup.equals(sourceGlyphGroup)) {
                    ret.add(morphedGroup);
                } else {
                    // remove prefix
                    addFailedCounter++;
                    removeCounter++;
                    morphedGroup = tryToDeletePrefix(sourceGlyphGroup);
                    if (morphedGroup.equals(sourceGlyphGroup)) {
                        removeFailedCounter++;
                    } else {
                        ret.add(morphedGroup);
                    }
                }
                break;
            }
            case combine_split: {
                // combine & split
                rand = randomNumberGenerator.rand(100);
                boolean combineGroups;
                // don't combine a group twice
                if (sourceGlyphGroup.generateType != GlyphGroup.GENERATE_TYPE.COMBINE) {
                    if (length < 6) {
                        // if the source group has less then 6 token prefer combineGlyphgroups() with 95 %
                        combineGroups = length <= 2 || rand < 96;
                    } else {
                        // if the source group has more then 8 token always use splitGlyphgroup() and with at least 6 token prefer splitGlyphgroup()
                        combineGroups = rand < 4 && length <= 8;
                    }
                } else {
                    combineGroups = false;
                }
                if (combineGroups && sourceGlyphGroups.size() > 1) {
                    // combine
                    combineCounter++;
                    GlyphGroup morphedGroup = combineGlyphgroups(sourceGlyphGroups);
                    if (morphedGroup.equals(sourceGlyphGroup)) {
                        combineFailedCounter++;
                    } else {
                        ret.add(morphedGroup);
                    }
                } else {
                    // split
                    splitCounter++;
                    List<GlyphGroup> splittedGroups = splitGlyphgroup(sourceGlyphGroup);
                    if (splittedGroups.isEmpty()) {
                        splitFailedCounter++;
                    } else {
                        ret.addAll(splittedGroups);
                    }
                }
                break;
            }
            case replace:
            default: {
                // replace an token with a similar one
                rand = randomNumberGenerator.rand(100);
                if (rand <= 30) { // case 0...30:
                    // replace 1 x
                    replaceCounter1++;
                    GlyphGroup temp = replaceRandomToken(sourceGlyphGroup, isParagraphInitial);
                    if (sourceGlyphGroup.equals(temp)) {
                        temp = replaceRandomToken(sourceGlyphGroup, isParagraphInitial);
                    }
                    if (sourceGlyphGroup.equals(temp)) {
                        if (Constants.DEBUG) {
                            System.out.println("# failed replace " + sourceGlyphGroup.tokensAsString() + " temp=" + temp.glyphGroup);
                        }
                        replaceFailedCounter1++;
                    } else {
                        ret.add(temp);
                    }
                } else if (rand <= 40) { // case 31...40:
                    replaceCounter3++;
                    // replace 3 x
                    GlyphGroup temp = replaceRandomToken(sourceGlyphGroup, isParagraphInitial);
                    GlyphGroup temp2 = replaceRandomToken(temp, isParagraphInitial);
                    if (sourceGlyphGroup.equals(temp2)) {
                        temp2 = temp;
                    }
                    GlyphGroup temp3 = replaceRandomToken(temp2, isParagraphInitial);
                    if (sourceGlyphGroup.equals(temp3)) {
                        temp3 = temp2;
                    }
                    if (sourceGlyphGroup.equals(temp3)) {
                        if (Constants.DEBUG) {
                            System.out.println("# failed replace " + sourceGlyphGroup.tokensAsString() + " temp3=" + temp3.glyphGroup);
                        }
                        replaceFailedCounter3++;
                    } else {
                        ret.add(temp3);
                    }
                } else { // default: // case 41...100:
                    replaceCounter2++;
                    // replace 2 x
                    GlyphGroup temp = replaceRandomToken(sourceGlyphGroup, isParagraphInitial);
                    GlyphGroup temp2 = replaceRandomToken(temp, isParagraphInitial);
                    if (sourceGlyphGroup.equals(temp2)) {
                        temp2 = temp;
                    }
                    if (sourceGlyphGroup.equals(temp2)) {
                        if (Constants.DEBUG) {
                            System.out.println("# failed replace " + sourceGlyphGroup.tokensAsString() + " temp2=" + temp2.glyphGroup + " temp=" + temp.glyphGroup);
                        }
                        replaceFailedCounter2++;
                    } else {
                        ret.add(temp2);
                    }
                }
                break;
            }
        }

        // check if at least one modified was generated
        if (ret.isEmpty()) {
            return ret;
        }

        // handle gallows - add gallow for paragraph initial lines
        handleGallows(isParagraphInitial, isLineInitial, ret);


        /* TODO delete - DEBUG OUTPUT
        if (ret.get(0).glyphGroup.contains("dk")) {
            debugOutput("morphGroup()", sourceGlyphGroups, ret, method);
            throw new IllegalStateException(debugOutput.toString());
        }

        if (method == MorphMethod.combine_split && (gg.generateType == GlyphGroup.GENERATE_TYPE.COMBINE || gg.generateType == GlyphGroup.GENERATE_TYPE.SPLIT)) {
            if (existsInVms) {
                debugOutput("morphGroup() #exists# ", sourceGlyphGroups, ret, method);
            } else {
                debugOutput("morphGroup() #unknown# ", sourceGlyphGroups, ret, method);
            }
        }
        */

        // use the newly generated glyph group to generate a second glyph group
        if (Config.methodReuseLastProbability > 0) {
            // chol -> chol or
            reuseLastMorphedGroup(isParagraphInitial, ret);
        }

        if (Constants.DEBUG) {
            debugOutput("morphGroup()", sourceGlyphGroups, ret, method);
        }

        return ret;
    }

    /**
     * choose between adding a prefix or a gallow
     * @param glyphGroup
     * @param previousGroup
     * @param isParagraphInitial
     * @param isLineInitial
     * @return
     */
    GlyphGroup addRandomGlyph(GlyphGroup glyphGroup, GlyphGroup previousGroup, boolean isParagraphInitial, boolean isLineInitial)  {
        if (isParagraphInitial) {
            // paragraph initial line
            int rand = randomNumberGenerator.rand(100);
            if (rand < 80) {
                return addGallow(glyphGroup, isParagraphInitial, isLineInitial);
            } else {
                return tryToAddPrefix(glyphGroup, previousGroup, isLineInitial);
            }
        } else {
            // normal line
            int rand = randomNumberGenerator.rand(100);
            if (rand < 8) {
                return addGallow(glyphGroup, isParagraphInitial, isLineInitial);
            } else {
                return tryToAddPrefix(glyphGroup, previousGroup, isLineInitial);
            }
        }
    }

    /**
     * add a prefix
     * @param glyphGroup
     * @param previousGroup
     * @param isLineInitial
     * @return
     */
    private GlyphGroup tryToAddPrefix(GlyphGroup glyphGroup, GlyphGroup previousGroup, boolean isLineInitial) {
        StringBuilder log = new StringBuilder();
        List prefixesAlreadyTried = new ArrayList();
        // try 7 times to add a prefix
        for (int j = 0; j < 7; j++) {
            // choose a prefix to try
            String prefix = choosePrefix(j, previousGroup, prefixesAlreadyTried);
            prefixesAlreadyTried.add(prefix);
            log.append("prefix " + prefix +" + ");
            if (prefix.equals(Glyph.qGlyph)) {
                // handle q as prefix
                String startToken = glyphGroup.getToken(0);
                if (startToken.equals(Glyph.oGlyph) || startToken.equals(Glyph.yGlyph)) {
                    List<String> newTokens = glyphGroup.copyTokens();
                    newTokens.set(0, "qo");
                    GlyphGroup g = new GlyphGroup(newTokens, determinGenerateType(GlyphGroup.GENERATE_TYPE.ADD, glyphGroup));
                    if (Constants.DEBUG && g.glyphGroup.equals(glyphGroup.glyphGroup)) {
                        throw new IllegalStateException("# tryToAddPrefix prefix=" + prefix + " glyphGroup=" + glyphGroup.glyphGroup + " -> " + g.glyphGroup + " log=" + log.toString());
                    }
                    return g;
                }
            } else if (prefix.equals(Glyph.xGlyph)) {
                // handle x as prefix
                List<String> allowedStartGlyphs = Glyph.prefixGlyphs.get(prefix);
                if (allowedStartGlyphs != null) {
                    for (String glyph : allowedStartGlyphs) {
                        if (glyphGroup.hasPrefix(glyph)) {
                            List<String> newTokens = glyphGroup.copyTokens();
                            newTokens.add(0, prefix);
                            GlyphGroup g = new GlyphGroup(newTokens, determinGenerateType(GlyphGroup.GENERATE_TYPE.ADD, glyphGroup));
                            if (Constants.DEBUG && g.glyphGroup.equals(glyphGroup.glyphGroup)) {
                                throw new IllegalStateException("# tryToAddPrefix prefix=" + prefix + " glyphGroup=" + glyphGroup.glyphGroup + " -> " + g.glyphGroup + " log=" + log.toString());
                            }
                            return g;
                        }
                    }
                }
            } else {
                // handle all other prefixes
                List<String> allowedStartGlyphs = Glyph.prefixGlyphs.get(prefix);
                if (allowedStartGlyphs != null) {
                    for (String glyph : allowedStartGlyphs) {
                        if (glyphGroup.hasPrefix(glyph)) {
                            int length = glyphGroup.getTokenCount();
                            List<String> newTokens = glyphGroup.copyTokens();
                            // 'd' and 'ch' can change into a gallow if a prefix is added  (o + daiin --> okaiin || y + chol --> ykol)
                            String startToken = glyphGroup.getToken(0);
                            if (length > 2
                                    && (startToken.equals(Glyph.dGlyph) || startToken.equals(Glyph.chGlyph) || startToken.equals(Glyph.shGlyph))) {
                                String gallow = Glyph.randomGallow(false /* firstLine */, randomNumberGenerator);
                                if (canFollowEachOtherBefore(gallow /* glyphToAdd */, newTokens.get(1), null) && canFollowEachOtherAfter(prefix, gallow, null)) {
                                    int rand = randomNumberGenerator.rand(100);
                                    if (rand < 70) {
                                        newTokens.set(0, gallow);
                                    }
                                }
                            }
                            newTokens.add(0, prefix);
                            GlyphGroup g = new GlyphGroup(newTokens, determinGenerateType(GlyphGroup.GENERATE_TYPE.ADD, glyphGroup));
                            //System.out.println("# tryToAddPrefix prefix=" + prefix + " glyphGroup=" + glyphGroup.glyphGroup + " -> " + g.glyphGroup + " log=" + log.toString());
                            if (Constants.DEBUG && g.glyphGroup.equals(glyphGroup.glyphGroup)) {
                                throw new IllegalStateException("# tryToAddPrefix prefix=" + prefix + " glyphGroup=" + glyphGroup.glyphGroup + " -> " + g.glyphGroup + " log=" + log.toString());
                            }

                            return g;
                        }
                    }
                }
            }
        }

        //System.out.println("# tryToAddPrefix glyphGroup=" + glyphGroup.glyphGroup + " -> " + glyphGroup.glyphGroup + " log=" + log.toString());
        return glyphGroup;
    }

    /**
     * return a group with an additional gallow or the source `glyphGroup`
     * @param glyphGroup
     * @param isParagraphInitial
     * @param isLineInitial
     * @return a group with an additional gallow or the source `glyphGroup`
     */
    protected GlyphGroup addGallow(GlyphGroup glyphGroup, boolean isParagraphInitial, boolean isLineInitial) {
        int length = glyphGroup.getTokenCount();
        if (isParagraphInitial && isLineInitial) {
            String gallow = Glyph.randomGallow(isParagraphInitial /* firstLine */, randomNumberGenerator);

            GlyphGroup returnGroup = tryToPlaceGallow(gallow, glyphGroup, 0);
            if (!returnGroup.getToken(0).equals(gallow)) {
                List<String> newTokens = returnGroup.copyTokens();
                if (canFollowEachOtherBefore(Glyph.oGlyph, returnGroup.glyphGroup, null)) {
                    newTokens.add(0, Glyph.oGlyph);
                } else if (canFollowEachOtherBefore(Glyph.aGlyph, returnGroup.glyphGroup, null)) {
                    newTokens.add(0, Glyph.aGlyph);
                }
                newTokens.add(0, gallow);
                returnGroup = new GlyphGroup(newTokens, returnGroup.generateType);
            }
            return returnGroup;
        } else if (length > 1) {
            boolean doit = true;
            // multiple gallows are rare
            if (glyphGroup.containsGallow()) {
                int rand = randomNumberGenerator.rand(100);
                if (rand < 90) {
                    doit = false;
                }
            }
            if (doit) {
                for (int j = 0; j < 5; j++) {
                    int pos = isParagraphInitial ? randomNumberGenerator.rand(length) : length == 2 ? 1 : (randomNumberGenerator.rand((length-2)) + 1);
                    String gallow = Glyph.randomGallow(isParagraphInitial /* firstLine */, randomNumberGenerator);
                    GlyphGroup returnGroup = tryToPlaceGallow(gallow, glyphGroup, pos);
                    if (!returnGroup.equals(glyphGroup)) {
                        return returnGroup;
                    }
                }
            }
        }

        return glyphGroup;
    }

    /**
     * try to add a `gallow` at `pos` and returns the result
     * @param gallow
     * @param glyphGroup
     * @param pos
     * @return
     */
    private GlyphGroup tryToPlaceGallow(String gallow, GlyphGroup glyphGroup, int pos) {
        // check if the glyphGroup did already contain a gallow
        String token = glyphGroup.getToken(pos);
        if (Glyph.isGallow(token)) {
            List<String> newTokens = glyphGroup.copyTokens();
            newTokens.set(pos, gallow);
            return new GlyphGroup(newTokens, glyphGroup.generateType);
        }

        // check if it is possible to add a gallow
        String last = pos == 0 ? "" : glyphGroup.getToken(pos-1);
        String next = pos == glyphGroup.getTokenCount() ? "" : glyphGroup.getToken(pos);
        boolean lastOk = canFollowEachOtherAfter(last, gallow /* glyphToAdd */, null);
        boolean nextOk = canFollowEachOtherBefore(gallow /* glyphToAdd */, next, null);

        if (lastOk && nextOk) {
            List<String> newTokens = glyphGroup.copyTokens();
            newTokens.add(pos, gallow);
            GlyphGroup g = new GlyphGroup(newTokens, glyphGroup.generateType);
            if (Constants.DEBUG) {
                System.out.println("tryToPlaceGallow lastOk && nextOk glyphGroup=" + glyphGroup.glyphGroup + " -> g=" + g.glyphGroup + " gallow=" + gallow + " pos=" + pos + " last=" + last + " next=" + next);
            }
            return g;
        } else {
            //System.out.println("tryToPlaceGallow glyphGroup=" + glyphGroup.glyphGroup + " gallow=" + gallow + " pos=" + pos + " lastOk=" + lastOk + " nextOk=" + nextOk);
            List<String> newTokens = glyphGroup.copyTokens();
            if (lastOk) {
                String alternativeSuffix = glyphGroup.getTokenCount() > pos+1 ? glyphGroup.getToken(pos+1) : "-";
                if (glyphGroup.getTokenCount() > pos+1) {
                    newTokens.set(pos, gallow);
                    GlyphGroup g = new GlyphGroup(newTokens, glyphGroup.generateType);
                    if (canFollowEachOtherBefore(gallow /* glyphToAdd */, alternativeSuffix, g)) {
                        //System.out.println("tryToPlaceGallow lastOk glyphGroup=" + glyphGroup.glyphGroup + " -> g=" + g.glyphGroup);
                        return g;
                    } else {
                        return glyphGroup;
                    }
                } else {
                    return glyphGroup;
                }

            } else if (nextOk) {
                String nextToLastGlyph = pos - 2 > 0 ? glyphGroup.getToken(pos-2) : "-";
                if (pos - 2 > 0) {
                    newTokens.set(pos - 1, gallow);
                    GlyphGroup g = new GlyphGroup(newTokens, glyphGroup.generateType);
                    if (canFollowEachOtherAfter(nextToLastGlyph, gallow /* glyphToAdd */, g)) {
                        //System.out.println("tryToPlaceGallow nextOk glyphGroup=" + glyphGroup.glyphGroup + " -> g=" + g.glyphGroup);
                        return g;
                    } else {
                        return glyphGroup;
                    }
                } else {
                    return glyphGroup;
                }
            } else {
                return glyphGroup;
            }
        }
    }

    /**
     * combine two `sourceGroups` for generating a new GlyphGroup
     * @param sourceGroups
     * @return
     */
    GlyphGroup combineGlyphgroups(List<GlyphGroup> sourceGroups) {
        GlyphGroup glyphGroup1 = sourceGroups.get(0);
        GlyphGroup glyphGroup2 = sourceGroups.get(1);

        List<String> newTokens1 = chooseSubgroupsForCombine(glyphGroup1, true);
        if (newTokens1.size() > 0 && calcLength(newTokens1) > 1) {
            String lastToken1 = newTokens1.get(newTokens1.size() - 1);
            List<String> newTokens2 = chooseSubgroupsForCombine(glyphGroup2, false);

            // check if it is necessary to remove the first token to prevent 'ol' in front of 'l'
            List<String> tokensToRemove = Glyph.allCombinableLigature.get(lastToken1);
            if (tokensToRemove != null) {
                for (String token : tokensToRemove) {
                    if (newTokens2.size() > 0 && token.equals(newTokens2.get(0))) {
                        newTokens2.remove(0);
                    }
                }
            }

            // check length
            if (newTokens2.size() > 0 && calcLength(newTokens2) > 1) {
                for (int i = 0; i < newTokens2.size(); i++) {
                    newTokens1.add(newTokens2.get(i));
                }
                if (calcLength(newTokens1) < 9) {
                    GlyphGroup glyphGroup = new GlyphGroup(newTokens1, GlyphGroup.GENERATE_TYPE.COMBINE);

                    // check if `newTokens1` is allowed in front of `newTokens2`
                    if ((Glyph.isCombinableLigature(lastToken1) || canFollowEachOtherBefore(lastToken1 /* glyphToAdd */, newTokens2.get(0), glyphGroup))
                            && canFollowEachOtherAfter(newTokens2.get(newTokens2.size()-1), "", glyphGroup)) {
                        // System.out.println("combineGlyphgroups glyphGroup1=" + glyphGroup1.glyphGroup + " glyphGroup2=" + glyphGroup2.glyphGroup + " -> g=" + glyphGroup.glyphGroup + " tokensToRemove=" + tokensToRemove + " newTokens1=" + gx1.glyphGroup + " 2=" + gx2.glyphGroup);
                        return glyphGroup;
                    }
                }
            }
        }

        return super.combineGlyphgroups(sourceGroups);
    }

    /**
     * helper method - calculates character length for a list of tokens
     * @param tokenList
     * @return
     */
    int calcLength(List<String> tokenList) {
        int length = 0;

        for (int i = 0; i < tokenList.size(); i++) {
            String token = tokenList.get(i);
            length += token.length();
        }

        return length;
    }

    /**
     * helper method - choose a part of a glyphGroup
     * @param glyphGroup
     * @param isFirstGroup
     * @return
     */
    List<String> chooseSubgroupsForCombine(GlyphGroup glyphGroup, boolean isFirstGroup) {
        List<String> tokenList = new ArrayList<>();
        if ((isFirstGroup && glyphGroup.getTokenCount() <= 2) || (!isFirstGroup && glyphGroup.getTokenCount() <= 3)) {
            tokenList = glyphGroup.copyTokens();
        } else {
            int pos = calcSplitPosition(glyphGroup);
            if (pos >= 0) {
                List<GlyphGroup> splittedList = splitAtPosition(glyphGroup, pos);
                if (splittedList.size() > 0) {
                    if (isFirstGroup || splittedList.size() == 1) {
                        tokenList = splittedList.get(0).copyTokens();
                    } else {
                        tokenList = splittedList.get(1).copyTokens();
                    }
                }
            }
        }

        return tokenList;
    }

    /**
     * helper method - ensure that the type for a combined group remains from type COMBINE while modfied
     * @param suggestedType
     * @param glyphGroup
     * @return
     */
    private GlyphGroup.GENERATE_TYPE determinGenerateType(GlyphGroup.GENERATE_TYPE suggestedType, GlyphGroup glyphGroup) {
        GlyphGroup.GENERATE_TYPE generateType = suggestedType;
        if (glyphGroup.generateType == GlyphGroup.GENERATE_TYPE.COMBINE) {
            generateType = glyphGroup.generateType;
        }
        return generateType;
    }

    /**
     * helper method - choose a prefix token - available prefix tokens are {'l', 'o', 'y', 'ch', 'sh', 'q', 'd', 'x'}
     * @param j
     * @param previousGroup
     * @param prefixesAlreadyTried
     * @return
     */
    private String choosePrefix(int j, GlyphGroup previousGroup, List<String> prefixesAlreadyTried) {
        int rand = randomNumberGenerator.rand(100);

        if (j == 0 && previousGroup != null && previousGroup.endsWithDyToken() && rand < 90) {
            return Glyph.qGlyph;
        } else {
            if (rand < 5 && !prefixesAlreadyTried.contains(Glyph.lGlyph)) {
                return Glyph.lGlyph;
            } else if (rand < 34 && !prefixesAlreadyTried.contains(Glyph.oGlyph)) {
                return Glyph.oGlyph;
            } else if (rand < 40 && !prefixesAlreadyTried.contains(Glyph.yGlyph)) {
                return Glyph.yGlyph;
            } else if (rand < 61 && !prefixesAlreadyTried.contains(Glyph.chGlyph)) {
                return Glyph.chGlyph;
            } else if (rand < 72 && !prefixesAlreadyTried.contains(Glyph.shGlyph)) {
                return Glyph.shGlyph;
            } else if (rand < 88 && !prefixesAlreadyTried.contains(Glyph.qGlyph)) {
                return Glyph.qGlyph;
            } else if (rand < 99) {
                return Glyph.dGlyph;
            } else if (j == 0) {
                return Glyph.xGlyph;
            }
        }

        return Glyph.oGlyph;
    }

    /**
     * remove an lineInitalGlyph [o,y,d,s]
     * @param glyphGroup
     * @return
     */
    protected GlyphGroup removeLineInitalGlyph(GlyphGroup glyphGroup) {
        int length = glyphGroup.getTokenCount();
        if (glyphGroup.startsWithLineInitialGlyph() && length > 2) {
            List<String> newTokens = glyphGroup.copyTokens();
            newTokens.remove(0);

            String first = newTokens.get(0);
            String second = newTokens.get(1);
            if (Glyph.isGallow(first)) {
                if (second.startsWith(Glyph.aGlyph)) {
                    newTokens.set(0,Glyph.dGlyph);
                }
                if (second.startsWith(Glyph.eGlyph)) {
                    newTokens.set(0, Glyph.chGlyph);
                }
            }

            return new GlyphGroup(newTokens, glyphGroup.generateType);
        }

        return glyphGroup;
    }

    /**
     * replace one token with a similar one
     * @param glyphGroup - the glyphGroup to modify
     * @param isParagraphInitial
     * @return the modified glyphGroup
     */
    GlyphGroup replaceRandomToken(GlyphGroup glyphGroup, boolean isParagraphInitial) {

        StringBuilder logBuilder = new StringBuilder();
        // try several times to replace a token
        List<Integer> posToTryList = getPossiblePositionen(glyphGroup);
        for (int j = 0; j < posToTryList.size(); j++) {

            // chose token
            int posToTry = posToTryList.size() > 1 ? randomNumberGenerator.rand(posToTryList.size()) : 0;
            int pos = posToTryList.isEmpty() ? glyphGroup.getTokenCount()-1 : posToTryList.remove(posToTry).intValue();
            String subGroup = glyphGroup.getToken(pos);
            List<Substitution> possibleSubstitutions = Glyph.similarGlyph(subGroup, Config.useWordFinalSubstitutions && j > 0 && pos == glyphGroup.getTokenCount()-1);
            // replace token
            if (possibleSubstitutions != null) {
                Substitution substitute = choose(possibleSubstitutions);
                logBuilder.append(" " + j + " (" + subGroup + "->" + substitute.toString() + ")" );

                List<String> newTokens = glyphGroup.copyTokens();
                String nextToken = newTokens.size() > pos+1 ? newTokens.get(pos+1) : "";

                boolean removeNext = false;
                // check if the substitute contain gallow glyphs
                if (Glyph.containsGallow(substitute.tokens)) {
                    if (Glyph.isGallow(nextToken)) {
                        // allow cht --> ckh
                        removeNext = true;
                    }
                }
                boolean replacable = isReplacable(substitute, glyphGroup, pos, removeNext);
                //System.out.println("replacable=" + replacable + " substitute=" + substitute + " pos=" + pos);
                if (replacable) {
                    newTokens.remove(pos);
                    if (removeNext) {
                        newTokens.remove(pos);
                    }
                    for (int i = 0; i < substitute.count(); i++) {
                        newTokens.add(pos+i, substitute.get(i));
                    }
                    GlyphGroup.GENERATE_TYPE generateType = GlyphGroup.GENERATE_TYPE.REPLACE;
                    if (glyphGroup.generateType == GlyphGroup.GENERATE_TYPE.COMBINE) {
                        generateType = glyphGroup.generateType;
                    }

                    GlyphGroup x = new GlyphGroup(newTokens, generateType);
                    /*
                    if (Constants.DEBUG && x.glyphGroup.startsWith("dd") && !(canFollow instanceof DummyCanFollow)) {
                        throw new IllegalStateException("source=" + glyphGroup.glyphGroup + " -> x=" + x.glyphGroup + " " + logBuilder.toString());
                    }
                    */
                    if (!glyphGroup.equals(x)) {
                        //System.out.println("replaceRandomToken " + glyphGroup.tokensAsString() + " " + logBuilder.toString());
                        return x;
                    }
                } else {
                    logBuilder.append("replacable=" + replacable);
                }
            } else {
                logBuilder.append("" + j + " (" + subGroup + "-> null)");
            }
        }

        return glyphGroup;
    }

    /**
     * helper method - creates token list to choose tokens from
     * @param glyphGroup
     * @return
     */
    private List<Integer> getPossiblePositionen(GlyphGroup glyphGroup) {
        List<Integer> returnList = new ArrayList<>();
        int maxTries = glyphGroup.getTokenCount();
        for (int i = 0; i < maxTries; i++) {
            returnList.add(i);
            returnList.add(i);
        }
        return returnList;
    }

    /**
     * helper method - randomly choose a similar token
     * @param substitutions
     * @return
     */
    private Substitution choose(List<Substitution> substitutions) {
        int rand = randomNumberGenerator.rand(100);
        for (Substitution substitute : substitutions) {
            if (substitute.probability > rand) {
                return substitute;
            }
        }

        // should not happen
        throw new IllegalStateException("maxProbability < 100");
    }

    /**
     * returns a string containing some statistic values
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (Constants.DEBUG) {
            sb.append("add " + addFailedCounter + " failed / " + addCounter + " # ");
            sb.append("remove " + removeFailedCounter + " failed / " + removeCounter + " # ");
            sb.append("combine " + combineFailedCounter + " failed / " + combineCounter + " # ");
            sb.append("split " + splitFailedCounter + " failed / " + splitCounter + " # ");
            sb.append("1x replace " + replaceFailedCounter1 + " failed / " + replaceCounter1 + " # ");
            sb.append("2x replace " + replaceFailedCounter2 + " failed / " + replaceCounter2 + " # ");
            sb.append("3x replace " + replaceFailedCounter3 + " failed / " + replaceCounter3 + " # ");
        }
        sb.append(super.toString());

        return sb.toString();
    }
}
