package de.voynich.text.sourcechooser;

import de.voynich.text.*;
import de.voynich.text.util.Config;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * class with methods for choosing source glyph groups near the writing position
 *
 * set method.sourceChooser=position in conf.properties to activate this class
 */
public class PositionSourceGroupChooser implements I_SourceGroupChooser {

    public I_RandomNumberGenerator randomNumberGenerator;

    private int startSamePositionProbability;
    private int samePositionProbability = 80;


    public PositionSourceGroupChooser(I_RandomNumberGenerator randomNumberGenerator, SelfCitationTextGenerator.CURRIER currierType) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.startSamePositionProbability = Config.methodSourceChooserSamePositionProbability;
    }

    @Override
    public void setRandomNumberGenerator(I_RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public List<GlyphGroup> chooseSourceGroup(List<List<GlyphGroup>> lineArrays,
                                              List<List<GlyphGroup>> paragraphInitialLineArrays,
                                              List<GlyphGroup> actualLineList,
                                              StatisticHelper statistics,
                                              boolean isParagraphInitialLine,
                                              boolean isLineInitial,
                                              int initialLineCount) {

        int rand = randomNumberGenerator.rand(100);

        // for the first line lineArrays is empty. therefore it is necessary to chose the source groups randomly.
        // for paragraphIni
        PageSourceGroupChooser.ChooseMode mode = lineArrays.size() < initialLineCount ? PageSourceGroupChooser.ChooseMode.RANDOM : isParagraphInitialLine && rand < 70 && paragraphInitialLineArrays.size() > 1 ? PageSourceGroupChooser.ChooseMode.PARAGRAPH_INITIAL : PageSourceGroupChooser.ChooseMode.LOCALY;

        Map<GlyphGroup, Long> groupDictionary = statistics.validGroupDictionary;
        List<GlyphGroup> ret = new ArrayList<>();
        switch (mode) {
            case RANDOM: {
                // randomly
                // pick first source group
                rand = randomNumberGenerator.rand(groupDictionary.size());
                ArrayList<GlyphGroup> arrayList = new ArrayList<>(groupDictionary.size());
                arrayList.addAll(groupDictionary.keySet());
                ret.add(arrayList.get(rand));
                // pick second source group
                rand = randomNumberGenerator.rand(groupDictionary.size());
                ret.add(arrayList.get(rand));
                break;
            }
            case LOCALY:
                rand = randomNumberGenerator.rand(100);

                // actual line contains less then four groups
                if (actualLineList.size() <= 3 && rand <= 10) {
                    switch (actualLineList.size()) {
                        case 2: {
                            // line contains 2 groups --> in 60 % use another line
                            int random = randomNumberGenerator.rand(100);
                            if (random < 60) {
                                rand = 11;
                            }
                            break;
                        }
                        case 3: {
                            // line contains 3 groups --> in 40 % use another line
                            int random = randomNumberGenerator.rand(100);
                            if (random< 40) {
                                rand = 11;
                            }
                            break;
                        }
                        case 1:
                        default: {
                            // actual line contains none or only one group --> use another line
                            rand = 11;
                            break;
                        }
                    }
                }
                if (rand <= 2) { // case 0...2:
                    // pick the last generated group as source group
                    ret.add(actualLineList.get(actualLineList.size()-1));
                    // pick second source group
                    if (actualLineList.size()-1 > 0) {
                        ret.add(actualLineList.get(actualLineList.size()-2));
                    } else {
                        ret.add(actualLineList.get(actualLineList.size()-1));
                    }
                } else if (rand <= 10) { // case 3...10:
                    // pick a RANDOM group within the same line as source group
                    rand = randomNumberGenerator.rand(actualLineList.size());
                    ret.add(actualLineList.get(rand));
                    // pick second source group
                    if (rand > 0) {
                        ret.add(actualLineList.get(rand - 1));
                    } else if (rand < actualLineList.size() - 1) {
                        ret.add(actualLineList.get(rand + 1));
                    } else {
                        ret.add(actualLineList.get(rand));
                    }
                } else if (rand <= 75) { // case 11...75:
                    // pick one of the lines of the actual page as source line
                    int line = lineArrays.size() - 1;
                    rand = randomNumberGenerator.rand(Math.min(10, lineArrays.size()));
                    switch (rand) {
                        case 10:
                        case 9:
                        case 8:
                        case 7:
                        case 6:
                        case 5: {
                            // pick one of the lines of the actual page as source line
                            line = lineArrays.size() - (1 + randomNumberGenerator.rand(Math.max(2, statistics.linesInPage) - 2));
                            if (line < 0) {
                                line = lineArrays.size() - 1;
                            }
                            break;
                        }
                        case 4: {
                            // increase the chance for line 4 to be picked
                            line = lineArrays.size() - 4;
                            break;
                        }
                        case 3: {
                            // increase the chance for line 3 to be picked
                            line = lineArrays.size() - 3;
                            break;
                        }
                        case 2: {
                            // increase the chance for next to last line to be picked
                            line = lineArrays.size() - 2;
                            break;
                        }
                        default: {
                            // increase the chance for last line to be picked
                            line = lineArrays.size() - 1;
                            break;
                        }
                    }

                    // pick a source group
                    List<GlyphGroup> sourceLine = lineArrays.get(line);
                    int pos = ChooserHelper.calcLinePosition(actualLineList, sourceLine);
                    rand = randomNumberGenerator.rand(100);
                    // check chance for picking a group in the same position
                    //if (rand <= startSamePosition_probability) || (pos > 2 && rand <= samePosition_probabilty) {
                    if ((rand <= startSamePositionProbability) || (pos > 2 && rand <= samePositionProbability)) {
                        // check if pos is out of range
                        if (pos < 0 || pos >= sourceLine.size()) {
                            if (line == lineArrays.size() - 1) {
                                // last group of previous line
                                pos = sourceLine.size() - 1;
                            } else {
                                // out of range -> RANDOM
                                pos = randomNumberGenerator.rand(sourceLine.size());
                            }
                        }
                    } else {
                        pos = randomNumberGenerator.rand(sourceLine.size());
                    }

                    // pick first source group
                    ret.add(sourceLine.get(pos));
                    // pick second source group
                    if (pos > 0) {
                        ret.add(sourceLine.get(pos - 1));
                    } else if (pos < sourceLine.size() - 1) {
                        ret.add(sourceLine.get(pos + 1));
                    } else {
                        ret.add(sourceLine.get(pos));
                    }
                } else { // default:
                    // if less then 20 % of the generated groups containing the 'i' glyph use 'daiin' as source group
                    // otherwise use a RANDOM group as source group
                    ret = statistics.suggestGroups();
                    if (ret.size() == 0) {
                        // RANDOM
                        rand = randomNumberGenerator.rand(groupDictionary.size());
                        List<GlyphGroup> groupList = new ArrayList<GlyphGroup>(groupDictionary.size());
                        groupList.addAll(groupDictionary.keySet());
                        ret.add(groupList.get(rand));
                    }
                    if (ret.size() == 1) {
                        // RANDOM
                        rand = randomNumberGenerator.rand(groupDictionary.size());
                        List<GlyphGroup> groupList = new ArrayList<GlyphGroup>(groupDictionary.size());
                        groupList.addAll(groupDictionary.keySet());
                        ret.add(groupList.get(rand));
                    }
                }
                break;
            case PARAGRAPH_INITIAL:
                // for PARAGRAPH_INITIAL initial lines use another PARAGRAPH_INITIAL initial line for picking the source group
                rand = randomNumberGenerator.rand(100);
                if (!isLineInitial && rand <= 20) {
                    rand = 21;
                }

                // actual line contains less then four groups
                if (actualLineList.size() <= 3 && rand <= 20) {
                    switch (actualLineList.size()) {
                        case 2: {
                            // line contains 2 groups --> in 60 % use another line
                            int random = randomNumberGenerator.rand(100);
                            if (random < 60) {
                                rand = 21;
                            }
                            break;
                        }
                        case 3: {
                            // line contains 3 groups --> in 40 % use another line
                            int random = randomNumberGenerator.rand(100);
                            if (random< 40) {
                                rand = 21;
                            }
                            break;
                        }
                        case 1:
                        default: {
                            // actual line contains none or only one group --> in 100 % use another line
                            rand = 21;
                            break;
                        }
                    }
                }
                if (rand <= 5) { // case 0...5:{
                    // pick the last generated group as source group
                    ret.add(actualLineList.get(actualLineList.size() - 1));
                    // pick second source group
                    if (actualLineList.size() - 1 > 0) {
                        ret.add(actualLineList.get(actualLineList.size() - 2));
                    } else {
                        ret.add(actualLineList.get(actualLineList.size() - 1));
                    }
                } else if (rand <=20) { // case 6...20:{
                    // pick a RANDOM group within the same line
                    rand = randomNumberGenerator.rand(actualLineList.size());
                    // pick first soruce group
                    ret.add(actualLineList.get(rand));
                    // pick second source group
                    if (rand > 0) {
                        ret.add(actualLineList.get(rand - 1));
                    } else if (rand < actualLineList.size() - 1) {
                        ret.add(actualLineList.get(rand + 1));
                    } else {
                        ret.add(actualLineList.get(rand));
                    }
                } else { // default: {
                    // use another PARAGRAPH_INITIAL initial line for picking the source group
                    rand = randomNumberGenerator.rand(paragraphInitialLineArrays.size());
                    List<GlyphGroup> sourceLine = paragraphInitialLineArrays.get(rand);

                    int pos = 0;
                    if (isLineInitial) {
                        pos = randomNumberGenerator.rand(3);
                        if (pos >= sourceLine.size()) {
                            pos = randomNumberGenerator.rand(sourceLine.size());
                        }
                    } else {
                        pos = randomNumberGenerator.rand(sourceLine.size());
                    }
                    // pick first soruce group
                    ret.add(sourceLine.get(pos));
                    // pick second source group
                    if (pos > 0) {
                        ret.add(sourceLine.get(pos - 1));
                    } else if (pos < sourceLine.size() - 1) {
                        ret.add(sourceLine.get(pos + 1));
                    } else {
                        ret.add(sourceLine.get(pos));
                    }
                }
                break;
        }

        // remove initial gallow glyph if there is any
        ChooserHelper.removeInitialGallow(ret);

        return ret;
    }


}
