package eu.excitementproject.eop.common.representation.coreference;


import java.io.Serializable;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;


// the class is NOT thread safe.

/**
 * Represents co-reference information.
 * The information is a collection of groups of nodes. Each group
 * represents the same entity in the real world (e.g. "he" and "john" refer to the
 * same entity), so the meaning is that each node in that
 * group refers to the same entity.
 * 
 * @param <T> the node type (e.g. {@link BasicNode})
 * 
 * <P>
 * <B> Note: the class is not thread safe! </B>
 * @author Asher Stern
 *
 */
public class TreeCoreferenceInformation<T> implements Serializable
{
	private static final long serialVersionUID = 7270840645037160241L;

	////////////////////// PUBLIC PART //////////////////////////////


	/**
	 * Creates a new empty group. Later, nodes can be added to the
	 * group by {@link #addNodeToGroup(Integer, BasicNode)} method.
	 * 
	 * @return the new group's ID.
	 */
	public Integer createNewGroup()
	{
		Integer id = nextId;
		mapIdToCorefGroups.put(id, new LinkedHashSet<T>());
		nextId++;
		return id;
	}
	
	/**
	 * Adds a {@linkplain BasicNode} to an existing group.
	 * <P>
	 * The group (identified by <code> id </code> must exist! It
	 * may be empty, but it must exist. Otherwise an exception
	 * ( {@link TreeCoreferenceInformationException} ) will be thrown.
	 * @param id the group ID to which the node should be added.
	 * @param node
	 * @throws TreeCoreferenceInformationException
	 */
	public void addNodeToGroup(Integer id, T node) throws TreeCoreferenceInformationException
	{
		if (id==null)
			throw new TreeCoreferenceInformationException("id == null");
		if (!mapIdToCorefGroups.containsKey(id))
			throw new TreeCoreferenceInformationException("Invalid id "+id+". The coreference group number "+id+" does not exist.");
		if (mapIdToCorefGroups.get(id) == null)
			throw new TreeCoreferenceInformationException("internal BUG: coreference group number "+id+" is null");
		
		if (mapNodeToId.get(node) != null)
		{
			if (mapNodeToId.get(node).intValue() != id.intValue())
				throw new TreeCoreferenceInformationException("The given node already belongs to another coreference group.");
			else
				; // do nothing. already exist in the required group.
		}
		else // add the node to the group.
		{
			mapIdToCorefGroups.get(id).add(node);
			mapNodeToId.put(node, id);

		}
		
	}
	
	/**
	 * Returns the group of nodes that is identified by the given ID.
	 * @param id the group's ID.
	 * @return A set of the group's Ts.
	 * @throws TreeCoreferenceInformationException invalid ID, or an unexpected error.
	 */
	public ImmutableSet<T> getGroup(Integer id) throws TreeCoreferenceInformationException
	{
		if (id==null)
			throw new TreeCoreferenceInformationException("id == null");
		if (!mapIdToCorefGroups.containsKey(id))
			throw new TreeCoreferenceInformationException("Invalid id "+id+". The coreference group number "+id+" does not exist.");
		if (mapIdToCorefGroups.get(id) == null)
			throw new TreeCoreferenceInformationException("internal BUG: coreference group number "+id+" is null");
		
		return new ImmutableSetWrapper<T>(mapIdToCorefGroups.get(id));
	}
	
	
	/**
	 * Returns the ID of the coreference group to which the given
	 * node belongs to.
	 * <B> Returns <code> null </code> if the node does not belong to
	 * any coreference group. </B>
	 * 
	 * @param node a node
	 * @return the group ID of that node,
	 * <B> or <code> null </code> if it does not belong to any group.
	 */
	public Integer getIdOf(T node)
	{
		if (!mapNodeToId.containsKey(node)) return null;
		return mapNodeToId.get(node);
	}
	
	/**
	 * Remove a node from the coreference information. If that node
	 * is the only node in its group - then the group is deleted.
	 * @param node a node to remove
	 * @throws TreeCoreferenceInformationException
	 */
	public void remove(T node) throws TreeCoreferenceInformationException
	{
		if (null==node)
			throw new TreeCoreferenceInformationException("null node");
		if (!mapNodeToId.containsKey(node))
			throw new TreeCoreferenceInformationException("the given node does not exist in that coreference information");
		
		Integer itsId = mapNodeToId.get(node);
		Set<T> itsGroup = mapIdToCorefGroups.get(itsId);
		if (itsGroup.size()==1)
		{
			mapIdToCorefGroups.remove(itsId);
		}
		else
		{
			itsGroup.remove(node);
		}
		mapNodeToId.remove(node);

		
	}
	

