package eu.excitementproject.eop.lap.dkpro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.eval.FMeasure;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.BigramNameFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;
import opennlp.tools.util.featuregen.OutcomePriorFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;

/**
 * This class incomplete, and it is a temporary try of using OpenNLP NER for German.
 * 
 * @author Rui
 *
 */
public class OpenNLPNERwithDistSimDE extends LAP_ImplBase implements LAPAccess {

	static Logger logger = Logger.getLogger(OpenNLPNERwithDistSimDE.class
			.getName());

	private static final String START_SYMBOL = "#START";
	private static final String END_SYMBOL = "#END";

	private static final String MODEL_PATH = "./target/OpenNLPNERModel";

	public static String getModelPath() {
		return MODEL_PATH;
	}

	TokenNameFinderModel model;

	private Map<String, String> POSMap;

	private static final String TRAIN_FILE = "./src/main/resources/DE_NER_distr_cluster/train-data/deu.train";

	public static String getTrainFile() {
		return TRAIN_FILE;
	}

	private static final String TEST_FILE = "./src/main/resources/DE_NER_distr_cluster/train-data/deu.testa";
//	private static final String TEST_FILE = "./src/main/resources/DE_NER_distr_cluster/train-data/deu.testb";

	public static String getTestFile() {
		return TEST_FILE;
	}

	private Map<String, String> lexicon;
	private static final String LEXICON_PATH = "./src/main/resources/DE_NER_distr_cluster/distr_clusters/hgc2.400.clusters.clean";
//	private static final String LEXICON_PATH = "./src/main/resources/DE_NER_distr_cluster/distr_clusters/hgc2_full_600";

	public static String getLexiconPath() {
		return LEXICON_PATH;
	}

	private static final String ENCODING = "ISO-8859-1";
	private static final String UNKNOWN_WORD_CLUSTER = "null";

