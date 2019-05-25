package de.voynich.text.util;

import de.voynich.text.SelfCitationTextGenerator;
import de.voynich.text.Constants;
import de.voynich.text.StatisticHelper;
import de.voynich.text.canfollow.*;
import de.voynich.text.morph.I_GroupMorpher;
import de.voynich.text.morph.SlimGroupMorpher;
import de.voynich.text.sourcechooser.PositionSourceGroupChooser;
import de.voynich.text.sourcechooser.I_SourceGroupChooser;
import de.voynich.text.sourcechooser.RandomSourceGroupChooser;
import de.voynich.text.sourcechooser.PageSourceGroupChooser;
import de.voynich.text.util.random.I_RandomNumberGenerator;
import de.voynich.text.util.random.PseudoRandomNumberGenerator;
import de.voynich.text.util.random.RealRandomNumberGenerator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * reads and stores the configuration parameters within conf.properties
 */
public class Config {

    private static final String CONFIG_FILE_NAME = "conf.properties";
    private static final Properties CONFIG;

    /** the length of a line */
    public static int maxLineLength = Constants.MAX_LINE_LENGTH_DEFAULT;

    /** the minimum line length */
    public static int minLineLength = Constants.MIN_LINE_LENGTH_DEFAULT;

    public static int methodReuseLastProbability = Constants.METHOD_REUSE_LAST_PROBABILITY_DEFAULT;

    public static int methodCombinedGroupsDismissAsSourceProbability = Constants.METHOD_DISMISS_COMBINED_GROUPS_AS_SOURCE_PROBABILITY;

    public static int methodSourceChooserSamePositionProbability = Constants.SAME_POSITION_PROBABILITY_DEFAULT;

    public int linesToCreate = Constants.LINES_TO_CREATE_DEFAULT;

    public int randomSeed = Constants.RANDOM_SEED_DEFAULT;

    public String[] initialLines = {Constants.SAMPLE_CURRIER_B_TEXT_LINE};

    /** the length of a page */
    public int pageLength;

    /** max repeats for a group */
    public int maxRepeatCount = Constants.MAX_REPEAT_COUNT_DEFAULT;

    ///
    private int methodCanFollowErrorRate = 0;

    public static boolean useWordFinalSubstitutions = true;


    private boolean save_properties_in_separate_file;
    public String save_path;
    private String saveDatePattern = Constants.SAVE_DATE_PATTERN_DEFAULT;
    private String filePattern = Constants.SAVE_FILENAME_PATTERN_DEFAULT;
    public static boolean saveGephiDictionary = false;
    public static boolean useVoynichSimilarities = false;

    /** to choose between pseudo RANDOM numbers and real random numbers */
    public I_RandomNumberGenerator randomNumberGenerator;

    private String methodRandomString = "pseudo";

    /** the statistic for the generated text */
    public StatisticHelper statistics;

    public I_SourceGroupChooser sourceGroupChooser;
    public I_GroupMorpher groupMorpher;

    public static I_CanFollow canFollow = new CurveLineCanFollow();

    private StatisticHelper.SUGGEST_METHOD suggestMethod;
    private int suggestionsProbability;

    public static float I_MIN_PERCENTAGE = Constants.I_MIN_PERCENTAGE_DEFAULT;
    public static float OL_MIN_PERCENTAGE = Constants.OL_MIN_PERCENTAGE_DEFAULT;
    public static float DY_MIN_PERCENTAGE = Constants.DY_MIN_PERCENTAGE_DEFAULT;

    static {
        String filename = CONFIG_FILE_NAME;
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("Java:comp:env");
            filename = (String) envContext.lookup("pathConfigurationFile");
        } catch (NamingException e) {
            // use default: conf.properties;
        }

        File file = new File(filename);
        Properties properties = null;
        if (file.exists()) {
            System.out.println("Reading properties from " + filename);
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println(filename + " not found. Using defaults.");
        }

