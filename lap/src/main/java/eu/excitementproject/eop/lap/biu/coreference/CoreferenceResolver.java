package eu.excitementproject.eop.lap.biu.coreference;

import java.util.List;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;


/**
 * Resolves co-reference relations, and creates a {@link TreeCoreferenceInformation} that
 * represents those co-reference relations.
 * 
 * <P>
 * Usage:
 * <OL>
 * <LI>Call {@link #init()}</LI>
 * <LI>Call {@link #SetInput(List, String)}</LI>
 * <LI>Call {@link #resolve()}</LI>
 * <LI>Get the co-reference information by calling the method {@link #getCoreferenceInformation()}</LI>
 * <LI>Repeat the setInput, resolve and getCoreferenceInformation as many times as required.
 * When done, and coreference resolver is not to be used any more, call {@link #cleanUp()}</LI>
 * </OL>
 * 
 * @author Asher Stern
 *
 * @param <S> the type of the tree's nodes. For example: {@link BasicNode}.
 */
public abstract class CoreferenceResolver<S>
{
	public abstract void init() throws CoreferenceResolutionException;
	public abstract void cleanUp();
	
	/**
	 * 
	 * @param trees a list of parse trees of the text. The list must be ordered by the sentences' order,
	 * i.e. the first item in the list should be the parse tree of the first sentence in the text, the
	 * second the second, etc.
	 * @param originalText The original text.
	 * @throws CoreferenceResolutionException
	 */
	public void setInput(List<S> trees, String originalText) throws CoreferenceResolutionException
	{
		if (null==trees) throw new CoreferenceResolutionException("null==trees");
		if (null==originalText) throw new CoreferenceResolutionException("null==originalText");
		this.trees = trees;
		this.originalText = originalText;
		resolved = false;
	}
	
	/**
	 * Calls a coreference resolution system that resolves the coreference relations,
	 * and creates {@link TreeCoreferenceInformation} that represents the coreference system's output.
	 * That information can be retrieved later by calling {@link #getCoreferenceInformation()}.
	 * @throws CoreferenceResolutionException
	 */
	public void resolve() throws CoreferenceResolutionException
	{
		if (resolved) throw new CoreferenceResolutionException("Resolution already done.");
		if (null==trees) throw new CoreferenceResolutionException("You did not call setInput()");
		if (null==originalText) throw new CoreferenceResolutionException("You did not call setInput()");
		
		implementResolve();
		resolved = true;
	}
	
	public TreeCoreferenceInformation<S> getCoreferenceInformation() throws CoreferenceResolutionException
	{
		if (!resolved) throw new CoreferenceResolutionException("Not yet resolved.");
		return this.coreferenceInformation;
	}
	
	protected abstract void implementResolve() throws CoreferenceResolutionException;
	
	
	protected List<S> trees = null;
	protected String originalText = null;
	protected TreeCoreferenceInformation<S> coreferenceInformation = null;
	protected boolean resolved = false;
}
