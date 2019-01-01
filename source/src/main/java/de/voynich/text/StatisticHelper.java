package de.voynich.text;

import de.voynich.text.canfollow.I_CanFollow;
import de.voynich.text.canfollow.VoynichCanFollow;
import de.voynich.text.util.Config;
import de.voynich.text.util.random.I_RandomNumberGenerator;

import java.text.NumberFormat;
import java.util.*;

/**
 * Class <code>StatisticHelper</code> to store statistic values for the generated text
 */
public class StatisticHelper {

    private I_CanFollow vmsCanFollow = new VoynichCanFollow();

    public final int suggestionsProbability;

    public enum SUGGEST_METHOD { TOP_TOKEN, DAIIN_OL_CHEDY, RANDOM, NONE }

    public SUGGEST_METHOD suggestMethod;

    public I_RandomNumberGenerator randomNumberGenerator;

    private final SelfCitationTextGenerator.CURRIER currierType;

    /// dictionary containing valid glyph groups
    public Map<GlyphGroup, Long> validGroupDictionary = new HashMap<>();

    /// dictionary containing all glyph groups
    public Map<GlyphGroup, Long> allGroupMap = new HashMap<>();

    public I_CanFollow canFollow;

    public int linesInPage      = 0;
    public int linesInParagraph = 0;

    public int statTokens       = 0;

    public int statTokensI      = 0;
    public int statTokensDy     = 0;
    public int statTokensOl     = 0;

    public int repeatedTokensI  = 0;
    public int repeatedTokensDy = 0;
    public int repeatedTokensOl = 0;
    public int repeatedTokensUnknown = 0;

    public int existsNotInVmsCount =0;
    public int existsInVmsCount = 0;

    public Set<GlyphGroup> groupIListForDoc = new HashSet<>();
    public Set<GlyphGroup> groupDyListForDoc = new HashSet<>();
    public Set<GlyphGroup> groupOlListForDoc = new HashSet<>();

    public Map<GlyphGroup, Long> usageOlMapForDoc = new HashMap<>();
    public Map<GlyphGroup, Long> usageDyMapForDoc = new HashMap<>();
    public Map<GlyphGroup, Long> usageIMapForDoc = new HashMap<>();

    public Set<GlyphGroup> groupOlListForPage = new HashSet<>();
    public Set<GlyphGroup> groupDyListForPage = new HashSet<>();
    public Set<GlyphGroup> groupIListForPage = new HashSet<>();

    public Map<GlyphGroup, Long> usageOlMapForPage = new HashMap<>();
    public Map<GlyphGroup, Long> usageDyMapForPage = new HashMap<>();
    public Map<GlyphGroup, Long> usageIMapForPage = new HashMap<>();

    public StatisticHelper(SelfCitationTextGenerator.CURRIER currierType, I_RandomNumberGenerator randomNumberGenerator, I_CanFollow canFollow, SUGGEST_METHOD suggestMethod, int suggestionsProbability) {
        this.currierType = currierType;
        this.randomNumberGenerator = randomNumberGenerator;
        this.canFollow = canFollow;
        this.suggestMethod = suggestMethod;
        this.suggestionsProbability = suggestionsProbability;
    }

    public float getPercentageDy() {
        return (float) statTokensDy / (float) statTokens;
    }

    public float getPercentageI() {
        return (float) statTokensI / (float) statTokens;
    }

    public float getPercentageOl() {
        return (float) statTokensOl / (float) statTokens;
    }

    public void newLine() {
        linesInPage += 1;
        linesInParagraph += 1;
    }

    public void newPage() {
        linesInPage = 0;
        linesInParagraph = 0;

        groupIListForPage.clear();
        groupDyListForPage.clear();
        groupOlListForPage.clear();

        usageIMapForPage.clear();
        usageDyMapForPage.clear();
        usageOlMapForPage.clear();
    }

    public void newParagraph() {
        linesInParagraph = 0;
    }

    void countGlyphGroup(GlyphGroup glyphGroup) {
        if (!vmsCanFollow.isValid(glyphGroup)) {
            existsNotInVmsCount++;
        } else {
            existsInVmsCount++;
        }
    }

