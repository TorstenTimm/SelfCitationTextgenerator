package de.voynich.text.sourcechooser;

import de.voynich.text.*;
import de.voynich.text.util.Config;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default class with methods for choosing source glyph groups from actual page
 *
 * set method.sourceChooser=page in conf.properties to activate this class
 */
public class PageSourceGroupChooser implements I_SourceGroupChooser {

    public I_RandomNumberGenerator randomNumberGenerator;

    private int samePositionProbability;
    private int lineInitialSamePositionProbability;
    private int paragraphInitialProbability = Constants.PARAGRAPH_INITIAL_PROBABILITY;

    public PageSourceGroupChooser(I_RandomNumberGenerator randomNumberGenerator, SelfCitationTextGenerator.CURRIER currierType) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.samePositionProbability = Config.methodSourceChooserSamePositionProbability;
        this.lineInitialSamePositionProbability = Math.max(10, (int) samePositionProbability / 2);
    }

    @Override
    public List<GlyphGroup> chooseSourceGroup(List<List<GlyphGroup>> generatedLinesList,
                                              List<List<GlyphGroup>> paragraphInitialLinesList,
                                              List<GlyphGroup> actualLineList,
                                              StatisticHelper statistics,
                                              boolean isParagraphInitialLine,
                                              boolean isLineInitial,
                                              int initialLineCount) {

        ChooseMode mode = ChooseMode.LOCALY;

        // for the first line generatedLinesList is empty. therefore it is necessary to chose the source group randomly.
        if (generatedLinesList.size() < initialLineCount) {
            mode = ChooseMode.RANDOM;
        }

        // for paragraph initial lines
        int rand = randomNumberGenerator.rand(100);
        if (isParagraphInitialLine && paragraphInitialLinesList.size() > 1 && (isLineInitial || (actualLineList.size() > 1 && rand < paragraphInitialProbability ))) {
            mode = ChooseMode.PARAGRAPH_INITIAL;
        }

        if (statistics.hasSuggestedGroups()) {
            rand = randomNumberGenerator.rand(100);
            if (rand < statistics.getSuggestionsProbability()) {
                mode = ChooseMode.SUGGESTION;
            }
        }

        List<GlyphGroup> ret;
        switch (mode) {
            case RANDOM:
                ret = chooseRandomly(statistics.validGroupDictionary);
                break;
            case LOCALY:
                ret = chooseFromPage(generatedLinesList, actualLineList, statistics.linesInPage, isLineInitial);
                break;
            case PARAGRAPH_INITIAL:
                ret = chooseFromParagraphInitialLines(paragraphInitialLinesList);
                break;
            case SUGGESTION:
                ret = chooseSuggestion(statistics.validGroupDictionary, statistics);
                break;
            default:
                ret = chooseRandomly(statistics.validGroupDictionary);
                break;
        }

        ChooserHelper.removeInitialGallow(ret);

        return ret;
    }

    @Override
    public void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    private List<GlyphGroup> chooseSuggestion(Map<GlyphGroup,Long> groupDictionary, StatisticHelper statistics) {
        List<GlyphGroup> ret = statistics.suggestGroups();
        if (ret.size() == 1) {
            List<GlyphGroup> retRandom = chooseRandomly(groupDictionary);
            ret.add(retRandom.get(0));
        }

        return ret;
    }

    private List<GlyphGroup> chooseFromPage(List<List<GlyphGroup>> generatedLinesList, List<GlyphGroup> actualLineList, int linesInPage, boolean isLineInitial) {
        List<GlyphGroup> ret = new ArrayList<>();

        // pick one of the previous lines of the actual page
        int line = generatedLinesList.size() - (1 + randomNumberGenerator.rand(Math.max(2, linesInPage) - 2));
        if (line < 0) {
            line = generatedLinesList.size() - 1;
        }
        List<GlyphGroup> sourceLine = generatedLinesList.get(line);

        // calc line position
        int pos;
        int rand = randomNumberGenerator.rand(100);
        int probability = isLineInitial ? lineInitialSamePositionProbability : samePositionProbability;
        // check chance for picking a group in the same position
        if (rand <= probability) {
            pos = ChooserHelper.calcLinePosition(actualLineList, sourceLine);
        } else {
            pos = randomNumberGenerator.rand(sourceLine.size());
        }

        // pick first source group
        ret.add(sourceLine.get(pos));
        // pick second source group
        if (pos < sourceLine.size() - 1) {
            ret.add(sourceLine.get(pos + 1));
        } else if (pos > 0) {
            ret.add(sourceLine.get(pos - 1));
        } else {
            ret.add(sourceLine.get(pos));
        }

        return ret;
    }

    private List<GlyphGroup> chooseFromParagraphInitialLines(List<List<GlyphGroup>> paragraphInitialLinesList) {
        List<GlyphGroup> ret = new ArrayList<>();

        // use another PARAGRAPH_INITIAL initial line for picking the source group
        int rand = randomNumberGenerator.rand(paragraphInitialLinesList.size());
        List<GlyphGroup> sourceLine = paragraphInitialLinesList.get(rand);

        int pos = randomNumberGenerator.rand(sourceLine.size());

        // pick first source group
        ret.add(sourceLine.get(pos));
        // pick second source group
        if (pos < sourceLine.size() - 1) {
            ret.add(sourceLine.get(pos + 1));
        } else if (pos > 0) {
            ret.add(sourceLine.get(pos - 1));
        } else {
            ret.add(sourceLine.get(pos));
        }

        return ret;
    }

    private List<GlyphGroup> chooseRandomly(Map<GlyphGroup, Long> groupDictionary) {
        return ChooserHelper.chooseRandomly(groupDictionary, randomNumberGenerator);
    }


}
