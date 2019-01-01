package de.voynich.text;

import de.voynich.text.util.Converter;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.*;

/**
 * Class <code>Glyph</code> contains substitutionMap
 */
public class Glyph {

    public static final String iGlyph = "i";
    public static final String dyGlyph = "dy";
    public static final String yGlyph = "y";
    public static final String dGlyph = "d";
    public static final String qGlyph = "q";
    public static final String oGlyph = "o";
    public static final String lGlyph = "l";
    public static final String chGlyph = "ch";
    public static final String shGlyph = "sh";
    public static final String xGlyph = "x";
    public static final String eGlyph = "e";
    public static final String aGlyph = "a";

    public static List<String> eGlyphList = Arrays.asList("e", "ee");
    public static List<String> inGlyphList = Arrays.asList("in", "iin", "iiin");
    public static List<String> olGlyphList = Arrays.asList("ol", "al", "or", "ar");

    public static final String[] gallowGlyphs = {"k", "t", "p", "f"};
    public static final String[] lineInitialGlyphs = {"o", "y", "d", "s"};
    public static final String[] lineFinalGlyphs = {"m", "g"};

    // determine which glyphs can be replaced by which other glyph
    public static Map<String, List<Substitution>> substitutionMap;
    private static Object[][][] substitutionObjects = {
            {{"k"}   ,  { new  Substitution( new String[] {"t"}, 77), new Substitution(new String[] {"p"}, 94), new Substitution(new String[] {"f"}, 100)}},
            {{"t"}   , { new Substitution(new String[] {"k"}, 84), new Substitution(new String[] {"p"}, 96), new Substitution(new String[] {"f"}, 100)}},
            {{"p"}   , { new Substitution(new String[] {"k"}, 59), new Substitution(new String[] {"t"}, 97), new Substitution(new String[] {"f"}, 100)}},
            {{"f"}   , { new Substitution(new String[] {"k"}, 56), new Substitution(new String[] {"t"}, 92), new Substitution(new String[] {"p"}, 100)}},

            {{"in"}  , { new Substitution(new String[] {"n"}, 8), new Substitution(new String[] {"iin"}, 84), new Substitution(new String[] {"iiin"}, 87), new Substitution(new String[] {"ir"}, 97), new Substitution(new String[] {"iir"}, 98), new Substitution(new String[] {"iis"}, 99), new Substitution(new String[] {"il"}, 100)}},
            {{"iin"} , { new Substitution(new String[] {"n"}, 13), new Substitution(new String[] {"in"}, 70), new Substitution(new String[] {"iiin"}, 74), new Substitution(new String[] {"ir"}, 92), new Substitution(new String[] {"iir"}, 97), new Substitution(new String[] {"is"}, 99), new Substitution(new String[] {"il"}, 100)}},
            {{"iiin"}, { new Substitution(new String[] {"n"}, 6), new Substitution(new String[] {"in"}, 31), new Substitution(new String[] {"iin"}, 90), new Substitution(new String[] {"ir"}, 98), new Substitution(new String[] {"iir"}, 100)}},

            {{"ir"}  , { new Substitution(new String[] {"r"}, 6), new Substitution(new String[] {"in"}, 33), new Substitution(new String[] {"iin"}, 95), new Substitution(new String[] {"iiin"}, 97), new Substitution(new String[] {"iir"}, 99), new Substitution(new String[] {"iiir"}, 100)}},
            {{"iir"} , { new Substitution(new String[] {"r"}, 6), new Substitution(new String[] {"in"}, 30), new Substitution(new String[] {"iin"}, 89), new Substitution(new String[] {"iiin"}, 91), new Substitution(new String[] {"ir"}, 99), new Substitution(new String[] {"iiir"}, 100)}},
            {{"iiir"}, { new Substitution(new String[] {"ir"}, 90), new Substitution(new String[] {"iir"}, 100)}},

            {{"is"}  , { new Substitution(new String[] {"in"}, 50), new Substitution(new String[] {"iis"}, 100)}},
            {{"iis"} , { new Substitution(new String[] {"iin"}, 50), new Substitution(new String[] {"is"}, 100)}},

            {{"il"}  , { new Substitution(new String[] {"in"}, 50), new Substitution(new String[] {"iil"}, 100)}},
            {{"iil"} , { new Substitution(new String[] {"iin"}, 50), new Substitution(new String[] {"il"}, 99), new Substitution(new String[] {"iiil"}, 100)}},
            {{"iiil"}, { new Substitution(new String[] {"il"}, 66), new Substitution(new String[] {"iil"}, 100)}},

            {{"im"}  , { new Substitution(new String[] {"in"}, 30), new Substitution(new String[] {"iin"}, 100)}},
            {{"iim"} , { new Substitution(new String[] {"in"}, 30), new Substitution(new String[] {"iin"}, 100)}},
            {{"iiim"}, { new Substitution(new String[] {"in"}, 30), new Substitution(new String[] {"iin"}, 100)}},

            {{"om"}  , { new Substitution(new String[] {"ol"}, 45), new Substitution(new String[] {"or"}, 94), new Substitution(new String[] {"og"}, 98), new Substitution(new String[] {"omg"}, 100)}},
            {{"am"}  , { new Substitution(new String[] {"al"}, 45), new Substitution(new String[] {"ar"}, 94), new Substitution(new String[] {"ag"}, 98), new Substitution(new String[] {"amg"}, 100)}},
            {{"og"}  , { new Substitution(new String[] {"or"}, 30), new Substitution(new String[] {"al"}, 63), new Substitution(new String[] {"ar"}, 100)}},
            {{"ag"}  , { new Substitution(new String[] {"ol"}, 48), new Substitution(new String[] {"or"}, 72), new Substitution(new String[] {"ar"}, 100)}},

            {{"ol"}  , { new Substitution(new String[] {"or"}, 30), new Substitution(new String[] {"al"}, 63), new Substitution(new String[] {"ar"}, 100)}},
            {{"or"}  , { new Substitution(new String[] {"ol"}, 47), new Substitution(new String[] {"al"}, 73), new Substitution(new String[] {"ar"}, 100)}},
            {{"al"}  , { new Substitution(new String[] {"ol"}, 48), new Substitution(new String[] {"or"}, 72), new Substitution(new String[] {"ar"}, 100)}},
            {{"ar"}  , { new Substitution(new String[] {"ol"}, 49), new Substitution(new String[] {"or"}, 73), new Substitution(new String[] {"al"}, 100)}},

            {{"e"}   , { new Substitution(new String[] {"e"}, 50), new Substitution(new String[] {"ee"}, 99), new Substitution(new String[] {"eee"}, 100)}},
            {{"ee"}  , { new Substitution(new String[] {"ch"}, 40), new Substitution(new String[] {"ch", "e"}, 50), new Substitution(new String[] {"e"}, 98), new Substitution(new String[] {"eee"}, 100)}},
            {{"eee"} , { new Substitution(new String[] {"ch"}, 10), new Substitution(new String[] {"ch", "e"}, 20), new Substitution(new String[] {"ee"}, 65), new Substitution(new String[] {"e"}, 100)}},

            {{"ch"}  , { new Substitution(new String[] {"ee"}, 10), new Substitution(new String[] {"ch", "e"}, 20), new Substitution(new String[] {"sh"}, 90), new Substitution(new String[] {"ckh"}, 97), new Substitution(new String[] {"cth"}, 100)}},
            {{"sh"}  , { new Substitution(new String[] {"ee"}, 10), new Substitution(new String[] {"ch"}, 90), new Substitution(new String[] {"ckh"}, 97), new Substitution(new String[] {"cth"}, 100)}},

            {{"ckh"} , { new Substitution(new String[] {"cth"}, 30), new Substitution(new String[] {"k", "ch"}, 50), new Substitution(new String[] {"t", "ch"}, 70), new Substitution(new String[] {"eke"}, 72), new Substitution(new String[] {"ete"}, 74), new Substitution(new String[] {"cph"}, 78), new Substitution(new String[] {"ch"}, 100)}},
            {{"cth"} , { new Substitution(new String[] {"ckh"}, 30), new Substitution(new String[] {"k", "ch"}, 50), new Substitution(new String[] {"t", "ch"}, 70), new Substitution(new String[] {"eke"}, 72), new Substitution(new String[] {"ete"}, 74), new Substitution(new String[] {"cph"}, 78), new Substitution(new String[] {"cfh"}, 80), new Substitution(new String[] {"ch"}, 100)}},

            {{"cs"} , { new Substitution(new String[] {"sh"}, 100)}},

            {{"ckhh"} , { new Substitution(new String[] {"ckh"}, 100)}},
            {{"cthh"} , { new Substitution(new String[] {"cth"}, 100)}},

            {{"ikh"} , { new Substitution(new String[] {"ckh"}, 100)}},
            {{"ith"} , { new Substitution(new String[] {"cth"}, 100)}},
            {{"iph"} , { new Substitution(new String[] {"cph"}, 100)}},
            {{"ifh"} , { new Substitution(new String[] {"cfh"}, 100)}},

            {{"eke"} , { new Substitution(new String[] {"ckh"}, 30), new Substitution(new String[] {"cth"}, 50), new Substitution(new String[] {"k", "ee"}, 56), new Substitution(new String[] {"t", "ee"}, 60), new Substitution(new String[] {"ete"}, 65), new Substitution(new String[] {"ee"}, 100)}},
            {{"ete"} , { new Substitution(new String[] {"ckh"}, 30), new Substitution(new String[] {"cth"}, 50), new Substitution(new String[] {"k", "ee"}, 56), new Substitution(new String[] {"t", "ee"}, 60), new Substitution(new String[] {"eke"}, 65), new Substitution(new String[] {"ee"}, 100)}},

            {{"cph"} , { new Substitution(new String[] {"ckh"}, 40), new Substitution(new String[] {"cth"}, 75), new Substitution(new String[] {"cfh"}, 80), new Substitution(new String[] {"ch"}, 100)}},
            {{"cfh"} , { new Substitution(new String[] {"ckh"}, 40), new Substitution(new String[] {"cth"}, 75), new Substitution(new String[] {"cph"}, 80), new Substitution(new String[] {"ch"}, 100)}},

            {{"y" }  , { new Substitution(new String[] {"o"}, 100)}},
            {{"o"}   , { new Substitution(new String[] {"y"}, 100)}},

            {{"n"}   , { new Substitution(new String[] {"r"}, 50), new Substitution(new String[] {"in"}, 63), new Substitution(new String[] {"iin"}, 100)}},
            {{"l"}   , { new Substitution(new String[] {"r"}, 100)}},
            {{"r"}   , { new Substitution(new String[] {"r"}, 50), new Substitution(new String[] {"s"}, 100)}},
            {{"g"}   , { new Substitution(new String[] {"m"}, 100)}},
            {{"s"}   , { new Substitution(new String[] {"r"}, 75), new Substitution(new String[] {"d"}, 100)}},
            {{"d"}   , { new Substitution(new String[] {"d"}, 90), new Substitution(new String[] {"s"}, 100)}},
            {{"a"}   , { new Substitution(new String[] {"a"}, 98), new Substitution(new String[] {"o"}, 100)}},
            {{"qo"}  , { new Substitution(new String[]{"o"}, 80), new Substitution(new String[]{"y"}, 100)}},
    };