    /**
     * if a `glyhGroup` is not to complex or invalid store it in the validGroupDictionary
     * @param glyphGroup
     */
    public void remember(GlyphGroup glyphGroup) {

        if (glyphGroup.generateType == GlyphGroup.GENERATE_TYPE.INITIAL || ((canFollow.isValid(glyphGroup) && glyphGroup.getTokenCount() < 8 && glyphGroup.generateType != GlyphGroup.GENERATE_TYPE.COMBINE))) {
            addToDictionary(glyphGroup, validGroupDictionary);
        }
        addToDictionary(glyphGroup, allGroupMap);
        countGlyphGroup(glyphGroup);

        statTokens += 1;
        if (glyphGroup.isTypeI()) {
            addToUsageMap(glyphGroup, usageIMapForDoc);
            addToUsageMap(glyphGroup, usageIMapForPage);
            groupIListForDoc.add(glyphGroup);
            groupIListForPage.add(glyphGroup);

            repeatedTokensDy    = 0;
            repeatedTokensOl    = 0;
            repeatedTokensUnknown = 0;

            statTokensI += 1;
            repeatedTokensI += 1;
        } else if (glyphGroup.isTypeDy()) {
            addToUsageMap(glyphGroup, usageDyMapForDoc);
            addToUsageMap(glyphGroup, usageDyMapForPage);

            groupDyListForDoc.add(glyphGroup);
            groupDyListForPage.add(glyphGroup);

            repeatedTokensUnknown = 0;
            repeatedTokensI     = 0;
            repeatedTokensOl    = 0;

            statTokensDy += 1;
            repeatedTokensDy += 1;
        } else if (glyphGroup.isTypeOl()) {
            addToUsageMap(glyphGroup, usageOlMapForDoc);
            addToUsageMap(glyphGroup, usageOlMapForPage);

            groupOlListForDoc.add(glyphGroup);
            groupOlListForPage.add(glyphGroup);

            repeatedTokensUnknown = 0;
            repeatedTokensI     = 0;
            repeatedTokensDy    = 0;

            statTokensOl += 1;
            repeatedTokensOl += 1;
        } else {
            repeatedTokensI  = 0;
            repeatedTokensDy = 0;
            repeatedTokensOl = 0;

            repeatedTokensUnknown += 1;
        }
    }

    protected void addToDictionary(GlyphGroup glyphGroup, Map<GlyphGroup, Long> dictionary) {
        if (!dictionary.containsKey(glyphGroup)) {
            dictionary.put(glyphGroup, 1L);
        } else {
            Long count = Long.valueOf(dictionary.get(glyphGroup).longValue()+1);
            dictionary.put(glyphGroup, count);
        }
    }

    private void addToUsageMap(GlyphGroup glyphGroup, Map<GlyphGroup, Long> groupMac) {
        Long countLong = groupMac.get(glyphGroup);
        if (countLong == null) {
            countLong = Long.valueOf(0);
        }
        countLong++;
        groupMac.put(glyphGroup, countLong);
    }

