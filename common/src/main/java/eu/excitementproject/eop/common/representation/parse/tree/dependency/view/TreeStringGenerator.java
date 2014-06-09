package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import java.util.Collection;
import java.util.Stack;
import java.util.Vector;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;





/**
 * Creates a multi-line string that is a graphical representation of the tree.
 * The graphical representation is merely several text lines, with characters
 * like "|" and "-" to represent edges, and the string produced by
 * a {@link NodeString} object.
 * @author Asher Stern
 *
 */
public class TreeStringGenerator<I extends Info>
{
	//////// nested Exception class //////////////////
	public static class TreeStringGeneratorException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public TreeStringGeneratorException(String message){
			super(message);
		}

		public TreeStringGeneratorException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	////////////////////// PRIVATE PART //////////////////////////
	private final static char SPACE = ' ';
	private final static char HORISONAL_EDGE = '-';
	private final static char VERTICAL_EDGE = '|';
	
	private static String generate(char c, int length)
	{
		char[] retchars = new char[length];
		for (int index=0;index<length;++index)
		{
			retchars[index] = c;
		}
		return new String(retchars);
		
	}
	
	private static String spaces(int length)
	{
		return generate(SPACE, length);
	}
	
	private static String center(String str, int length, char left, char right)
	{
		if (str.length()>=length) return str;
		else return generate(left,(length-str.length())/2+(length-str.length())%2)+str+generate(right,(length-str.length())/2);
		
	}
	
	private static String center(String str, int length)
	{
		return center(str,length,SPACE,SPACE);
	}
	

	
	private static boolean anyStackHasNext(Collection<Stack<String>> listStack)
	{
		boolean ret = false;
		for (Stack<?> stack : listStack)
		{
			if (!stack.empty())
				ret = true;
		}
		return ret;
	}
	
	private static <T> Stack<T> flip(Stack<T> stack)
	{
		Stack<T> ret = new Stack<T>();
		while (!stack.empty())
		{
			ret.push(stack.pop());
		}
		return ret;
	}
	
	/////////////// PROTECTED PART - IMPLEMENTATION //////////////////////
	
	protected Stack<String> generateStringStackImplementation2() throws TreeStringGeneratorException
	{
		Stack<String> ret = new Stack<String>();
		int totalLength = 0;
		if ((root.getChildren()==null) || (root.getChildren().size()==0))
		{
		}
		else
		{
			Vector<Stack<String>> childrenStacks = new Vector<Stack<String>>();
			for (AbstractNode<? extends I, ?> child : root.getChildren())
			{
				TreeStringGenerator<I> childTsg = new TreeStringGenerator<I>(nodeString,child);
				childrenStacks.add(childTsg.generateStringStack());
			}
			if (childrenStacks.size()>0)
			{
				StringBuffer childrenEdges = new StringBuffer();
				Stack<String> firstChildStack = childrenStacks.get(0);
				if (childrenStacks.size()>1)
				{
					int firstChildLength = firstChildStack.peek().length()+1+1;
					childrenEdges.append(center(String.valueOf(VERTICAL_EDGE),firstChildLength,SPACE,HORISONAL_EDGE));
					for (int index=1;index<(childrenStacks.size()-1);++index)
					{
						int childLength = childrenStacks.get(index).peek().length()+1+1;
						childrenEdges.append(center(String.valueOf(VERTICAL_EDGE),childLength,HORISONAL_EDGE,HORISONAL_EDGE));
					}
					int lastChildLength = childrenStacks.get(childrenStacks.size()-1).peek().length()+1+1;
					childrenEdges.append(center(String.valueOf(VERTICAL_EDGE),lastChildLength,HORISONAL_EDGE,SPACE));
				}
				else
				{
					childrenEdges.append(center(String.valueOf(VERTICAL_EDGE),childrenStacks.get(0).peek().length()+1+1));
					
				}
				
				for (Stack<String> childStack : childrenStacks)
				{
					totalLength += childStack.peek().length()+1+1;
				}
				String parentEdge = center(String.valueOf(VERTICAL_EDGE),totalLength);
				
				Vector<Integer> childrenLengthes = new Vector<Integer>();
				for (Stack<String> childStack : childrenStacks)
				{
					childrenLengthes.add(childStack.peek().length()+1+1);
				}
				
				// push all children
				Stack<String> allChildren = new Stack<String>();
				while (anyStackHasNext(childrenStacks))
				{
					StringBuffer sb = new StringBuffer();
					int index=0;
					for (Stack<String> childStack : childrenStacks)
					{
						if (childStack.empty())
						{
							sb.append(spaces(childrenLengthes.get(index)));
						}
						else
						{
							sb.append(center(SPACE+childStack.pop()+SPACE,childrenLengthes.get(index)));
						}
						++index;
					}
					allChildren.push(sb.toString());
				}
				ret = flip(allChildren);
				ret.push(childrenEdges.toString());
				ret.push(parentEdge);
			}
		}
		nodeString.set(root);
		String rootString = nodeString.getStringRepresentation();
		if (totalLength==0)
			totalLength = rootString.length();

		ret.push(center(rootString,totalLength));

		return ret;
	}
	


	
	////////////////////// PUBLIC PART //////////////////////////////

	
	/**
	 * Construct the object with:
	 * <ol>
	 * <li> the root of the parse tree </li>
	 * <li> an object of type {@link NodeString} that will produce a string
	 * for each node in the tree. </li>
	 * </ol>
	 * @param nodeString used to produce a string for each node in the tree.
	 * @param root the root of the parse tree.
	 */
	public TreeStringGenerator(NodeString<I> nodeString, AbstractNode<? extends I, ?> root)
	{
		this.root = root;
		this.nodeString = nodeString;
	}
	
	
	public Stack<String> generateStringStack() throws TreeStringGeneratorException
	{
		try
		{
			return generateStringStackImplementation2();
		}
		catch(RuntimeException e)
		{
			throw new TreeStringGeneratorException("Unknown error while trying to build the tree string representation.",e);
		}
	}
	
	
	
	public String generateString() throws TreeStringGeneratorException
	{
		Stack<String> stack = generateStringStack();
		if (stack.empty()) return "";
		else
		{
			StringBuffer ret = new StringBuffer();
			while (!stack.empty())
			{
				String line = stack.pop();
				ret.append(line+"\n");
			}
			return ret.toString();
		}
	}
	
	
	
	
	
	
	
	protected AbstractNode<? extends I, ?> root;
	protected NodeString<I> nodeString;
	

}
