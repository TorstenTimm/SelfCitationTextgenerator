package de.voynich.text;

import java.io.File;

/**
 * Class <code>Constants</code> stores common definitions
 */
public final class Constants {

	public static final boolean DEBUG = false;

	public static final String ENTER = "\n";
	public static final char SEPERATOR_CHAR = File.separatorChar;

	public static final String SAVE_DATE_PATTERN_DEFAULT = "yyyyMMddHHmm";
	public static final String SAVE_FILENAME_PATTERN_DEFAULT = "vms_generate_{date}.txt";

	// default seed for initializing the text generator [line f103v.P.9]
	public static final String SAMPLE_CURRIER_B_TEXT_LINE = "pchal shal shorchdy okeor okain shedy pchedy qotchedy qotar ol lkar";

	public static final String NEW_LINE_SEPEARTOR = "#";

	public static final int PARAGRAPH_STARTS_WITH_GALLOW_PROPABILITY = 94;

	public static final int RANDOM_SEED_DEFAULT = 19;

	public static final int SUGGESTIONS_PROBABILITY_DEFAULT = 40;

	/// the length of a line
	public static final int MAX_LINE_LENGTH_DEFAULT  = 55;
	public static final int MIN_LINE_LENGTH_DEFAULT  = 15;
	public static final int LINES_TO_CREATE_DEFAULT  = 500;
	public static final int LINES_PER_PAGE_DEFAULT   = 29;
	public static final int MAX_REPEAT_COUNT_DEFAULT = 3;

	public static final int METHOD_REUSE_LAST_PROBABILITY_DEFAULT = 10;
    public static final int METHOD_DISMISS_COMBINED_GROUPS_AS_SOURCE_PROBABILITY = 30;

    static final GlyphGroup DAIIN_GLYPH_GROUP  = new GlyphGroup( "daiin",  GlyphGroup.GENERATE_TYPE.INITIAL);
	static final GlyphGroup CHOL_GLYPH_GROUP   = new GlyphGroup( "chol",   GlyphGroup.GENERATE_TYPE.INITIAL);
	static final GlyphGroup OL_GLYPH_GROUP     = new GlyphGroup( "ol",     GlyphGroup.GENERATE_TYPE.INITIAL);
	static final GlyphGroup CHEDY_GLYPH_GROUP  = new GlyphGroup( "chedy",  GlyphGroup.GENERATE_TYPE.INITIAL);
	static final GlyphGroup CHEODY_GLYPH_GROUP = new GlyphGroup( "cheody", GlyphGroup.GENERATE_TYPE.INITIAL);

	public static final int SAME_POSITION_PROBABILITY_DEFAULT=28;
	public static final int PARAGRAPH_INITIAL_PROBABILITY = 70;

	public static final float I_MIN_PERCENTAGE_DEFAULT = 0.20f;
	public static final float OL_MIN_PERCENTAGE_DEFAULT = 0.25f;
	public static final float DY_MIN_PERCENTAGE_DEFAULT = 0.25f;
}