    public boolean hasSuggestedGroups() {
        if (suggestMethod.equals(SUGGEST_METHOD.NONE)) {
            return false;
        }
        float percentageI = getPercentageI();
        float percentageDy = getPercentageDy();
        float percentageOl = getPercentageOl();
        switch (currierType) {
            case B:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    return true;
                } else if (percentageDy < Config.DY_MIN_PERCENTAGE) {
                    return true;
                }
                break;
            case A:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    return true;
                } else if (percentageOl < Config.OL_MIN_PERCENTAGE) {
                    return true;
                }
                break;
            default:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    return true;
                }
                break;
        }

        return false;
    }

    public List<GlyphGroup> suggestGroups() {
        List<GlyphGroup> ret = new ArrayList<>();
        // if less then 20 % of the generated groups containing the 'i' glyph use 'daiin' as source group
        // otherwise use a RANDOM group as source group
        float percentageI = getPercentageI();
        float percentageDy = getPercentageDy();
        float percentageOl = getPercentageOl();
        switch (currierType) {
            case B:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    ret.add(chooseIGroup());
                } else if (percentageDy < Config.DY_MIN_PERCENTAGE) {
                    ret.add(chooseDyGroup());
                }
                break;
            case A:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    ret.add(chooseIGroup());
                } else if (percentageOl < Config.OL_MIN_PERCENTAGE) {
                    ret.add(chooseOlGroup());
                }
                break;
            default:
                if (percentageI < Config.I_MIN_PERCENTAGE) {
                    ret.add(chooseIGroup());
                }
                break;
        }

        return ret;
    }

    public List<GlyphGroup> suggestGroupsForce(int tryCount) {
        List<GlyphGroup> ret;
        if (tryCount < 12) {
            ret = suggestGroups();
            if (ret.size() > 0) {
                return ret;

            }
        } else {
            ret = new ArrayList<>(2);
        }

        switch (currierType) {
            case B:
                switch (tryCount % 3) {
                    case 0: {
                        ret.add(chooseIGroup());
                        ret.add(chooseDyGroup());
                        break;
                    }
                    case 1: {
                        ret.add(chooseDyGroup());
                        ret.add(chooseIGroup());
                        break;
                    }
                    default: {
                        ret.add(chooseOlGroup());
                        ret.add(chooseIGroup());
                        break;
                    }
                }
                break;
            case A:
                switch (tryCount % 3) {
                    case 0: {
                        ret.add(chooseIGroup());
                        ret.add(chooseOlGroup());
                        break;
                    }
                    case 1: {
                        ret.add(chooseOlGroup());
                        ret.add(chooseIGroup());
                        break;
                    }
                    default: {
                        ret.add(chooseDyGroup());
                        ret.add(chooseIGroup());
                        break;
                    }
                }
                break;
            default: {
                ret.add(chooseIGroup());
                ret.add(chooseOlGroup());
                break;
            }
        }
        return ret;
    }

    private GlyphGroup chooseOlGroup() {
        switch (suggestMethod) {
            case RANDOM: {
                if (groupOlListForPage.size() > 0) {
                    return chooseOneElementRandomly(groupOlListForPage);
                }
                return chooseOneElementRandomly(groupOlListForDoc);
            }
            case TOP_TOKEN: {
                if (usageOlMapForPage.size() >= 0) {
                    return chooseTopGroup(Constants.CHOL_GLYPH_GROUP, usageOlMapForPage);
                }
                return chooseTopGroup(Constants.CHOL_GLYPH_GROUP, usageOlMapForDoc);
            }
            case DAIIN_OL_CHEDY:
            default:
                return Constants.CHOL_GLYPH_GROUP;
        }
    }

    private GlyphGroup chooseDyGroup() {
        GlyphGroup defaultGroup = currierType.equals(SelfCitationTextGenerator.CURRIER.A) ? Constants.CHEODY_GLYPH_GROUP : Constants.CHEDY_GLYPH_GROUP;
        switch (suggestMethod) {
            case RANDOM: {
                if (groupDyListForPage.size() > 0) {
                    return chooseOneElementRandomly(groupDyListForPage);
                }
                return chooseOneElementRandomly(groupDyListForDoc);
            }
            case TOP_TOKEN: {
                if (usageDyMapForPage.size() >= 0) {
                    return chooseTopGroup(defaultGroup, usageDyMapForPage);
                }
                return chooseTopGroup(defaultGroup, usageDyMapForDoc);
            }
            case DAIIN_OL_CHEDY:
            default:
                return defaultGroup;
        }
    }

    private GlyphGroup chooseIGroup() {
        switch (suggestMethod) {
            case RANDOM: {
                if (groupIListForPage.size() > 0) {
                    return chooseOneElementRandomly(groupIListForPage);
                }
                return chooseOneElementRandomly(groupIListForDoc);
            }
            case TOP_TOKEN: {
                if (usageIMapForPage.size() >= 0) {
                    return chooseTopGroup(Constants.DAIIN_GLYPH_GROUP, usageIMapForPage);
                }
                return chooseTopGroup(Constants.DAIIN_GLYPH_GROUP, usageIMapForDoc);
            }
            case DAIIN_OL_CHEDY:
            default:
                return Constants.DAIIN_GLYPH_GROUP;
        }
    }

    private GlyphGroup chooseTopGroup(GlyphGroup defaultGroup, Map<GlyphGroup,Long> usageMap) {
        GlyphGroup maxGlyphGroup = defaultGroup;

        long maxCount = 0;
        Iterator<Map.Entry<GlyphGroup, Long>> it = usageMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<GlyphGroup, Long> entry = it.next();
            long count = entry.getValue().longValue();
            if (count > maxCount) {
                maxCount = count;
                maxGlyphGroup = entry.getKey();
            }
        }

        return maxGlyphGroup;
    }

    private GlyphGroup chooseOneElementRandomly(Set<GlyphGroup> glyphGroups) {
        ArrayList<GlyphGroup> arrayList = new ArrayList<>(glyphGroups.size());
        arrayList.addAll(glyphGroups);

        int rand = randomNumberGenerator.rand(glyphGroups.size());
        return arrayList.get(rand);
    }

    public int getSuggestionsProbability() {
        return suggestionsProbability;
    }

    public int getVmsGroupPercent() {
        return (int) ((float) 100 * (float) existsInVmsCount / (float) (existsNotInVmsCount + existsInVmsCount));
    }

    public String vmsCountAsString() {
        StringBuilder sb = new StringBuilder();
        int ratio = getVmsGroupPercent();
        sb.append("None vms token count: ").append(existsNotInVmsCount).append(" (" + (100 - ratio)+ " %)").append(Constants.ENTER);
        sb.append("VMS tokens count: ").append(existsInVmsCount).append(" (" + ratio + " %)").append(Constants.ENTER);

        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        float percentageI = getPercentageI();
        float percentageDy = getPercentageDy();
        float percentageOl = getPercentageOl();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        sb.append("Proportion of i-Types: ").append(nf.format(percentageI)).append(" %").append(Constants.ENTER);
        sb.append("Proportion of ol-Types: ").append(nf.format(percentageOl)).append(" %").append(Constants.ENTER);
        sb.append("Proportion of dy-Types: ").append(nf.format(percentageDy)).append(" %").append(Constants.ENTER);
        return sb.toString();
    }
}
