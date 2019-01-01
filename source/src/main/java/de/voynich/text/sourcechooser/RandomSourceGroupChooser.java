package de.voynich.text.sourcechooser;

import de.voynich.text.GlyphGroup;
import de.voynich.text.StatisticHelper;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.List;

/**
 * class with methods for choosing source glyph groups randomly
 *
 * set method.sourceChooser=random in conf.properties to activate this class
 */
public class RandomSourceGroupChooser implements I_SourceGroupChooser {

    public I_RandomNumberGenerator randomNumberGenerator;

    public RandomSourceGroupChooser(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public List<GlyphGroup> chooseSourceGroup(List<List<GlyphGroup>> generatedLinesList, List<List<GlyphGroup>> paragraphInitialLinesList, List<GlyphGroup> actualLineList, StatisticHelper statistics, boolean isParagraphInitialLine, boolean isLineInitial, int initialLineCount) {
        return ChooserHelper.chooseRandomly(statistics.validGroupDictionary, randomNumberGenerator);
    }
}