	/**
	 * Merges the given groups to one group. Returns the id of the merged group, which is
	 * an id of one of the given groups. The other groups are just deleted.
	 * <P>
	 * Note: In case of error, one of the following is hopefully true:
	 * <UL>
	 * <LI> Nothing is changed. </LI>
	 * <LI> <B>All</B> of the groups that can be merged are merged.</LI>
	 * </UL>
	 * (i.e. the coreference information has a "correct" state).
	 * @param groups list of groups to be merged
	 * @return the Id of the group that now contains all nodes of all the given
	 * groups.
	 * @throws TreeCoreferenceInformationException
	 */
	public Integer mergeGroups(List<Integer> groups) throws TreeCoreferenceInformationException
	{
		if (null==groups)
			throw new TreeCoreferenceInformationException("null list");
		if (groups.size()==0)
			throw new TreeCoreferenceInformationException("empty list");
		Integer ret = groups.get(0);
		if (groups.size()==1)
			; // do nothing
		else
		{
			Set<Integer> badGroups = null;
			for (Integer id : groups)
			{
				if (id<ret)
					ret = id;
			}
			if (!mapIdToCorefGroups.containsKey(ret))
				throw new TreeCoreferenceInformationException("One of the given groups, "+ret+", does not exist.");
			Set<T> retGroup = mapIdToCorefGroups.get(ret);
			if (null==retGroup)
				throw new TreeCoreferenceInformationException("Internal bug. Seems that One of the given groups, "+ret+", does not exist.");
			
			for (Integer id : groups)
			{
				if (id.intValue()!=ret.intValue())
				{
					Set<T> currentGroup = mapIdToCorefGroups.get(id);
					if (null==currentGroup)
					{
						// I still want to keep the stability of this co reference information state.
						if (badGroups==null) badGroups = new LinkedHashSet<Integer>();
						badGroups.add(id);
					}
					else
					{
						for (T node : currentGroup)
						{
							retGroup.add(node);
							mapNodeToId.put(node, ret);
						}
					}
					mapIdToCorefGroups.remove(id);
				}
			} // end of for loop
			if (badGroups!=null)
			{
				StringBuffer sbBadGroups = new StringBuffer();
				boolean firstIteration = true;
				for (Integer badId : badGroups)
				{
					if (firstIteration)
						firstIteration = false;
					else
						sbBadGroups.append(", ");

					sbBadGroups.append(badId.toString());
				}
				throw new TreeCoreferenceInformationException("The following groups do not exist: "+sbBadGroups);
			}
		}
		return ret;
	}
	
	/**
	 * Indicates whether the given group id is an id of a group in that coreference information.
	 * @param id
	 * @return
	 */
	public boolean isGroupExist(Integer id)
	{
		if (mapIdToCorefGroups.containsKey(id))
			return true;
		else
			return false;
	}

	/**
	 * Indicates whether the given node exist in one of the groups in that coreference information.
	 * @param node
	 * @return
	 */
	public boolean isNodeExist(T node)
	{
		if (mapNodeToId.containsKey(node))
			return true;
		else
			return false;
	}

	/**
	 * Returns a set of all IDs of groups in that coreference information 
	 * @return
	 */
	public ImmutableSet<Integer> getAllExistingGroupIds()
	{
		// Returning a copy of the keySet is safer.
		Set<Integer> realSetRet = new LinkedHashSet<Integer>();
		realSetRet.addAll(mapIdToCorefGroups.keySet());
		return new ImmutableSetWrapper<Integer>(realSetRet);
	}
	

	/**
	 * TODO ofer bronstein August 2013
	 * This implementation should somehow be common with a method
	 * that can assume that T inherits from AbstractNode, so that
	 * for each node we can call:
	 * 
	 *   "* " + AbstractNodeUtils.getIndentedString(node, "  ", "\t\t  ");
	 *   
	 * and get relevant info on the tree.
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		writer.write("TreeCoreferenceInformation - coref groups and their members:\n");
		
		ImmutableSet<Integer> groupIDs = getAllExistingGroupIds();
		for (Integer  groupID : groupIDs)
		{
			writer.write("\tgroup #"+groupID+":\n");
			ImmutableSet<T> group;
			try {
				group = getGroup(groupID);
			} catch (TreeCoreferenceInformationException e) {
				e.printStackTrace();
				return // instead of throwing an exception
				"Cannot print coreference information due to an exception:\n"+ExceptionUtil.getStackTrace(e);
			}
			for (T node :  group)
				writer.write("\t\t"+node+"\n");
		}
		return writer.toString();
	}	

	//////////////////// PROTECTED PART ////////////////////////////
	
	protected Map<T, Integer> mapNodeToId = new LinkedHashMap<T, Integer>();
	protected Map<Integer,Set<T>> mapIdToCorefGroups = new LinkedHashMap<Integer, Set<T>>();
	
	private Integer nextId = new Integer(1);
}