    public static Map<String, List<Substitution>> combinableFinalSubstitutionMap;
    private static Object[][][] combinableFinalSubstitutionObjects = {
            {{"om"}  , { new Substitution(new String[] {"o"}, 8), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"ol"}, 75), new Substitution(new String[] {"or"}, 100) }},
            {{"am"}  , { new Substitution(new String[] {"o"}, 4),  new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"al"}, 70), new Substitution(new String[] {"ar"}, 100) }},
            {{"og"}  , { new Substitution(new String[] {"o"}, 8), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"or"}, 55), new Substitution(new String[] {"al"}, 80), new Substitution(new String[] {"ar"}, 100)}},
            {{"ag"}  , { new Substitution(new String[] {"o"}, 4),  new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"ol"}, 65), new Substitution(new String[] {"or"}, 80), new Substitution(new String[] {"ar"}, 100)}},

            {{"ol"}  , { new Substitution(new String[] {"o"}, 8), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"or"}, 54), new Substitution(new String[] {"al"}, 81), new Substitution(new String[] {"ar"}, 100)}},
            {{"or"}  , { new Substitution(new String[] {"o"}, 8), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"ol"}, 63), new Substitution(new String[] {"al"}, 81), new Substitution(new String[] {"ar"}, 100)}},
            {{"al"}  , { new Substitution(new String[] {"o"}, 4), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"ol"}, 64), new Substitution(new String[] {"or"}, 81), new Substitution(new String[] {"ar"}, 100)}},
            {{"ar"}  , { new Substitution(new String[] {"o"}, 4), new Substitution(new String[] {"y"}, 30), new Substitution(new String[] {"ol"}, 64), new Substitution(new String[] {"or"}, 82), new Substitution(new String[] {"al"}, 100)}},

            {{"y" }  , { new Substitution(new String[] {"o"}, 20), new Substitution(new String[] {"ol"}, 44), new Substitution(new String[] {"or"}, 60), new Substitution(new String[] {"al"}, 75), new Substitution(new String[] {"ar"}, 100)}},
            {{"o" }  , { new Substitution(new String[] {"y"}, 20), new Substitution(new String[] {"ol"}, 44), new Substitution(new String[] {"or"}, 60), new Substitution(new String[] {"al"}, 75), new Substitution(new String[] {"ar"}, 100)}},

            {{"d" }  , { new Substitution(new String[] {"dy"}, 70), new Substitution(new String[] {"d"}, 100)}},
            {{"dy" } , { new Substitution(new String[] {"d"}, 10), new Substitution(new String[] {"dy"}, 100)}},
    };

    // determines if a gallow glyph occurs mainly in the first line of a paragraph
    public static Map<String, Boolean> gallowGlyphDictionary;
    private static String[][] gallowGlyphDictionaryStrings = {
            {"k" , "false"},
            {"t" , "false"},
            {"p" , "true"},
            {"f" , "true"},
    };

    // glyphs typically used as group initial glyphs
    // list of tokens to which a prefix can be added
    public static Map<String, List<String>> prefixGlyphs;
    private static String[][][] prefixGlyphsStrings = {
            {{lGlyph}, {"k", "t", "p", "f", "d", "ch", "sh", "o", "a", "e", "i", "o"}},
            {{oGlyph}, {"k", "t", "p", "f", "d", "ch", "sh"}},
            {{yGlyph}, {"k", "t", "p", "f", "d", "ch", "sh"}},
            {{chGlyph}, {"k", "t", "p", "f", "d", "ol", "or", "al", "ar"}},
            {{shGlyph}, {"k", "t", "p", "f", "d", "ol", "or", "al", "ar"}},
            {{qGlyph}, {}},
            {{dGlyph}, {"a"}},
            {{xGlyph}, {"ol", "or", "al", "ar"}},
    };

    // glyphs used as ligature
    public static Map<String, Boolean> ligature;
    private static String[][] ligatureStrings ={
            {"ol","true"},
            {"or","true"},
            {"al","true"},
            {"ar","true"},
            {"dy","true"},
            {"qo","true"},
            {"ch","true"},
            {"sh","true"},
            {"cs","true"},
            {"eee","true"},
            {"ee","true"},
            {"cth","true"},
            {"cthh","true"},
            {"ckh","true"},
            {"ckhh","true"},
            {"cph","true"},
            {"cfh","true"},
            {"ith","true"},
            {"ikh","true"},
            {"iph","true"},
            {"ifh","true"},
            {"eke","true"},
            {"ete","true"},
            {"in","true"},
            {"iin","true"},
            {"iiin","true"},
            {"ir","true"},
            {"iir","true"},
            {"iiir","true"},
            {"is","true"},
            {"iis","true"},
            {"iiis","true"},
            {"il","true"},
            {"iil","true"},
            {"iiil","true"},
            {"im","true"},
            {"iim","true"},
            {"iiim","true"},
            {"om","true"},
            {"am","true"},
            {"og","true"},
            {"ag","true"},
    };

    // combinable ligature - determine which glyphs should be deleted after adding a `combinableLigature`
    public static Map<String, List<String>> combinableLigature;
    private static String[][][] combinableLigatureStrings = {
            {{"ol"}, {"l", "r", "s"}},
            {{"al"}, {"l", "r", "s"}},
            {{"or"}, {"l", "r", "s"}},
            {{"ar"}, {"l", "r", "s"}},
    };

    public static Map<String, List<String>> allCombinableLigature;
    private static String[][][] allCombinableLigatureStrings = {
            {{"ol"}, {"l", "r", "s"}},
            {{"al"}, {"l", "r", "s"}},
            {{"or"}, {"l", "r", "s"}},
            {{"ar"}, {"l", "r", "s"}},
            {{"om"}, {"l", "r", "s"}},
            {{"am"}, {"l", "r", "s"}},
    };

    // glyphs typical in line final position
    public static Map<String, String> finalLineReplacements;
    private static String[][] finalLineReplacementsStrings = {
            {"ol", "om"},
            {"or", "om"},
            {"al", "am"},
            {"ar", "am"},
            {"om", "og"},
            {"am", "ag"},
            {"im", "mg"},
            {"in", "n"},
            {"iin", "im"},
            {"iiin", "im"},
    };

    // determine which glyphs can stand in front of other glyphs
    public static Map<String, String> ingroupGlyphReplacements;
    private static String[][] ingroupGlyphReplacementsStrings = {
            {"y" , "o"},
            {"m", "r"},
            {"g", "r"},
            {"n", "r"},
            {"in", "ir"},
            {"iin", "iir"},
            {"dy", "da"}
    };

    public static Map<String, String> groupFinalGlyphReplacements;
    private static String[][] groupFinalGlyphReplacementsStrings = {
            {"o", "y"},
            {"a", "y"},
            {"k", "t"},
            {"t", "k"},
            {"p", "f"},
            {"f", "p"},
    };

    static {
        substitutionMap = Converter.constructSubstitutionMap(substitutionObjects);
        combinableFinalSubstitutionMap = Converter.constructSubstitutionMap(combinableFinalSubstitutionObjects);

        ligature = Converter.constructBooleanMap(ligatureStrings);
        gallowGlyphDictionary = Converter.constructBooleanMap(gallowGlyphDictionaryStrings);

        finalLineReplacements = Converter.constructMap(finalLineReplacementsStrings);
        ingroupGlyphReplacements = Converter.constructMap(ingroupGlyphReplacementsStrings);
        groupFinalGlyphReplacements = Converter.constructMap(groupFinalGlyphReplacementsStrings);

        combinableLigature = Converter.constructMap(combinableLigatureStrings);
        allCombinableLigature = Converter.constructMap(allCombinableLigatureStrings);
        prefixGlyphs = Converter.constructMap(prefixGlyphsStrings);
    }

    /**
     *
     * @param glyphGroup
     * @return
     */
    public static boolean startsWithGallow(String glyphGroup) {
        for (String gallow : gallowGlyphs) {
            if (glyphGroup.startsWith(gallow)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param glyphGroup
     * @return true if a `glyphGroup` starts with a glyph typical used line initialy (o,y,d,s)
     */
    public static boolean startsWithLineInitialGlyph(String glyphGroup) {
        for (String glyph : lineInitialGlyphs) {
            if (glyphGroup.startsWith(glyph)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param token
     * @return a `token` with the final glyph replaced if such a replacement is possible
     */
    public static String replaceLineFinalGlyph(String token) {
        String replacement = finalLineReplacements.get(token);
        if (replacement != null) {
            return replacement;
        }

        return token;
    }

    /**
     *
     * @param token
     * @return a `token` with the final glyph replaced if such a replacement is possible
     */
    public static String replaceGroupFinalGlyph(String token) {
        String replacement = groupFinalGlyphReplacements.get(token);
        if (replacement != null) {
            return replacement;
        }

        return token;
    }

    /**
     *
     * @param token
     * @return a `token` with the final glyph replaced if such a replacement is possible
     */
    public static String replaceIngroupGlyph(String token) {
        String replacement = ingroupGlyphReplacements.get(token);
        if (replacement != null) {
            return replacement;
        }

        return token;
    }

    /**
     *
     * @param glyphGroup
     * @return true if a `glyphGroup` ends with a glyph typical at the end of a line (m,g)
     */
    public static boolean endsWithEndGlyph(String glyphGroup) {
        for (String glyph : lineFinalGlyphs) {
            if (glyphGroup.endsWith(glyph)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param glyphGroup
     * @return true if a `glyphGroup` contains a gallow preffered in paragraph initial lines (p,f)
     */
    public static boolean containsFirstLineGallow(String glyphGroup) {
        for (String gallow : gallowGlyphs) {
            if (gallowGlyphDictionary.get(gallow).booleanValue() && glyphGroup.contains(gallow)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param glyphGroup
     * @param newGallow
     * @return a `glyphGroup` with the initial gallows replaced by a `newGallow` [p,f]--> [k,t]
     */
    public static String replaceFirstLineGallow(String glyphGroup, String newGallow) {
        String ret = glyphGroup;
        for (String gallow : gallowGlyphs) {
            if (gallowGlyphDictionary.get(gallow).booleanValue() && glyphGroup.contains(gallow)) {
                ret = ret.replace(gallow, newGallow);
            }
        }

        return ret;
    }

    /**
     * replaced all gallows in `glypGroup` with `newGallow`
     *
     * @param glyphGroup
     * @param newGallow
     * @return
     */
    public static String replaceGallows(String glyphGroup, String newGallow) {
        String ret = glyphGroup;
        for (String gallow : gallowGlyphs) {
            if (glyphGroup.contains(gallow)) {
                ret = ret.replace(gallow, newGallow);
            }
        }

        return ret;
    }

    /**
     *
     * @param glyphGroup
     * @return true if the `glyphGroup` contains a gallow (k,t,p,f)
     */
    public static boolean containsGallow(String glyphGroup) {
        for (String gallow : gallowGlyphs) {
            if (glyphGroup.contains(gallow)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param glyphGroups
     * @return true if one of the `glyphGroups` contains a gallow (k,t,p,f)
     */
    public static boolean containsGallow(String[] glyphGroups) {
        for (String glyphGroup : glyphGroups) {
            if (containsGallow(glyphGroup)) {
                return true;
            }
        }

        return false;
    }

    /**
     * eturns the initial group for a `glyphGroup` if this is a ligaure or nil
     * @param glyphGroup
     * @return
     */
    public static String determineStartLigature(String glyphGroup) {
        // make sure 'eee' is not mixed up with 'ee'
        if (glyphGroup.startsWith("eee")) {
            return "eee";
        }
        for (String lig : ligature.keySet()) {
            if (glyphGroup.startsWith(lig)) {
                return lig;
            }
        }

        return null;
    }

    /**
     * a list of similar tokens if available
     * @param token
     * @param isFinalToken
     * @return a list of similar tokens or null
     */
    public static List<Substitution> similarGlyph(String token, boolean isFinalToken) {
        if (isFinalToken && combinableFinalSubstitutionMap.containsKey(token)) {
            return combinableFinalSubstitutionMap.get(token);
        }
        return substitutionMap.get(token);
    }

    /**
     * determines if a `glyph` is a gallow glyph (k,t,p,f)
     * @param glyph
     * @return
     */
    public static boolean isGallow(String glyph) {
        return gallowGlyphDictionary.keySet().contains(glyph);
    }

    /**
     * determines if a `token` is a CombinableLigature (ol, or, al, ar)
     * @param token
     * @return
     */
    public static boolean isCombinableLigature(String token) {
        return combinableLigature.keySet().contains(token);
    }

    /**
     * try to find a shorter replacement for a `token`
     * @param token
     * @return
     */
    public static Substitution searchShorterGlyph(String token) {
        int glyphLength = token.length();
        int returnSubLength = glyphLength;
        Substitution returnSub = null;
        if (glyphLength > 1) {
            List<Substitution> substitutions = substitutionMap.get(token);

            if (substitutions != null) {
                for (Substitution substitution : substitutions) {
                    if (substitution.count() == 1) {
                        int substitutionLength = substitution.get(0).length();
                        if (substitutionLength < returnSubLength) {
                            returnSubLength = substitutionLength;
                            returnSub = substitution;
                        }
                    }
                }
            }
        }

        return returnSub;
    }

    /**
     * returns a RANDOM line initial `glyph`
     * @param randomNumberGenerator
     * @return
     */
    public static String randomLineInitalGlyph(I_RandomNumberGenerator randomNumberGenerator) {
        int rand = randomNumberGenerator.rand(100);

        if (rand <= 45) {
            return lineInitialGlyphs[0];
        } else if (rand <= 75) {
            return lineInitialGlyphs[1];
        } else if (rand <= 90) {
            return lineInitialGlyphs[2];
        } else if (rand <= 99) {
            return lineInitialGlyphs[3];
        }

        return lineInitialGlyphs[0];
    }

    /**
     * if `firstLine` is true it returns all gallows and if it is set to false only 'k' (gallowGlyphs[0]) and 't' (gallowGlyphs[1])
     * @param firstLine
     * @param randomNumberGenerator
     * @return a RANDOM gallow glyph
     */
    public static String randomGallow(boolean firstLine, I_RandomNumberGenerator randomNumberGenerator) {
        int rand = randomNumberGenerator.rand(100);

        if (rand <= 34) {
            return gallowGlyphs[0];
        } else if (rand <= 49) {
            return gallowGlyphs[1];
        } else if (rand <= 89) {
            if (firstLine) {
                return gallowGlyphs[2];
            } else {
                return gallowGlyphs[0];
            }
        } else if (rand <= 99) {
            if (firstLine) {
                return gallowGlyphs[3];
            } else {
                return gallowGlyphs[1];
            }
        }

        return gallowGlyphs[0];
    }
}
