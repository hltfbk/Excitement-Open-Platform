The distributional similarity tool: an open source for generation of distributional similarity models for a given corpus and methods

Author: Meni Adler, The Excitement Project

Versrion: 1.1

Date: 20/9/2013

Licence: GNU Lesser General Public License (http://www.gnu.org/copyleft/lesser.html)

Based on JDK 1.7

Contact: meni.adler@gmail.com

-------------------------------------------------

This directory contains an open source code and a system for generationg distributional similarity models for a given corpus.

1. Documentation:

The 'doc' directory contains the following documentations:
- dist-sim-guide.pdf: a general overview on distributional similarity and a user guide for the programming module and the system, see: 
- JavaDoc: a documentation of the system API.

2. Demo

The 'demo' directoy cotains a tiny compiled corpora for English and German (each of 1000 sentences) for testing, and a set of running configuration with application scripts.

2.1 Building distributional similarity model files 

You can test these configurations in order to build various kinds of distributional modules (the whole process can be applied on German by selecting the equivalent configuration dirs, with the '-ger' extension):

>configurations/lin/build-model configurations/lin/proximity/

>configurations/lin/build-model configurations/lin/dependency/

>configurations/bap/build-model configurations/bap/

>configurations/dirt/build-model configurations/dirt/

The generated model files will be stored at the 'models' directory.

Each of the lexical models (=lin and bap) s now stored in two Redis dbs, at redis/db directory, one for left-to-right similarities, and the other right-to-left similarities. 
The Syntactic model (=dirt) is represented by one Redis DB,  at redis/db/dirt directory, for left-2-right similarities. 

3. Usage of the models


The lexical models (e.g., lin and bap) can be accessed by the eu.excitementproject.eop.distsim.storage.SimilarityStorageBasedLexicalResource class, which implements the LexicalResource interface.
You can test your lexical models by applying the eu.excitementproject.eop.distsim.resource.TestLemmaPosSimilarity program, with the appropriate  configuration file (the relevant redis db should be up):

>java -cp distsim.jar eu.excitementproject.eop.distsim.resource.TestLemmaPosSimilarity configurations/lin/proximity/knowledge-resource.xml

>java -cp distsim.jar eu.excitementproject.eop.distsim.resource.TestLemmaPosSimilarity configurations/lin/dependency/knowledge-resource.xml

>java -cp distsim.jar eu.excitementproject.eop.distsim.resource.TestLemmaPosSimilarity configurations/bap/knowledge-resource.xml

The dirt model can be accessed by the eu.excitementproject.eop.distsim.storage.SimilarityStorageBasedDIRTSyntacticResource class, which implements the SyntacticResource interface.
You can test your dirt model by applying the eu.excitementproject.eop.core.component.syntacticknowledge.TestDIRTSimilarity program, with the appropriate  configuration file (the relevant redis db should be up):
Note, that this program should be with the EOP's core class path
>java -cp ... eu.excitementproject.eop.core.component.syntacticknowledge.TestDIRTSimilarity configurations/dirt/knowledge-resource.xml

4. Scaling to larger corpus

The current version is based on memory. For the case of English, lexical models based on the huge UkWAC corpus, and DIRT model based on the two CDs of Reuters, were generated with 64G RAM. For the case of German and Italian, 32G RAM seems to be sufficient.
When moving to larger corpus, the memory for the Java programs (in the build-model scripts) should be increased accordingly. In addition, the number of threads should be set according to the system hardware abilities.
In case, you have a memory problem, there is an option to apply a memory-free map reduce program, by commenting the first two command lines in each build-model (GeneralCooccurrenceExtractor, GeneralElementFeatureExtractor) and uncommenting the third command line (ExtractAndCountBasicNodeBasedElementsFeatures/ExtractAndCountBasicNodeBasedDirtElementsFeatures). Contact me for more details.
