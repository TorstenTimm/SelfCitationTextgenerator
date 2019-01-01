package de.voynich.text.sourcechooser;

import de.voynich.text.GlyphGroup;
import de.voynich.text.StatisticHelper;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.List;

/**
 * interface for PageSourceGroupChooser, PositionSourceGroupChooser and RandomSourceGroupChooser
 * @see PageSourceGroupChooser - DEFAULT class
 * @see PositionSourceGroupChooser
 * @see RandomSourceGroupChooser
 */
public interface I_SourceGroupChooser {

    enum ChooseMode {
        RANDOM, LOCALY, PARAGRAPH_INITIAL, SUGGESTION
    }

    void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator);

    /**
     * method for choosing two source glyph groups
     * @param generatedLinesList
     * @param paragraphInitialLinesList
     * @param actualLineList
     * @param statistics
     * @param isParagraphInitialLine
     * @param isLineInitial
     * @param initialLineCount
     * @return
     */
    List<GlyphGroup> chooseSourceGroup(List<List<GlyphGroup>> generatedLinesList,
                                              List<List<GlyphGroup>> paragraphInitialLinesList,
                                              List<GlyphGroup> actualLineList,
                                              StatisticHelper statistics,
                                              boolean isParagraphInitialLine,
                                              boolean isLineInitial,
                                              int initialLineCount);
}
