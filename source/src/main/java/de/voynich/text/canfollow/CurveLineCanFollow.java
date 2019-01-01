package de.voynich.text.canfollow;

import de.voynich.text.Constants;
import de.voynich.text.Glyph;
import de.voynich.text.GlyphGroup;
import de.voynich.text.util.Config;
import de.voynich.text.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Default class to decide which tokens can follow after each other.
 *
 * set method.canFollow=curveline in conf.properties to activate this class
 */
public class CurveLineCanFollow implements I_CanFollow {

    enum Type {GALLOW, CURVE_TYPE, LINE_TYPE, EMPTY, NONE, FINAL_TYPE }

    private static final List<String> startGlyphList = Arrays.asList( "qo", "a", "o", "y", "c", "s", "d", "k", "t", "p", "f", "x");
    private static final List<String> finalGlyphList = Arrays.asList( "y", "n", "l", "r", "s", "d", "m", "g", "x" );

    // curve - curve tokens
    private static final List<String> ccTokenList = Arrays.asList( "e", "h", "d", "s", "y", "o", "ch", "sh", "ckh", "cth", "cph", "cfh", "al", "ol", "x", "l");
    // curve - final tokens
    private static final List<String> cFinalTokenList = Arrays.asList( "d", "g", "o", "y", "dy", "s", "om", "am", "og", "ag", "al", "ar", "ol", "or", "x" );
    // curve - line tokens
    private static final List<String> clTokenList = Arrays.asList( "a" );

    // line - curve tokens
    private static final List<String> lcTokenList = Arrays.asList( "ikh", "ith", "iph", "ifh");
    // line - line tokens
    private static final List<String> llTokenList = Arrays.asList("i");
    // line final tokens
    private static final List<String> lFinalTokenList = Arrays.asList( "n", "in", "iin", "iiin", "r", "ir", "iir", "iiir", "m", "im", "iiil", "iil", "il", "iis", "is");

    // gallow tokens
    private static final List<String> gallowTokenList = Arrays.asList("k", "t", "p", "f");
    // tokens that can follow to a gallow
    private static final List<String> afterGallowTokenList = Arrays.asList( "a", "e", "o", "y", "h", "ch", "sh" );
    // tokens a gallow can follow
    private static final List<String> beforeGallowTokenList = Arrays.asList( "", "a", "e", "o", "l", "y", "h" );

    private static final List<String> aoyTokenList = Arrays.asList(  "a", "o", "y");
    private static final List<String> rmngTokenList = Arrays.asList(  "r", "m", "n", "g" );

    private boolean isCombinableLigature(String token) {
        return Glyph.allCombinableLigature.keySet().contains(token);
    }

