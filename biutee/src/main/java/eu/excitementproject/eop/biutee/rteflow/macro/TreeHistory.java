package eu.excitementproject.eop.biutee.rteflow.macro;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent.TreeHistoryComponentType;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * A tree history is a vector of {@linkplain Specification}s, that describes a proof.
 * A {@linkplain TreeHistory} might also contain other information. See {@link TreeHistoryComponent}.
 * 
 * @see TreeHistoryComponent
 * @see TreeHistoryUtilities
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class TreeHistory implements Serializable
{
	private static final long serialVersionUID = -7221907268973486192L;

	public TreeHistory(TreeHistoryComponent initialComponent)
	{
		this.initialComponent = initialComponent;
		components = new Vector<TreeHistoryComponent>();
	}
	public TreeHistory(TreeHistory other) throws TeEngineMlException
	{
		if (null==other) throw new TeEngineMlException("TreeHistory copy constructor - other is null");
		this.initialComponent = other.initialComponent;
		if (other.activeTypes!=null)
		{
			this.activeTypes = new EnumMap<TreeHistoryComponent.TreeHistoryComponentType, Boolean>(TreeHistoryComponent.TreeHistoryComponentType.class);
			this.activeTypes.putAll(other.activeTypes);
		}
		this.components = new Vector<TreeHistoryComponent>();
		this.components.addAll(other.components);
	}
	
	public void addSpecification(Specification specification) throws TeEngineMlException
	{
		addComponent(new TreeHistoryComponent(specification,null,null,null));
	}
	

	public void addSpecificationAndVector(Specification specification, Map<Integer,Double> featureVector) throws TeEngineMlException
	{
		addComponent(new TreeHistoryComponent(specification,featureVector,null,null));
	}
	
	public void addComponent(TreeHistoryComponent component) throws TeEngineMlException
	{
		if (null==activeTypes)
			constructActiveTypes(component);
		else
			validateActiveTypes(component);
		
		components.add(component);
	}

	
	
	public ImmutableList<Specification> getSpecifications()
	{
		ArrayList<Specification> list = new ArrayList<Specification>(components.size());
		for (TreeHistoryComponent component : components)
		{
			list.add(component.getSpecification());
		}
		return new ImmutableListWrapper<Specification>(list);
	}
	
	public ImmutableList<Map<Integer, Double>> getFeaturesVectors()
	{
		if (activeTypes == null || !activeTypes.containsKey(TreeHistoryComponentType.FEATURE_VECTOR) || false == activeTypes.get(TreeHistoryComponentType.FEATURE_VECTOR))
		{
			return new ImmutableListWrapper<Map<Integer, Double>>(emptyListFeatureVectors);
		}
		else
		{
			ArrayList<Map<Integer, Double>> list = new ArrayList<Map<Integer, Double>>(components.size());
			for (TreeHistoryComponent component : components)
			{
				list.add(component.getFeatureVector());
			}
			return new ImmutableListWrapper<Map<Integer, Double>>(list);
		}
	}
	
	public ImmutableList<TreeHistoryComponent> getComponents()
	{
		return new ImmutableListWrapper<TreeHistoryComponent>(components);
	}
	
	public TreeHistoryComponent getInitialComponent()
	{
		return initialComponent;
	}
	
	
	private void constructActiveTypes(TreeHistoryComponent firstComponent)
	{
		activeTypes = new EnumMap<TreeHistoryComponent.TreeHistoryComponentType, Boolean>(TreeHistoryComponent.TreeHistoryComponentType.class);
		for (TreeHistoryComponentType type : TreeHistoryComponentType.values())
		{
			activeTypes.put(type, false);
		}
		activeTypes.put(TreeHistoryComponentType.SPECIFICATION, true);
		if (firstComponent.getFeatureVector()!=null)
			activeTypes.put(TreeHistoryComponentType.FEATURE_VECTOR, true);
		if (firstComponent.getTree()!=null)
			activeTypes.put(TreeHistoryComponentType.TREE, true);
		if (firstComponent.getAffectedNodes()!=null)
			activeTypes.put(TreeHistoryComponentType.AFFECTED_NODES, true);
	}
	
	private void validateActiveTypes(TreeHistoryComponent component) throws TeEngineMlException 
	{
		if (null==component.getSpecification()) throw new TeEngineMlException("Specification must not be null");
		
		boolean hasFeatureVector = (component.getFeatureVector()!=null);
		boolean activeMapHasFeatureVector = activeTypes.containsKey(TreeHistoryComponentType.FEATURE_VECTOR) ? activeTypes.get(TreeHistoryComponentType.FEATURE_VECTOR) : false; 
		if ( activeMapHasFeatureVector!=hasFeatureVector)
			throw new TeEngineMlException("inconsistent use of feature vector field");

		boolean hasTree = (component.getTree()!=null);
		boolean activeMapHasTree = activeTypes.containsKey(TreeHistoryComponentType.TREE) ? activeTypes.get(TreeHistoryComponentType.TREE) : false;
		if (activeMapHasTree!=hasTree)
			throw new TeEngineMlException("inconsistent use of tree field");

		boolean hasAffectedNodes = (component.getAffectedNodes()!=null);
		boolean activeMapHasAffectedNodes = activeTypes.containsKey(TreeHistoryComponentType.AFFECTED_NODES) ? activeTypes.get(TreeHistoryComponentType.AFFECTED_NODES) : false;
		if (activeMapHasAffectedNodes!=hasAffectedNodes)
			throw new TeEngineMlException("inconsistent use of affected-nodes field");
	}


	private TreeHistoryComponent initialComponent;
	
	private Vector<TreeHistoryComponent> components;
	
	private EnumMap<TreeHistoryComponent.TreeHistoryComponentType, Boolean> activeTypes = null;
	
	private static final List<Map<Integer, Double>> emptyListFeatureVectors = new ArrayList<Map<Integer,Double>>(0);
}
