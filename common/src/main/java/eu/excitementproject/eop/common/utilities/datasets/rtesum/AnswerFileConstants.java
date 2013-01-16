package eu.excitementproject.eop.common.utilities.datasets.rtesum;

/**
 * Constants used for IO of Answers file.
 * The answers file is the file that should be submitted for
 * RTE6 main and novelty task.
 * 
 * @author Asher Stern
 *
 */
public class AnswerFileConstants
{
	public static final String ROOT_ELEMENT_NAME = "entailment_corpus";
	public static final String TOPIC_ELEMENT_NAME = "TOPIC";
	public static final String TOPIC_ID_ATTRIBUTE_NAME = "t_id";
	public static final String HYPOTHESIS_ELEMENT_NAME = "H";
	public static final String HYPOTHESIS_ID_ATTRIBUTE_NAME = "h_id";
	public static final String HYPOTHESIS_SENTENCE_ELEMENT_NAME = "H_sentence";
	public static final String TEXT_SENTENCE_ELEMENT_NAME = "text";
	public static final String TEXT_SENTENCE_DOCUMENT_ID_ATTRIBUTE_NAME = "doc_id";
	public static final String TEXT_SENTENCE_SENTENCE_ID_ATTRIBUTE_NAME = "s_id";
	public static final String TEXT_SENTENCE_EVALUATION_ATTRIBUTE_NAME = "evaluation";
	public static final String TEXT_SENTENCE_EVALUATION_YES_VALUE = "YES";
}