    /**
     * helper method - used to determine if a token starts with a combinable ligature like 'ol', 'or', 'ar', 'al'
     * @param token
     * @return
     */
    private boolean startsWithCombinableLigature(String token) {
        for (String s : Glyph.allCombinableLigature.keySet()) {
            if (token.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * helper method - used to determine if a token ends with a combinable ligature like 'ol', 'or', 'ar', 'al'
     * @param token
     * @return
     */
    private boolean endsWithCombinableLigature(String token) {
        for (String s : Glyph.allCombinableLigature.keySet()) {
            if (token.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * helper method - determines the type at the end of a token
     * @param token
     * @return
     */
    private Type determineEndTokenType(String token) {
        if (token.equals("")) {
            return Type.EMPTY;
        }

        for (String s : clTokenList) {
            if (token.endsWith(s)) {
                return Type.LINE_TYPE;
            }
        }

        for (String s : llTokenList) {
            if (token.endsWith(s)) {
                return Type.LINE_TYPE;
            }
        }

        for (String s : ccTokenList) {
            if (token.endsWith(s)) {
                return Type.CURVE_TYPE;
            }
        }

        for (String s : lcTokenList) {
            if (token.endsWith(s)) {
                return Type.CURVE_TYPE;
            }
        }

        for (String s : finalGlyphList) {
            if (token.endsWith(s)) {
                return Type.FINAL_TYPE;
            }
        }

        for (String s : gallowTokenList) {
            if (token.endsWith(s)) {
                return Type.GALLOW;
            }
        }

        //System.out.println("determineEndTokenType() unable do determine type for " + token);
        return Type.NONE;
    }

    /**
     * helper method - determines the type at the start of a token
     * @param token
     * @return
     */
    private Type determineStartTokenType(String token) {

        if (token.equals("")) {
            return Type.EMPTY;
        }

        for (String s : llTokenList) {
            if (token.startsWith(s)) {
                return Type.LINE_TYPE;
            }
        }

        for (String s : lcTokenList) {
            if (token.startsWith(s)) {
                return Type.LINE_TYPE;
            }
        }

        for (String s : lFinalTokenList) {
            if (token.startsWith(s)) {
                return Type.LINE_TYPE;
            }
        }

        for (String s : ccTokenList) {
            if (token.startsWith(s)) {
                return Type.CURVE_TYPE;
            }
        }

        for (String s : clTokenList) {
            if (token.startsWith(s)) {
                return Type.CURVE_TYPE;
            }
        }

        for (String s : cFinalTokenList) {
            if (token.startsWith(s)) {
                return Type.CURVE_TYPE;
            }
        }

        for (String s : gallowTokenList) {
            if (token.startsWith(s)) {
                return Type.GALLOW;
            }
        }

        //System.out.println("determineStartType() unable do determine type for " + token);
        return Type.NONE;
    }

    /**
     * @param glyphToAdd
     * @param group2
     * @param resultingGlyphGroup
     * @return true if `glyphToAdd` can be used in front of `group2`
     */
    @Override
    public boolean canFollowEachOtherBefore(String glyphToAdd, String group2, GlyphGroup resultingGlyphGroup) {

        if (isCombinableLigature(glyphToAdd)) {
            for (String s : aoyTokenList) {
                if (group2.startsWith(s)) {
                    return true;
                }
            }
        }
        if (startsWithCombinableLigature(group2)) {
            for (String s : rmngTokenList) {
                if (glyphToAdd.endsWith(s)) {
                    return true;
                }
            }
        }

        if (!StringUtil.isEmpty(glyphToAdd) && group2.startsWith(glyphToAdd)) {
            //System.out.println("glyphToAdd=" + glyphToAdd + " group2=" + group2 + " resultingGlyphGroup=" + resultingGlyphGroup.glyphGroup);
            return false;
        }

        // at the end group2 is empty
        Type group2Type = determineEndTokenType(group2);
        if (Config.useWordFinalSubstitutions && group2Type == Type.EMPTY) {
            for (String s : lFinalTokenList) {
                if (glyphToAdd.equals(s)) {
                    return true;
                }
            }
            for (String s : cFinalTokenList) {
                if (glyphToAdd.equals(s)) {
                    return true;
                }
            }
            return false;
        }

        Type type = determineEndTokenType(glyphToAdd);
        //System.out.println("glyphToAdd=" + glyphToAdd + " group2=" + group2 + " type=" +type + " group2Type=" + group2Type);
        switch (type) {
            case EMPTY: {
                for (String s : startGlyphList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                return  false;
            }
            case LINE_TYPE: {
                for (String s : lcTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                for (String s : lFinalTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                for (String s : llTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                return  false;
            }
            case CURVE_TYPE: {
                for (String s : ccTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                for (String s : clTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                for (String s : cFinalTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                for (String s : gallowTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                return  false;
            }
            case GALLOW: {
                for (String s : afterGallowTokenList) {
                    if (group2.startsWith(s)) {
                        return true;
                    }
                }
                return  false;
            }
            case FINAL_TYPE: {
                return group2.equals("");
            }
            case NONE: {
                return false;
            }
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * @param group1
     * @param glyphToAdd
     * @param resultingGlyphGroup
     * @return true if `glyphToAdd` can follow to `group1`
     */
    @Override
    public boolean canFollowEachOtherAfter(String group1, String glyphToAdd, GlyphGroup resultingGlyphGroup) {

        if (isCombinableLigature(glyphToAdd)) {
            for (String s : rmngTokenList) {
                if (group1.endsWith(s)) {
                    return true;
                }
            }
        }
        if (endsWithCombinableLigature(group1)) {
            for (String s : aoyTokenList) {
                if (glyphToAdd.startsWith(s)) {
                    return true;
                }
            }
        }

        if (!StringUtil.isEmpty(glyphToAdd) && group1.endsWith(glyphToAdd)) {
            return false;
        }

        // at the start group1 is empty
        Type group1Type = determineEndTokenType(group1);
        if (Config.useWordFinalSubstitutions && group1Type == Type.EMPTY) {
            for (String s : startGlyphList) {
                if (glyphToAdd.equals(s)) {
                    return true;
                }
            }
            return false;
        }

        Type type = determineStartTokenType(glyphToAdd);
        //System.out.println("canFollowEachOtherAfter (" + type + ") " + glyphToAdd);
        switch (type) {
            case EMPTY: {
                // group final
                for (String s : finalGlyphList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                return false;
            }
            case LINE_TYPE: {
                for (String s : llTokenList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                for (String s : clTokenList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                return  false;
            }
            case CURVE_TYPE: {
                for (String s : ccTokenList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                for (String s : lcTokenList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                for (String s : gallowTokenList) {
                    if (group1.endsWith(s)) {
                        return true;
                    }
                }
                return false;
            }
            case GALLOW: {
                for (String s : beforeGallowTokenList) {
                    if ((s.equals("") && group1.equals(s)) || (!s.equals("") && group1.endsWith(s))) {
                        return true;
                    }
                }
                return false;
            }
            case NONE: {
                return false;
            }
            default:
                throw new IllegalStateException();
        }
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
     * @return true if all tokens can follow to each other
     */
    public boolean isValid(GlyphGroup glyphGroup) {
        return isValid(glyphGroup, this);
    }

    /**
     * @param glyphGroup
     * @return true if the first token is valid
     */
    @Override
    public boolean hasValidStartGlyph(GlyphGroup glyphGroup) {
        if (glyphGroup.generateType == GlyphGroup.GENERATE_TYPE.INITIAL) {
            return true;
        }
        return glyphGroup.getTokenCount() > 0 && canFollowEachOtherBefore("", glyphGroup.glyphGroup, glyphGroup);
    }

    /**
     *
     * @return true if all tokens can follow to each other
     */
    static boolean isValid(GlyphGroup glyphGroup, I_CanFollow canFollow) {
        if (!canFollow.hasValidStartGlyph(glyphGroup)) {
            return false;
        }
        for (int i = 1; i < glyphGroup.getTokenCount(); i++) {
            String previousToken = glyphGroup.getToken(i-1);
            String nextToken = glyphGroup.getToken(i);

            boolean lastOk = canFollow.canFollowEachOtherAfter(previousToken, nextToken /* glyphToAdd */, glyphGroup);
            boolean nextOk = canFollow.canFollowEachOtherBefore(previousToken /* glyphToAdd */, nextToken, glyphGroup);

            if (!lastOk || !nextOk) {
                if (Constants.DEBUG && glyphGroup.generateType == GlyphGroup.GENERATE_TYPE.INITIAL) {
                    System.out.println("INVALID: lastOk=" + lastOk + " nextOk=" + nextOk + " glyphgroup=" + glyphGroup.glyphGroup + " previousToken=" + previousToken + " nextToken=" + nextToken);
                }
                return false;
            }
        }

        return true;
    }
}
