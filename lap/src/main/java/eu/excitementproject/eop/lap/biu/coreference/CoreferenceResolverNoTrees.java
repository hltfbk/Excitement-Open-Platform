package eu.excitementproject.eop.lap.biu.coreference;

import java.util.List;

import eu.excitementproject.eop.common.representation.coreference.DockedMention;


/**
 * Resolves co-reference relations, and creates a list of {@link DockedMention} lists
 * that represents those co-reference relations. Each list represents one coreference group,
 * all docked mentions in the list are part of the same coreference group.
 * 
 * <P>
 * Usage:
 * <OL>
 * <LI>Call {@link #init()}</LI>
 * <LI>Call {@link #SetInput(String)}</LI>
 * <LI>Call {@link #resolve()}</LI>
 * <LI>Get the co-reference information by calling the method {@link #getCoreferenceInformation()}</LI>
 * <LI>Repeat the setInput, resolve and getCoreferenceInformation as many times as required.
 * When done, and coreference resolver is not to be used any more, call {@link #cleanUp()}</LI>
 * </OL>
 * 
 * <B>NOTE</B> This is based on {@link CoreferenceResolver}. The main difference is that
 * this class does not connect the coreference information to trees, but rather to offsets
 * on the input text. The main motivation here is UIMA.
 * 
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public abstract class CoreferenceResolverNoTrees
{
	public abstract void init() throws CoreferenceResolutionException;
	public abstract void cleanUp();
	
	/**
	 * @param originalText The original text, e.g. a full document.
	 * @throws CoreferenceResolutionException
	 */
	public void setInput(String originalText) throws CoreferenceResolutionException
	{
		if (null==originalText) throw new CoreferenceResolutionException("null==originalText");
		this.originalText = originalText;
		resolved = false;
	}
	
	/**
	 * Calls a coreference resolution system that resolves the coreference relations,
	 * and creates a list of {@link DockedMention} lists that represents the coreference system's output.
	 * That information can be retrieved later by calling {@link #getCoreferenceInformation()}.
	 * @throws CoreferenceResolutionException
	 */
	public void resolve() throws CoreferenceResolutionException
	{
		if (resolved) throw new CoreferenceResolutionException("Resolution already done.");
		if (null==originalText) throw new CoreferenceResolutionException("You did not call setInput()");
		
		implementResolve();
		resolved = true;
	}
	
	public List<List<DockedMention>> getCoreferenceInformation() throws CoreferenceResolutionException
	{
		if (!resolved) throw new CoreferenceResolutionException("Not yet resolved.");
		return this.dockedMentions;
	}
	
	protected abstract void implementResolve() throws CoreferenceResolutionException;
	
	
	protected String originalText = null;
	protected List<List<DockedMention>> dockedMentions = null;
	protected boolean resolved = false;
}
