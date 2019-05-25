## ﻿<p align='center'>Configuration in conf.properties</p>

The file [conf.properties](executable/conf.properties) contains the following configuration settings:

### <p align='center'>text.* properties</p>

General properties to initialize the text generator.

| property              | possible values  | default        | purpose     |
|:--------------------- |:----------------:|:--------------:|:----------- |
| text.lines_to_create  | [1...]           | 1200           | the number of lines to create |
| text.max_line_length  | [40...]          | 55             | the number of characters in a line |
| text.min_line_length  | [5...{text.max_line_length}] | 15 | the minimum number of characters for the last line of a paragraph |
| text.lines_per_page   | [1...]           | 29             | the number of lines for a page |
| text.max_repeat_count | [0...5]          | 3              | the number of times a token can occur repeated |
| text.initial_line     | VMS-text         | line f103v.P.9 | the seed for initializing the text generator |

### <p align='center'>method.random.* properties</p>

Properties to initialize the random number generator.

| property                  | possible values | default | purpose     |
|:------------------------- |:---------------:|:-------:|:----------- |
| method.random             | [pseudo, real]  | pseudo  | choose implementation of the random number generator |
| method.random.pseudo.seed | [0...]          | 19      | seed to initialize the pseudo random number generator |

### <p align='center'>method.canFollow.* properties</p>

Properties to control which glyphs can follow each other.

| property                    | possible values  | default       | purpose     |
|:--------------------------- |:----------------:|:-------------:|:----------- |
| method.canFollow            | [curveline, statistic, voynich, none] | curveline | choose implementation to determine which tokens can follow after each other |
| method.canFollow.error_rate | [0...10]         | 0             | an error rate for allowing weird types groups like &lt;doiin&gt; |

### <p align='center'>method.sourceChooser.* properties</p>

Properties to control the behavior for choosing source tokens.

| property             | possible values  | default   | purpose     |
|:-------------------- |:----------------:|:---------:|:----------- |
| method.sourceChooser | [page, position, random] | page | choose implementation for choosing the source tokens |
| method.sourceChooser.same_position_probability | [0...100] | 28 | probability to choose a token in writing position from a previous line |

### <p align='center'>method.morph.* properties</p>

Properties to control the modification of source tokens.

| property                               | possible values  | default  | purpose     |
|:-------------------------------------- |:----------------:|:--------:|:----------- |
| method.morph.add_remove.probability    | [0...80]         | 20       | the probability to use add & remove to morph the source token |
| method.morph.combine_split.probability | [0...80]         | 30       | the probability to use combine & split to morph the source token |
| method.morph.replace.probability       | [20...100]       | 50       | the probability to use replace to morph the source token |
| method.morph.reuse_last.probability    | [0...20]         | 10       | the probability to use the last generated token as source |
| method.morph.combined.dismiss_as_source_probability | [0...100] | 30 | the probability that combined tokens are dismissed as source |
| method.morph.use_word_final_substitutions | [true, false] | true     | use word final substitutions like &lt;chor&gt; into &lt;chy&gt; |

### <p align='center'>method.suggestions.* properties</p>

Properties to control suggestions. Suggestions are used to ensure that all types of tokens occur in the generated text.

| property                                 | possible values | default        | purpose     |
|:---------------------------------------- |:---------------:|:--------------:|:----------- |
| method.suggestions                       | [top, daiin, random, none] | top | suggestions are used if the number of &lt;-in&gt;, &lt;-ol&gt; or &lt;-dy&gt; tokens is to low |
| method.suggestions.probability           | [0...100]       | 40             | the probability to use suggestions. If it is set to zero no suggestions are used |
| method.suggestions.iType_min_percentage  | [0...100]       | 20             | min percentage of tokens using a &lt;i&gt;-glyph like in &lt;daiin&gt; |
| method.suggestions.olType_min_percentage | [0...100]       | 25             | min percentage of tokens using a sequence &lt;ol&gt; like in &lt;chol&gt; |
| method.suggestions.dyType_min_percentage | [0...100]       | 25             | min percentage of tokens using a sequence &lt;dy&gt; like in &lt;chedy&gt; |

### <p align='center'>save.* properties</p>

Properties to control the output files.

| property                            | possible values | default            | purpose     |
|:----------------------------------- |:---------------:|:------------------:|:----------- |
| save.path                           | Text            | ./generate/        | path to store the files with generated text |
| save.filename                       | Text            | generated_text.txt | name pattern for the generated files |
| save.properties_in_seperate_file    | [true, false]   | false              | determines if the properties are stored in a separate file |
| save.gephi.nodes                    | [true, false]   | false              | save nodes & edges for gephi as {save.filename}_nodes.cvs |
| save.gephi.use_voynich_similarities | [true, false]   | false              | define elements like &lt;ch&gt; and &lt;sh&gt; as similar |
