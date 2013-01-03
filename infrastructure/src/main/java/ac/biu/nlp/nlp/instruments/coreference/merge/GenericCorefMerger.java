package ac.biu.nlp.nlp.instruments.coreference.merge;

import java.util.List;

import ac.biu.nlp.nlp.general.IteratorByMultipleIterables;
import ac.biu.nlp.nlp.general.match.MatchFinder;
import ac.biu.nlp.nlp.general.match.Matcher;
import ac.biu.nlp.nlp.general.match.Operator;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.coreference.merge.english.EnglishCorefMerger;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


/**
 * An helper class for merging the output of a parser, with the
 * output of a co-reference system.
 * <B>Don't use this class directly</B>. This class implements only part of what needed
 * in order to make the merging. It is given as input not the trees themselves, but
 * list of list of nodes. In addition, it is given the {@link MatchFinder} and
 * the {@link Operator} in its constructor.
 * In order to use this class you should implement a mechanism that converts list
 * of trees to a list of lists of nodes, and gives actual {@link MatchFinder} and
 * {@link Operator}.
 * This is done by {@link EnglishCorefMerger}, for {@link BasicNode}.
 * 
 * The merging is done by creating a {@link TreeCoreferenceInformation} object
 * that represents the co-reference relations between the trees' nodes.
 * 
 * The parse trees (and their nodes) are not changed.
 
 * 
 * 
 * @author Asher Stern
 *
 * @param <T> the type of a tree node (typically, a subclass of {@link AbstractNode}).
 */
public class GenericCorefMerger<T>
{
	public GenericCorefMerger(List<WordWithCoreferenceTag> corefSystemOutput,
			List<List<T>> trees, CorefMatchFinder<T> corefMatchFinder,
			CorefOperator<T> corefOperator)
	{
		super();
		this.corefSystemOutput = corefSystemOutput;
		this.trees = trees;
		this.corefMatchFinder = corefMatchFinder;
		this.corefOperator = corefOperator;
	}

	public void merge() throws TreeCoreferenceInformationException
	{
		IteratorByMultipleIterables<T> treesIterator = new IteratorByMultipleIterables<T>(trees);
		
		Matcher<T, WordWithCoreferenceTag> matcher = new Matcher<T, WordWithCoreferenceTag>(treesIterator, corefSystemOutput.iterator(), corefMatchFinder, corefOperator);
		matcher.makeMatchOperation();
		
		if (corefOperator.getException()!=null)
		{
			throw corefOperator.getException();
		}
		this.coreferenceInformation = corefOperator.getCorefInformation();
	}
	
	public TreeCoreferenceInformation<T> getCoreferenceInformation() throws CorefMergeException
	{
		if (null==this.coreferenceInformation) throw new CorefMergeException("Caller\'s bug. merge() was not called.");
		return this.coreferenceInformation;
	}

	protected List<WordWithCoreferenceTag> corefSystemOutput;
	protected List<List<T>> trees;
	protected CorefMatchFinder<T> corefMatchFinder;
	protected CorefOperator<T> corefOperator;
	
	protected TreeCoreferenceInformation<T> coreferenceInformation = null;
}