	public OpenNLPNERwithDistSimDE() throws LAPException {
		super();
		languageIdentifier = "DE"; // set languageIdentifer

		// load in the word cluster based on distributional similarity
		lexicon = new HashMap<String, String>();
		BufferedReader input;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(LEXICON_PATH), "UTF-8"));
			String line = "";
			while ((line = input.readLine()) != null) {
				String[] items = line.split("\\s+");
				// if the words are all capitalized, add the "toUpperCase()" in
				// the later feature functions!
				lexicon.put(items[0], items[1]);
			}
			input.close();
		} catch (IOException e) {
			throw new LAPException(e.getMessage());
		}
	}

	@Override
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException {
		initialize();
		// TODO: process
	}

	private void initialize() throws LAPException {
		File modelFile = new File(MODEL_PATH);
		if (!modelFile.exists()) {
			// needs training
			throw new LAPException("Please train and specify the model!");
		}

		// load in the model
		try {
			InputStream modelStream = new FileInputStream(MODEL_PATH);
			model = new TokenNameFinderModel(modelStream);
			modelStream.close();

		} catch (IOException e) {
			throw new LAPException(e.getMessage());
		}
	}

	public void test() throws LAPException, IOException {
		initialize();

		NameFinderME nameFinder = new NameFinderME(model);
		// String[] sentence = new String[] { "Mike", "Smith", "is", "a",
		// "good",
		// "person" };
		preprocess(TEST_FILE, TEST_FILE + ".pp");

		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(TEST_FILE + ".pp"), ENCODING));
		String line = "";
		while ((line = input.readLine()) != null) {

			Span nameSpans[] = nameFinder.find(line.split(" "));

			System.out.println(line);
			for (Span s : nameSpans)
				System.out.println(s.toString());
		}
		input.close();

		// evaluation
		TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(
				new NameFinderME(model));
		NameSampleDataStream sampleStream = new NameSampleDataStream(
				new PlainTextByLineStream(new InputStreamReader(
						new FileInputStream(TEST_FILE + ".pp"), ENCODING)));
		evaluator.evaluate(sampleStream);
		FMeasure result = evaluator.getFMeasure();
		System.out.println(result.toString());
	}

	private void train() throws IOException {
		File trainFile = new File(TRAIN_FILE);
		if (!trainFile.exists()) {
			throw new IOException("Please specify the training data!");
		}

		// preprocess the data
		preprocess(TRAIN_FILE, TRAIN_FILE + ".pp");

		// read in the data and train the model
		NameSampleDataStream sampleStream = new NameSampleDataStream(
				new PlainTextByLineStream(new InputStreamReader(
						new FileInputStream(TRAIN_FILE + ".pp"), ENCODING)));

		int iterations = 100;
		int cutoff = 5;
		model = NameFinderME.train(languageIdentifier, "default", sampleStream,
				createFeatureGenerator(), new HashMap<String, Object>(),
				iterations, cutoff);
		model.serialize(new FileOutputStream(MODEL_PATH));
	}

	private void preprocess(String inputFile, String outputFile)
			throws IOException {
		// store the POS information, and put it in a HashMap
		POSMap = new HashMap<String, String>();

		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile), ENCODING));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), ENCODING));
		List<String> tokenList = new ArrayList<String>();
		String line = "";
		while ((line = input.readLine()) != null) {
			if (line.trim().length() == 0 && tokenList.size() != 0) {
				// one sentence already in buffer
				String prevTag = "";
				for (int i = 0; i < tokenList.size(); i++) {
					String word = tokenList.get(i).split(" ")[0];
					String tag = tokenList.get(i).split(" ")[4];
					if (tag.equals("I-MISC")) {
						tag = "O";
					}
					if (!tag.equals("O")) {
						if (!tag.equals(prevTag)) {
							if (!prevTag.equals("O") && i != 0) {
								output.write("<END> ");
							}
							if (tag.contains("PER")) {
								output.write("<START:person> ");
							} else if (tag.contains("LOC")) {
								output.write("<START:location> ");
							} else if (tag.contains("ORG")) {
								output.write("<START:organization> ");
							} else {
								output.write("<START:" + tag + "> ");
							}
						}
					} else {
						if (!prevTag.equals("O") && i != 0) {
							output.write("<END> ");
						}
					}
					output.write(word);
					output.write(" ");
					prevTag = tag;

					// put POS information into POSMap
					String prev, next;
					if (i == 0) {
						prev = START_SYMBOL;
					} else {
						prev = tokenList.get(i - 1).split(" ")[0];
					}
					if (i == tokenList.size() - 1) {
						next = END_SYMBOL;
					} else {
						next = tokenList.get(i + 1).split(" ")[0];
					}
					POSMap.put(prev + "_" + word + "_" + next, tokenList.get(i)
							.split(" ")[2]);
				}
				output.newLine();
				tokenList.clear();
			} else {
				tokenList.add(line.trim());
			}
		}
		input.close();
		output.close();
	}

	private AdaptiveFeatureGenerator createFeatureGenerator()
			throws IOException {
		return new CachedFeatureGenerator(new AdaptiveFeatureGenerator[] {
				new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2),
				new WindowFeatureGenerator(
						new TokenClassFeatureGenerator(true), 2, 2),
				new OutcomePriorFeatureGenerator(),
				new PreviousMapFeatureGenerator(),
				new BigramNameFeatureGenerator(),
				
				// newly added: word cluster features
				new WindowFeatureGenerator(new TokenClusterFeatureGenerator(
						true), 2, 2),
//				// newly added: word cluster features for the previous map
//				new PreviousWClMapFeatureGenerator(),
//				// newly added: bigram word cluster features
//				new BigramWClNameFeatureGenerator(),
				
				// newly added: POS features
//				new WindowFeatureGenerator(new TokenPOSFeatureGenerator(true),
//						2, 2),
//				// newly added: POS features for the previous map
//				new PreviousWPOSMapFeatureGenerator(),
				// newly added: bigram POS features
//				new BigramWPOSNameFeatureGenerator(),
				
				new SentenceFeatureGenerator(true, false)
				});
	}

	/**
	 * Generates features for different word clusters.
	 */
	public class TokenClusterFeatureGenerator extends FeatureGeneratorAdapter {

		private static final String TOKEN_CLUSTER_PREFIX = "wcl";
		private static final String TOKEN_AND_CLUSTER_PREFIX = "w&cl";

		private boolean generateWordAndClusterFeature;

		public TokenClusterFeatureGenerator() throws IOException {
			this(false);
		}

		public TokenClusterFeatureGenerator(
				boolean genearteWordAndClusterFeature) throws IOException {
			this.generateWordAndClusterFeature = genearteWordAndClusterFeature;
		}

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] preds) {
			String word = tokens[index];
			String wordCluster = lexicon.get(word);
			if (null == wordCluster) {
				wordCluster = UNKNOWN_WORD_CLUSTER;
			}
			features.add(TOKEN_CLUSTER_PREFIX + "=" + wordCluster);

			if (generateWordAndClusterFeature) {
				features.add(TOKEN_AND_CLUSTER_PREFIX + "=" + word + ","
						+ wordCluster);
			}
		}
	}

	/**
	 * Generates features for different POSes.
	 */
	public class TokenPOSFeatureGenerator extends FeatureGeneratorAdapter {

		private static final String TOKEN_POS_PREFIX = "wpos";
		private static final String TOKEN_AND_POS_PREFIX = "w&pos";

		private boolean generateWordAndPOSFeature;

		public TokenPOSFeatureGenerator() throws IOException {
			this(false);
		}

		public TokenPOSFeatureGenerator(boolean generateWordAndPOSFeature)
				throws IOException {
			this.generateWordAndPOSFeature = generateWordAndPOSFeature;
		}

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] preds) {
			String prev, next;
			if (index == 0) {
				prev = START_SYMBOL;
			} else {
				prev = tokens[index - 1];
			}
			if (index == tokens.length - 1) {
				next = END_SYMBOL;
			} else {
				next = tokens[index + 1];
			}
			String word = tokens[index];
			String wordPOS = POSMap.get(prev + "_" + word + "_" + next);
			features.add(TOKEN_POS_PREFIX + "=" + wordPOS);

			if (generateWordAndPOSFeature) {
				features.add(TOKEN_AND_POS_PREFIX + "=" + word + "," + wordPOS);
			}
		}
	}

	/**
	 * This {@link FeatureGeneratorAdapter} generates features indicating the
	 * outcome associated with a previously occuring word cluster.
	 */
	public class PreviousWClMapFeatureGenerator implements
			AdaptiveFeatureGenerator {

		private Map<String, String> previousMap = new HashMap<String, String>();

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] preds) {
			features.add("pdcl=" + previousMap.get(lexicon.get(tokens[index])));
		}

		/**
		 * Generates previous decision features for the token based on contents
		 * of the previous map.
		 */
		public void updateAdaptiveData(String[] tokens, String[] outcomes) {

			for (int i = 0; i < tokens.length; i++) {
				previousMap.put(lexicon.get(tokens[i]), outcomes[i]);
			}
		}

		/**
		 * Clears the previous map.
		 */
		public void clearAdaptiveData() {
			previousMap.clear();
		}
	}

	/**
	 * This {@link FeatureGeneratorAdapter} generates features indicating the
	 * outcome associated with a previously occuring POS.
	 */
	public class PreviousWPOSMapFeatureGenerator implements
			AdaptiveFeatureGenerator {

		private Map<String, String> previousMap = new HashMap<String, String>();

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] preds) {
			String prev, next;
			if (index == 0) {
				prev = START_SYMBOL;
			} else {
				prev = tokens[index - 1];
			}
			if (index == tokens.length - 1) {
				next = END_SYMBOL;
			} else {
				next = tokens[index + 1];
			}
			String word = tokens[index];
			String wordPOS = POSMap.get(prev + "_" + word + "_" + next);
			features.add("pdpos=" + previousMap.get(wordPOS));
		}

		/**
		 * Generates previous decision features for the token based on contents
		 * of the previous map.
		 */
		public void updateAdaptiveData(String[] tokens, String[] outcomes) {

			for (int i = 0; i < tokens.length; i++) {
				String prev, next;
				if (i == 0) {
					prev = START_SYMBOL;
				} else {
					prev = tokens[i - 1];
				}
				if (i == tokens.length - 1) {
					next = END_SYMBOL;
				} else {
					next = tokens[i + 1];
				}
				String word = tokens[i];
				String wordPOS = POSMap.get(prev + "_" + word + "_" + next);
				previousMap.put(wordPOS, outcomes[i]);
			}
		}

		/**
		 * Clears the previous map.
		 */
		public void clearAdaptiveData() {
			previousMap.clear();
		}
	}

	public class BigramWClNameFeatureGenerator extends FeatureGeneratorAdapter {

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] previousOutcomes) {
			String wcl = lexicon.get(tokens[index]);
			// bi-gram features
			if (index > 0) {
				// features.add("pw,w=" + tokens[index - 1] + "," +
				// tokens[index]);
				String pwcl = lexicon.get(tokens[index - 1]);
				features.add("pwcl,wcl=" + pwcl + "," + wcl);
			}
			if (index + 1 < tokens.length) {
				// features.add("w,nw=" + tokens[index] + "," + tokens[index +
				// 1]);
				String nwcl = lexicon.get(tokens[index + 1]);
				features.add("wcl,ncl=" + wcl + "," + nwcl);
			}
		}
	}

	public class BigramWPOSNameFeatureGenerator extends FeatureGeneratorAdapter {

		public void createFeatures(List<String> features, String[] tokens,
				int index, String[] previousOutcomes) {
			// bi-gram features
			String wpos = getPOS(tokens, index);
			if (index > 0) {
				// features.add("pw,w=" + tokens[index - 1] + "," +
				// tokens[index]);
				String pwcl = getPOS(tokens, index - 1);
				features.add("pwcpos,wcpos=" + pwcl + "," + wpos);
			}
			if (index + 1 < tokens.length) {
				// features.add("w,nw=" + tokens[index] + "," + tokens[index +
				// 1]);
				String nwcl = getPOS(tokens, index + 1);
				features.add("wcpos,ncpos=" + wpos + "," + nwcl);
			}
		}

		private String getPOS(String[] tokens, int index) {
			String prev, next;
			if (index == 0) {
				prev = START_SYMBOL;
			} else {
				prev = tokens[index - 1];
			}
			if (index == tokens.length - 1) {
				next = END_SYMBOL;
			} else {
				next = tokens[index + 1];
			}
			String word = tokens[index];
			String wordPOS = POSMap.get(prev + "_" + word + "_" + next);
			return wordPOS;
		}
	}

	public static void main(String[] args) throws LAPException, IOException {
		OpenNLPNERwithDistSimDE ner = new OpenNLPNERwithDistSimDE();
		ner.train();
		ner.test();
	}

}
