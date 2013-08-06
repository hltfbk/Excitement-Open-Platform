/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.charger;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.DirectedPair;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.DirectedPairSet;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.PairSet;

/**
 * This class is concerned with Cgx xml files, particularly with parsing out all the nodes, edges and mappings 
 * out of it, using DOM. <br>
 * Usage: instantiate this class with a Cgx file's text, and use the methods to parse out the nodes, edges, and alignment edges. For other files, use new
 * instantiations.  
 * 
 * @author Amnon Lotan
 * @since 18/06/2011
 * 
 */
public class CgxDomParser	 
{
	private final Document doc;
	/**
	 * Map each Concept ID to its String Label, created by {@link #parseNodes()}
	 */
	private Map<Long, String>  nodesMap;

	/**
	 * Ctor
	 * @param cgxText
	 * @param compilationServices
	 * @throws CompilationException 
	 */
	public CgxDomParser(String cgxText) throws CompilationException 
	{
		 // prepare the xml file to be read
		  doc = prepareDoc(cgxText);
	}

	/**
	 * Scan the CGX text, and return a map from each Concept ID to its text contents
	 * 
	 * @param twoRoots
	 * @return
	 * @throws CompilationException
	 */
	public Map<Long, String> parseNodes() throws CompilationException 
	{
		Map<Long, String> nodesMap = new LinkedHashMap<Long, String>();
		
		// read all "concepts" in the CGX
		  NodeList nodeLst = doc.getElementsByTagName("concept");
	
		  for (int s = 0; s < nodeLst.getLength(); s++) {
	
			// get a concept node
		    Node fstNode = nodeLst.item(s);
		    
		    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		  
			    Element fstElmnt = (Element) fstNode;
			      
			    String stringID = fstElmnt.getAttribute("id");
			    long id = Long.parseLong(stringID);
			      
			    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("type");
			    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			    NodeList fstNmElmntLst2 = fstNmElmnt.getElementsByTagName("label");
			    Element fstNmElmnt2 = (Element) fstNmElmntLst2.item(0);
			      
			    NodeList fstNm = fstNmElmnt2.getChildNodes();		      
			      
			    String label = ((Node) fstNm.item(0)).getNodeValue();
			    
				nodesMap.put(id, label);
		    }
		  }
		  this.nodesMap = nodesMap;
		  return nodesMap;
	}
	
	/**
	 * parse the undirected edges from the CGX doc
	 * 
	 * @return
	 * @throws CompilationException
	 */
	public PairSet<Long> parseEdges() throws CompilationException 
	{
		if (nodesMap == null)
			nodesMap = parseNodes();
		PairSet<Long> undirectedEdges = new PairSet<Long>();

		NodeList nodeLst = doc.getElementsByTagName("coref");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			// get a coref node
			Node fstNode = nodeLst.item(i);

			if (fstNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element fstElmnt = (Element) fstNode;
				Long id1 = Long.parseLong( fstElmnt.getAttribute("from") );
				Long id2 = Long.parseLong( fstElmnt.getAttribute("to") );
				if (id1 == null || id2 == null)
					throw new CompilationException("There is a coref line that doesn't connect two Concepts");

				undirectedEdges.put(new Pair<Long>(id1, id2));
			}
		}
		return undirectedEdges;
	}
	
	/**
	 * return the mappings from some LHS nodes to RHS nodes
	 * @return
	 * @throws CompilationException
	 */
	public List<GenericAlignment> parseMappings() throws CompilationException 	
	{
		if (nodesMap == null)
			nodesMap = parseNodes();
		DirectedPairSet<Long> partialArrowsMap = new DirectedPairSet<Long>();
		
		// get all arrows
		NodeList arrowList = doc.getElementsByTagName("arrow");

		// read all partial arrows
		for (int s = 0; s < arrowList.getLength(); s++) 
		{
			// get an arrow node
		    Node fstNode = arrowList.item(s);
		    		    
		    if (fstNode.getNodeType() == Node.ELEMENT_NODE) 
		    {		  
			    Element fstElmnt = (Element) fstNode;
			     
			    // notice that you must use DirectedPair here and NOT Pair, cos the direction counts
			    partialArrowsMap.put(new DirectedPair<Long>(Long.parseLong( fstElmnt.getAttribute("from")), Long.parseLong( fstElmnt.getAttribute("to")) ));
		    }		    
		}
		
		// read all Actor nodes
		Map<Long, String> actorsMap = new LinkedHashMap<Long, String>();
		NodeList actorList = doc.getElementsByTagName("actor");
		//		System.out.println("AnnotatedInformation of all actors");

		for (int s = 0; s < actorList.getLength(); s++) 
		{
			// get an arrow node
		    Node fstNode = actorList.item(s);
		    		    
		    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		  
			    Element fstElmnt = (Element) fstNode;
			    Long id = Long.parseLong( fstElmnt.getAttribute("id") ); 
			    
			    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("type");
			    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			    NodeList fstNmElmntLst2 = fstNmElmnt.getElementsByTagName("label");
			    Element fstNmElmnt2 = (Element) fstNmElmntLst2.item(0);
			      
			    NodeList fstNm = fstNmElmnt2.getChildNodes();
			    String actorLabel = ((Node) fstNm.item(0)).getNodeValue().trim();
			    //			    System.out.println("Actor : "  + label);			    
			    actorsMap.put(id, actorLabel);			    			   			    
		    }		    
		}		
				  
		List<GenericAlignment> alignmentEdges = new ArrayList<GenericAlignment>();
		// build the mappings
		for (DirectedPair<Long> leftArrow : partialArrowsMap.getAll())
		{
			Long leftCoceptId = leftArrow.getLeft();

			// if this is the left side of a whole arrow (and not the middle of an arrow)
			if ( nodesMap.containsKey(leftCoceptId))
			{				  
				Long middle = leftArrow.getRight();
				@SuppressWarnings("unchecked")
				DirectedPair<Long>[] arrows = (DirectedPair<Long>[]) new DirectedPair[2];  
				arrows =  partialArrowsMap.getDirectedPairsContaining(middle).toArray(arrows);
				DirectedPair<Long> rightArrow = arrows[0].equals(leftArrow) ? arrows[1] : arrows[0]; 
				if (!actorsMap.containsKey(middle))
					throw new CompilationException("There's an arrow coming out of a Concept and pointing at something which isn't an Actor. An arrow from a " +
							"Concept must point at an Actor");
				String actorLablel = actorsMap.get(middle);
				alignmentEdges.add(new GenericAlignment(leftArrow.getLeft(), rightArrow.getRight() , actorLablel.toUpperCase()));
			}
		}
		return alignmentEdges;		 				
	}

	////////////////////////////////////////////// PRIVATE //////////////////////////////////////////////////////
	
	private static Document prepareDoc(String text) throws CompilationException {

		Document doc = null;
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(text)));
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			throw new CompilationException("DOM had an error parsing this xml ");
		}
		return doc;
	}
}
