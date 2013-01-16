package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class SingleTreeComponent
{



	public SingleTreeComponent(ExtendedNode tree, TreeHistory history,
			Specification lastSpec, Set<ExtendedNode> affectedNodes,
			Map<Integer, Double> featureVector,
			double classificationScore, double evaluation,
			SingleTreeComponent previous, int iterationNumber, int id,
			Set<ExtendedNode> missingRelations,
			double classificationScoreForPredictions, double cost, int originalSentenceNo) throws VisualTracingToolException
	{
		super();
		synchronized (this.getClass())
		{
			if (id!=knownNextId)throw new VisualTracingToolException("id("+id+")!=knownNextId("+knownNextId+")");
			this.id = id;
			knownNextId++;
		}
		this.tree = tree;
		this.history = history;
		this.lastSpec = lastSpec;
		this.affectedNodes = affectedNodes;
		this.featureVector = featureVector;
		this.classificationScore = classificationScore;
		this.evaluation = evaluation;
		this.previous = previous;
		this.iterationNumber = iterationNumber;
		this.missingRelations = missingRelations;
		this.classificationScoreForPredictions = classificationScoreForPredictions;
		this.cost = cost;
		if (originalSentenceNo <= 0 )
			throw new VisualTracingToolException("The originalSentenceNo must be positive. I got " + originalSentenceNo);
		this.originalSentenceNo = originalSentenceNo;;
	}
	
	
	public static synchronized void resetIds()
	{
		knownNextId = 0;
	}


	public ExtendedNode getTree()
	{
		return tree;
	}

	public TreeHistory getHistory()
	{
		return history;
	}

	public Specification getLastSpec()
	{
		return lastSpec;
	}
	
	public Set<ExtendedNode> getAffectedNodes()
	{
		return affectedNodes;
	}


	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}

	public double getClassificationScoreForSearch()
	{
		return classificationScore;
	}

	public double getEvaluation()
	{
		return evaluation;
	}

	public SingleTreeComponent getPrevious()
	{
		return previous;
	}

	public int getIterationNumber()
	{
		return iterationNumber;
	}
	
	public int getId()
	{
		return id;
	}

	public Set<ExtendedNode> getMissingRelations()
	{
		return missingRelations;
	}

	public double getClassificationScoreForPredictions()
	{
		return classificationScoreForPredictions;
	}
	
	public double getCost()
	{
		return cost;
	}

	public int getOriginalSentenceNo() {
		return originalSentenceNo;
	}




	private final ExtendedNode tree;
	private final TreeHistory history;
	private final Specification lastSpec;
	private final Set<ExtendedNode> affectedNodes;
	private final Map<Integer,Double> featureVector;
	private final double classificationScore;
	private final double evaluation;
	private final SingleTreeComponent previous;
	private final int iterationNumber;
	private final int id;
	private final Set<ExtendedNode> missingRelations;
	private final double classificationScoreForPredictions;
	private final double cost;
	private final int originalSentenceNo;
	
	private static int knownNextId = 0;
}