        if (properties != null) {
            CONFIG = properties;
        } else {
            CONFIG = new PropertiesLoader().readPropertyFile(filename);
        }
    }

    public SelfCitationTextGenerator.CURRIER currierType = SelfCitationTextGenerator.CURRIER.A;


    private static String getProperty(String key) {
        String property = CONFIG.getProperty(key);
        if (property == null) {
            return "";
        }
        return property;
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    /**
     * Constructor
     */
    public Config() {
        readConfig();
    }

    /**
     * read configuration file
     */
    private void readConfig() {
        String linesToCreateString = Config.getProperty("text.lines_to_create");
        String maxRepeatCountString = Config.getProperty("text.max_repeat_count");
        String maxLineLengthString = Config.getProperty("text.max_line_length");
        String minLineLengthString = Config.getProperty("text.min_line_length");
        String linesPerPageString = Config.getProperty("text.lines_per_page");
        String initialSourceLineString = Config.getProperty("text.initial_line");

        methodRandomString = Config.getProperty("method.random");
        String randomSeedString = Config.getProperty("method.random.pseudo.seed");

        String methodCanFollowString = Config.getProperty("method.canFollow");
        String methodCanFollowErrorRateString = Config.getProperty("method.canFollow.error_rate");

        String methodSourceChooserString = Config.getProperty("method.sourceChooser");
        String methodSourceChooserSamePositionProbailityString = Config.getProperty("method.sourceChooser.same_position_probability");

        String methodMorphString = Config.getProperty("method.morph");
        String combineSplitProbabilityString = Config.getProperty("method.morph.combine_split.probability");
        String addRemoveProbabilityString = Config.getProperty("method.morph.add_remove.probability");
        String replaceProbabilityString = Config.getProperty("method.morph.replace.probability");

        String methodSuggestionsString = Config.getProperty("method.suggestions");
        String suggestionsProbabilityString = Config.getProperty("method.suggestions.probability");

        String methodSuggestionsITypeMinPercentageString = Config.getProperty("method.suggestions.iType_min_percentage");
        String methodSuggestionsOlTypeMinPercentageString = Config.getProperty("method.suggestions.olType_min_percentage");
        String methodSuggestionsDyTypeMinPercentageString = Config.getProperty("method.suggestions.dyType_min_percentage");

        String methodReuseLastProbabilityString = Config.getProperty("method.morph.reuse_last.probability");
        String useWordFinalSubstitutionsString = Config.getProperty("method.morph.use_word_final_substitutions");
        String methodCombinedGroupsDismissAsSourceProbabilityString = Config.getProperty("method.morph.combined.dismiss_as_source_probability");

        filePattern = Config.getProperty("save.filename");
        readFilename(filePattern);
        save_properties_in_separate_file = "true".equals(Config.getProperty("save.properties_in_seperate_file"));
        save_path = Config.getProperty("save.path");
        saveGephiDictionary = "true".equals(Config.getProperty("save.gephi.nodes"));
        useVoynichSimilarities = "true".equals(Config.getProperty("save.gephi.use_voynich_similarities"));

        if (!save_path.endsWith("" + Constants.SEPERATOR_CHAR)) {
            save_path = save_path + Constants.SEPERATOR_CHAR;
        }

        if (!StringUtil.isEmpty(initialSourceLineString)) {
            initialLines = StringUtil.splitAsArray(initialSourceLineString, Constants.NEW_LINE_SEPEARTOR);
            for (String line : initialLines) {
                if (line.contains("ed")) {
                    currierType = SelfCitationTextGenerator.CURRIER.B;
                }
            }
        }

        int combineSplitProbability = StringUtil.isParsableAsNumber(combineSplitProbabilityString) ? (int) Long.parseLong(combineSplitProbabilityString) : 15;
        int addRemoveProbability = StringUtil.isParsableAsNumber(addRemoveProbabilityString) ? (int) Long.parseLong(addRemoveProbabilityString) : 20;
        int replaceProbability = StringUtil.isParsableAsNumber(replaceProbabilityString) ? (int) Long.parseLong(replaceProbabilityString) : 65;
        int sum = combineSplitProbability + addRemoveProbability + replaceProbability;
        if (sum != 100) {
            System.out.println("WARN! method.morph.combine_split.probability + method.morph.add_remove.probability + method.morph.replace.probability != 100");
        }

        methodSourceChooserSamePositionProbability = StringUtil.isParsableAsNumber(methodSourceChooserSamePositionProbailityString) ? (int) Long.parseLong(methodSourceChooserSamePositionProbailityString) : Constants.METHOD_REUSE_LAST_PROBABILITY_DEFAULT;
        methodReuseLastProbability = StringUtil.isParsableAsNumber(methodReuseLastProbabilityString) ? (int) Long.parseLong(methodReuseLastProbabilityString) : Constants.METHOD_REUSE_LAST_PROBABILITY_DEFAULT;
        methodCombinedGroupsDismissAsSourceProbability =  StringUtil.isParsableAsNumber(methodCombinedGroupsDismissAsSourceProbabilityString) ? (int) Long.parseLong(methodCombinedGroupsDismissAsSourceProbabilityString) : Constants.METHOD_DISMISS_COMBINED_GROUPS_AS_SOURCE_PROBABILITY;

        maxLineLength = Math.max(40, StringUtil.isParsableAsNumber(maxLineLengthString) ? (int) Long.parseLong(maxLineLengthString) : Constants.MAX_LINE_LENGTH_DEFAULT);
        minLineLength = Math.max(5, StringUtil.isParsableAsNumber(minLineLengthString) ? (int) Long.parseLong(minLineLengthString) : Constants.MIN_LINE_LENGTH_DEFAULT);
        this.maxRepeatCount = Math.max(1, Math.min(5, StringUtil.isParsableAsNumber(maxRepeatCountString) ? (int) Long.parseLong(maxRepeatCountString) : Constants.MAX_REPEAT_COUNT_DEFAULT));
        this.linesToCreate = Math.max(1, StringUtil.isParsableAsNumber(linesToCreateString) ? (int) Long.parseLong(linesToCreateString) : Constants.LINES_TO_CREATE_DEFAULT);
        this.pageLength = Math.max(1, StringUtil.isParsableAsNumber(linesPerPageString) ? (int) Long.parseLong(linesPerPageString) : Constants.LINES_PER_PAGE_DEFAULT);
        this.methodCanFollowErrorRate = Math.max(0, Math.min(10, StringUtil.isParsableAsNumber(methodCanFollowErrorRateString) ? (int) Long.parseLong(methodCanFollowErrorRateString) : 0));
        this.suggestionsProbability = Math.max(0, Math.min(100, StringUtil.isParsableAsNumber(suggestionsProbabilityString) ? (int) Long.parseLong(suggestionsProbabilityString) : Constants.SUGGESTIONS_PROBABILITY_DEFAULT));

        this.randomSeed = StringUtil.isParsableAsNumber(randomSeedString) ? (int) Long.parseLong(randomSeedString) : Constants.RANDOM_SEED_DEFAULT;
        useWordFinalSubstitutions = !("false".equals(useWordFinalSubstitutionsString));

        if (StringUtil.isParsableAsNumber(methodSuggestionsOlTypeMinPercentageString)) {
            int number = (int) Long.parseLong(methodSuggestionsOlTypeMinPercentageString);
            OL_MIN_PERCENTAGE = (float) number / (float) 100.0;
        }
        if (StringUtil.isParsableAsNumber(methodSuggestionsDyTypeMinPercentageString)) {
            int number = (int) Long.parseLong(methodSuggestionsDyTypeMinPercentageString);
            DY_MIN_PERCENTAGE = (float) number / (float) 100.0;
        }
        if (StringUtil.isParsableAsNumber(methodSuggestionsITypeMinPercentageString)) {
            int number = (int) Long.parseLong(methodSuggestionsITypeMinPercentageString);
            I_MIN_PERCENTAGE = (float) number / (float) 100.0;
        }

        switch (methodCanFollowString.toLowerCase()) {
            case "none": {
                canFollow = new DummyCanFollow();
                break;
            }
            case "statistic":
            case "percent": {
                canFollow = new StatisticCanFollow();
                break;
            }
            case "vms":
            case "voynich": {
                canFollow = new VoynichCanFollow();
                break;
            }
            case "curveline":
            default: {
                canFollow = new CurveLineCanFollow();
                break;
            }
        }

        switch (methodSuggestionsString.toLowerCase()) {
            case "daiin": {
                suggestMethod = StatisticHelper.SUGGEST_METHOD.DAIIN_OL_CHEDY;
                break;
            }
            case "random": {
                suggestMethod = StatisticHelper.SUGGEST_METHOD.RANDOM;
                break;
            }
            case "none": {
                suggestMethod = StatisticHelper.SUGGEST_METHOD.NONE;
                break;
            }
            case "top":
            default: {
                suggestMethod = StatisticHelper.SUGGEST_METHOD.TOP_TOKEN;
                break;
            }
        }

        switch (methodSourceChooserString.toLowerCase()) {
            case "random": {
                sourceGroupChooser = new RandomSourceGroupChooser(randomNumberGenerator);
                break;
            }
            case "position": {
                sourceGroupChooser = new PositionSourceGroupChooser(randomNumberGenerator, currierType);
                break;
            }
            case "page":
            default: {
                sourceGroupChooser= new PageSourceGroupChooser(randomNumberGenerator, currierType);
                break;
            }
        }

        switch (methodMorphString.toLowerCase()) {
            case "slim":
            default: {
                groupMorpher = new SlimGroupMorpher(currierType, canFollow, methodCanFollowErrorRate);
                groupMorpher.setProbabilities(addRemoveProbability, combineSplitProbability);
                break;
            }
        }
    }

    /**
     * reset statistics to its initiak state
     */
    public void init() {
        // init random number generator
        switch (methodRandomString.toLowerCase()) {
            case "real": {
                randomNumberGenerator = new RealRandomNumberGenerator();
                break;
            }
            case "pseudo":
            default: {
                randomNumberGenerator = new PseudoRandomNumberGenerator(randomSeed);
                break;
            }
        }

        // init statictics
        this.statistics = new StatisticHelper(currierType, randomNumberGenerator, canFollow, suggestMethod, suggestionsProbability);

        if (canFollow instanceof StatisticCanFollow) {
            ((StatisticCanFollow) canFollow).setRandomNumberGenerator(randomNumberGenerator);
        }
        sourceGroupChooser.setRandomNumberGenerator(randomNumberGenerator);
        groupMorpher.setRandomNumberGenerator(randomNumberGenerator);
    }

    /**
     * @return the actual configuration as string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("text.lines_to_create=").append(this.linesToCreate).append(Constants.ENTER);
        sb.append("text.max_repeat_count=").append(this.maxRepeatCount).append(Constants.ENTER);
        sb.append("text.max_line_length=").append(maxLineLength).append(Constants.ENTER);
        sb.append("text.min_line_length=").append(minLineLength).append(Constants.ENTER);
        sb.append("text.lines_per_page=").append(this.pageLength).append(Constants.ENTER);
        sb.append("text.initial_line=").append(initialLines[0]).append(Constants.ENTER);

        if (randomNumberGenerator instanceof RealRandomNumberGenerator) {
            sb.append("method.random=real").append(Constants.ENTER);
        } else {
            sb.append("method.random=pseudo").append(Constants.ENTER);
            sb.append("method.random.pseudo.seed=").append(randomSeed).append(Constants.ENTER);
        }

        if (canFollow instanceof DummyCanFollow) {
            sb.append("method.canFollow=none").append(Constants.ENTER);
        } else if (canFollow instanceof VoynichCanFollow) {
            sb.append("method.canFollow=voynich").append(Constants.ENTER);
        } else if (canFollow instanceof StatisticCanFollow) {
            sb.append("method.canFollow=statistic").append(Constants.ENTER);
        } else if (canFollow instanceof CurveLineCanFollow) {
            sb.append("method.canFollow=curveline").append(Constants.ENTER);
        } else {
            throw new IllegalStateException();
        }
        sb.append("method.canFollow.error_rate=").append(methodCanFollowErrorRate).append(Constants.ENTER);
        sb.append("method.morph.use_word_final_substitutions=").append(useWordFinalSubstitutions).append(Constants.ENTER);

        switch (statistics.suggestMethod) {
            case DAIIN_OL_CHEDY: {
                sb.append("method.suggestions=daiin").append(Constants.ENTER);
                break;
            }
            case RANDOM: {
                sb.append("method.suggestions=random").append(Constants.ENTER);
                break;
            }
            case NONE: {
                sb.append("method.suggestions=none").append(Constants.ENTER);
                break;
            }
            case TOP_TOKEN: {
                sb.append("method.suggestions=top").append(Constants.ENTER);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }

        sb.append("method.suggestions.probability=").append(statistics.suggestionsProbability).append(Constants.ENTER);

        sb.append("method.suggestions.iType_min_percentage=").append((int) (I_MIN_PERCENTAGE  * 100)).append(Constants.ENTER);
        sb.append("method.suggestions.olType_min_percentage=").append((int) (OL_MIN_PERCENTAGE * 100)).append(Constants.ENTER);
        sb.append("method.suggestions.dyType_min_percentage=").append((int) (DY_MIN_PERCENTAGE * 100)).append(Constants.ENTER);

        if (sourceGroupChooser instanceof RandomSourceGroupChooser) {
            sb.append("method.sourceChooser=random").append(Constants.ENTER);
        } else if (sourceGroupChooser instanceof PositionSourceGroupChooser) {
            sb.append("method.sourceChooser=position").append(Constants.ENTER);
        } else if (sourceGroupChooser instanceof PageSourceGroupChooser) {
            sb.append("method.sourceChooser=page").append(Constants.ENTER);
        } else {
            throw new IllegalStateException();
        }

        if (groupMorpher instanceof SlimGroupMorpher) {
            sb.append("method.morph=slim").append(Constants.ENTER);
        } else {
            throw new IllegalStateException();
        }

        sb.append("method.morph.combine_split.probability=").append(groupMorpher.getCombineSplitPropability()).append(Constants.ENTER);
        sb.append("method.morph.add_remove.probability=").append(groupMorpher.getAddRemovePropability()).append(Constants.ENTER);
        sb.append("method.morph.replace.probability=").append(groupMorpher.getReplacePropability()).append(Constants.ENTER);

        sb.append("method.morph.reuse_last.probability=").append(methodReuseLastProbability).append(Constants.ENTER);

        return sb.toString();
    }

    /**
     * parse the file name for the date pattern
     * @param pattern
     * @return
     */
    private void readFilename(String pattern) {
        int pos1 = pattern.indexOf("{");
        int pos2 = pattern.indexOf("}", pos1);

        if (pos1 >= 0 && pos2 > pos1) {
            saveDatePattern = pattern.substring(pos1+1, pos2);
        }
    }

    /**
     * store the generated text into a file
     * @param generatedText
     */
    public void saveGeneratedText(List<String> generatedText) {

        System.out.print(statistics.vmsCountAsString());

        StringBuilder sb = new StringBuilder();
        sb.append("Properties").append(Constants.ENTER);
        sb.append("----------").append(Constants.ENTER);
        sb.append(this.toString());
        sb.append(Constants.ENTER);
        sb.append("Statistics").append(Constants.ENTER);
        sb.append("-----").append(Constants.ENTER);
        sb.append("Line count: ").append(generatedText.size()).append(Constants.ENTER);
        sb.append(statistics.vmsCountAsString());
        sb.append(statistics.toString()).append(Constants.ENTER);

        // build filename
        String fileName = StringUtil.substitute(filePattern, "{" + saveDatePattern + "}", StringUtil.dateAsString(saveDatePattern));
        if (save_properties_in_separate_file) {
            String fileBaseName = FileTools.getBaseName(fileName);
            String fileNodeName = save_path + fileBaseName + ".properties.txt";
            if (FileTools.saveFile(fileNodeName, sb.toString())) {
                System.out.println("File " + fileNodeName + " saved.");
                sb.setLength(0);
            }
        } else {
            String propertyString = sb.toString();
            sb.setLength(0);
            sb.append("#");
            sb.append(StringUtil.substitute(propertyString, Constants.ENTER, Constants.ENTER + "#"));
            sb.append(Constants.ENTER);
        }

        for (String line : generatedText) {
            sb.append(line).append(Constants.ENTER);
        }
        FileTools.saveFile(save_path + fileName, sb.toString());
        System.out.println("File " + save_path + fileName + " saved.");

        String fileBaseName = FileTools.getBaseName(fileName);
        if (saveGephiDictionary) {
            Map<String, GephiNode> nodeMap = GephiNode.convertGephiNode(statistics.allGroupMap);
            String fileNodeName = save_path + fileBaseName + "_nodes.csv";
            if (FileTools.saveFile(fileNodeName, GephiNode.calcType(nodeMap))) {
                System.out.println("File " + fileNodeName + " saved.");
            }
            String fileEdgeName = save_path + fileBaseName + "_edges.csv";
            if (FileTools.saveFile(fileEdgeName, GephiNode.calcSimilarities(nodeMap))) {
                System.out.println("File " + fileEdgeName + " saved.");
            }
        }
    }
}
