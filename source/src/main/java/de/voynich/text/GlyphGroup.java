package de.voynich.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Class <code>StatisticHelper</code> to store a VMS word
 */
public class GlyphGroup {

    public enum GENERATE_TYPE {
        INITIAL, ADD, DELETE, REPLACE, COMBINE, SPLIT, SHORTEN
    }

    // implementation Printable
    public String glyphGroup;

    // stores the tokens of the `glyphGroup`
    private List<String> tokens = new ArrayList<>();

    public GENERATE_TYPE generateType;

    public int getTokenCount() {
        return tokens.size();
    }

    public int length() {
        return glyphGroup.length();
    }

    public GlyphGroup(String glyphGroup, GENERATE_TYPE generateType) {
        this.glyphGroup = glyphGroup;
        this.generateType = generateType;

        parse();
    }

    public GlyphGroup(List<String> tokens, GENERATE_TYPE generateType) {
        this.tokens = tokens;
        this.generateType = generateType;

        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append(token);
        }
        glyphGroup = sb.toString();

        parse();
    }

    /// initialize the `tokens`-List
    private void parse() {
        int length = glyphGroup.length();
        String temp = glyphGroup;
        int pos = 0;
        tokens.clear();

        while (pos < length) {
            String ligature = Glyph.determineStartLigature(temp);
            if (ligature != null) {
                tokens.add(ligature);
                pos += ligature.length();
            } else {
                tokens.add(temp.substring(0, 1));
                pos += 1;
            }
            temp = glyphGroup.substring(pos);
        }
    }

    public String getToken(int pos) {
        return tokens.get(pos);
    }

    /// returns the tokens within the `glyphGroup` as human readable string
    public String tokensAsString() {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append("{").append(token).append("}");
        }

        return sb.toString();
    }

    /// returns a copy of the `tokens`
    public List<String> copyTokens() {
        List<String> returnList = new ArrayList<>(tokens.size());
        returnList.addAll(tokens);
        return  returnList;
    }

    /**
     * @return true is a glpyh starts with a gallow glyph (k,t,p,f)
     */
    public boolean startsWithGallow() {
        return Glyph.startsWithGallow(glyphGroup);
    }

    /**
     * @return true if the `glyphGroup` starts with a typical line inital glyph (o,y,d,s)
     */
    public boolean startsWithLineInitialGlyph() {
        return Glyph.startsWithLineInitialGlyph(glyphGroup);
    }

    /**
     * @param prefix
     * @return true if the `glyphGroup` starts with the `prefix` given
     */
    public boolean hasPrefix(String prefix) {
        String firstToken = tokens.get(0);
        return firstToken.equals(prefix);
    }

    /**
     *
     * @return true if it contains a combinableLigature like 'ol', 'al', 'ar' or 'or'
     */
    public boolean containsCombinableLigature() {
        for (String ligature : Glyph.combinableLigature.keySet()) {
            for (String token : tokens) {
                if (token.equals(ligature)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return a combinableLigature ['ol', 'al', 'ar', 'or'] or null
     */
    public String getFirstCombinableLigature() {
        for (String ligature : Glyph.combinableLigature.keySet()) {
            for (String token : tokens) {
                if (token.equals(ligature)) {
                    return token;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param search
     * @return true if `search` is part of `glyphGroup`
     */
    public boolean contains(String search) {
        for (String token : tokens) {
            if (token.equals(search)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @return true if the glpyhGroup contains a gallow glyph
     */
    public boolean containsGallow() {
        return Glyph.containsGallow(glyphGroup);
    }

    /**
     *
     * @return true if the glpyhGroup contains a 'i'
     */
    public boolean isTypeI() {
        return glyphGroup.contains("i");
    }

    /**
     *
     * @return true if the glpyhGroup contains a combinableLigature 'ol', 'or', 'al' or 'ar'
     */
    public boolean isTypeOl() {
        for (String search : Glyph.olGlyphList) {
            for (String token : tokens) {
                if (search.equals(token)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *
     * @return true if the glpyhGroup contains 'dy' or ends with 'y' or 'd'
     */
    public boolean isTypeDy() {
        for (String token : tokens) {
            if (Glyph.dyGlyph.equals(token)) {
                return true;
            }
        }

        String lastChar = glyphGroup.substring(glyphGroup.length()-1);
        return lastChar.equals(Glyph.yGlyph) || lastChar.equals(Glyph.dGlyph);
    }

    public boolean endsWithDyToken() {
        String suffix = tokens.get(tokens.size()-1);
        return suffix.equals(Glyph.dyGlyph);
    }

    public int hashCode() {
        return glyphGroup.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof GlyphGroup) {
            return glyphGroup.equals(((GlyphGroup) obj).glyphGroup);
        }
        return false;
    }

    public String toString() {
        return glyphGroup;
    }
}
