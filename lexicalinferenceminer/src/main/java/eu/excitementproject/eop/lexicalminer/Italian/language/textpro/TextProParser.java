package eu.excitementproject.eop.lexicalminer.Italian.language.textpro;

import java.util.ArrayList;

import java.util.HashMap;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelationType;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.partofspeech.MiniparPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.InstrumentCombinationException;

/**
 * Implementation of {@link EnglishSingleTreeParser} for TextPro.
 *
 * @author Asher Stern
 */
public class TextProParser implements BasicParser {
    ////////////////////////// CONSTANTS ///////////////////////
    public static final String ROOT_NODE_ID = "ROOT";
    // Used if no other node was found as root (i.e. never used).

/*
    protected static final int IGNORED_MINIPAR_LINES_HEADER = 1;
    protected static final int IGNORED_MINIPAR_LINES_FOOTER = 1;
    protected static final int IGNORED_MINIPAR_LINES = IGNORED_MINIPAR_LINES_HEADER + IGNORED_MINIPAR_LINES_FOOTER;
    protected static final HashSet<String> IGNORED_MINIPAR_LINES_CONTENTES = new HashSet<String>();
*/
    protected static final HashMap<String, DependencyRelationType> mapRelation = new HashMap<String, DependencyRelationType>();

    private String textProPath;
    private String parser;
    private String parserPath;
    private String encoding;
    public ArrayList<HashMap<String, String>> tokens = null;

    public TextProParser(String s1, String s2, String s3, String s4) {
        this.textProPath = s1;
        this.parser = s2;
        this.parserPath = s3;
        this.encoding = s4;
    }

    public TextProParser(ConfigurationParams params) throws InstrumentCombinationException {
        try {
            this.textProPath = params.getString("textPro-path");
            this.parser = params.get("parser");
            this.parserPath = params.getString("parser-path");
            this.encoding = params.getString("encoding");
        } catch (ConfigurationException e) {
            throw new InstrumentCombinationException("Nested exception in reading the configuration file", e);
        }
    }

    public static DependencyRelationType getDependencyRelationType(String dependencyRelationString) {
        return mapRelation.get(dependencyRelationString);
    }

    static {
//        IGNORED_MINIPAR_LINES_CONTENTES.add("(");
//        IGNORED_MINIPAR_LINES_CONTENTES.add(")");

        /*
        mapRelation.put("obj", DependencyRelationType.OBJECT);
        mapRelation.put("obj2", DependencyRelationType.OBJECT);
        mapRelation.put("subj", DependencyRelationType.SUBJECT);
        mapRelation.put("s", DependencyRelationType.SUBJECT);
        */
        mapRelation.put("DOBJ", DependencyRelationType.OBJECT);
        mapRelation.put("SUBJ", DependencyRelationType.SUBJECT);
    }

    /////////////////// PRIVATE & PROTECTED ///////////////////////


    /**
     * This class groups together a {@link BasicConstructionNode},
     * and an ID of the parent of that node.
     * <p/>
     * Note: the ID is <B> not </B> the id of that
     * {@link BasicConstructionNode}, but the ID of its parent.
     *
     * @author Asher Stern
     */
    protected final static class ParentAndConstructionNode {
        public String parentId;
        public BasicConstructionNode node;
    }

    protected static MiniparPartOfSpeech convertMiniparCategoryToPartOfSpeech(String miniparCategory) throws UnsupportedPosTagStringException {
        return new MiniparPartOfSpeech(miniparCategory);
    }


    protected void cleanSentence() {
        sentence = null;
        allNodes = null;
        wordsOnlyNodes = null;
        root = null;
        rootOfImmutable = null;
        serial = 1;

    }

    /**
     * Builds a {@link BasicConstructionNode} that reflects the given line.
     * In addition it extracts the parent-id.
     * <p/>
     * Then, the {@link BasicConstructionNode} and the parent-id are grouped
     * together into a {@link ParentAndConstructionNode}.
     * <p/>
     * Note that {@link ParentAndConstructionNode} object holds a {@link BasicConstructionNode}
     * and an ID, but that ID does <B> not </B> belong to the {@link BasicConstructionNode},
     * but to its parent.
     *
     * @param token a line, which is one line in {@link ArrayList}
     *              of lines that was returned from {@link this.parse(String)} method.
     * @return a {@link ParentAndConstructionNode} object, that contains the
     *         {@link BasicConstructionNode} that reflects the given line, with the ID of
     *         <B> its parent </B>.
     * @throws UnsupportedPosTagStringException
     *
     */
    protected ParentAndConstructionNode buildConstructionNode(HashMap<String, String> token) throws UnsupportedPosTagStringException {
        ParentAndConstructionNode ret = new ParentAndConstructionNode();

        String label = token.get("parselabel");
        String word = token.get("token");
        String lemma = token.get("lemma");

        DependencyRelation relation = null;
        if (mapRelation.containsKey(label)) {
            relation = new DependencyRelation(label, mapRelation.get(label));
        }
        else {
            relation = new DependencyRelation(label, null);
        }

        TextProPartOfSpeech pos = new TextProPartOfSpeech(token.get("pos"));

        DefaultEdgeInfo defaultEdgeInfo = new DefaultEdgeInfo(relation);
        DefaultSyntacticInfo syntacticInfo = new DefaultSyntacticInfo(pos);
        DefaultNodeInfo defaultNodeInfo = new DefaultNodeInfo(word, lemma, this.serial, null, syntacticInfo);
        DefaultInfo info = new DefaultInfo(token.get("parseid"), defaultNodeInfo, defaultEdgeInfo);

        this.serial++;

/*
        if (lineParser.getWord() == null)
            ; // serial has no meaning. Next word should get the current serial.
        else
            this.serial++; // We assigned the current serial to the current ConstructionNode.
        // So the next word should get the next serial.
*/


        ret.node = new BasicConstructionNode(info);
        ret.parentId = token.get("parseparent");

/*
        // and - add the antecedent information
        if (lineParser.getAntecedentLabel() != null) {
            mapAntecedentOf.put(lineParser.getLabel(), lineParser.getAntecedentLabel());
        }
*/

        return ret;
    }


    ////////////////////////////// PUBLIC METHODS //////////////////////////


    /* (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#init()
      */
    public void init() {

    }

    /* (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#setSentence(java.lang.String)
      */
    public void setSentence(String sentence) {
        cleanSentence();
        this.sentence = sentence;
    }

    /* (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#parse()
      */
    @SuppressWarnings("unchecked")
	public void parse() throws ParserRunException {
        // Verifying parameters and flow are legal.
        if (this.sentence == null)
            throw new ParserRunException("the parse method was called with null sentence.");

        String[] settings = {"token", "sentence", "pos", "lemma", "parse"};
        String language = "ita";

        //"/home/aprosio/textpro-sw/", "", "", "ISO-8859-1"
        TextPro tp = new TextPro(textProPath, parser, parserPath, encoding);
        tp.debug = false;

        this.tokens = tp.run(sentence, language, settings);
        // System.out.println(tokens);

        // Creating the parse tree. First creating the root.
        DefaultInfo rootInfo = new DefaultInfo(ROOT_NODE_ID, new DefaultNodeInfo(null, null, 0, null, new DefaultSyntacticInfo(null)), new DefaultEdgeInfo(null));
        root = new BasicConstructionNode(rootInfo);

        // This map: maps an ID to its ConstructionNode.
        // In addition, the mapping also maps to the ID of its parent node.
        HashMap<String, ParentAndConstructionNode> mapIdToNode = new HashMap<String, ParentAndConstructionNode>();
        this.allNodes = new ArrayList<BasicConstructionNode>(this.tokens.size());
        this.mapAntecedentOf = new HashMap<String, String>();

        try {
            for (HashMap<String, String> token : this.tokens) {
                ParentAndConstructionNode parentAndNode = buildConstructionNode(token);
                allNodes.add(parentAndNode.node);
                mapIdToNode.put(parentAndNode.node.getInfo().getId(), parentAndNode);
            }
            // wordsOnlyNodes = new ArrayList<BasicConstructionNode>(serial - 1);
            wordsOnlyNodes = (ArrayList<BasicConstructionNode>) allNodes.clone();

            // This loop has two purposes:
            // 1. Add elements to the wordsOnlyNodes list. Since Minipar
            //    returns the lines in the order the words appear in the sentence,
            //    they are just added in that order (which is the correct order)
            // 2. Add the children of a node to it.
            //    I.e. We just have list of nodes, but none of them has children.
            //    Though, we do know for each node who is parent is (by mapIdToNode map).
            for (BasicConstructionNode node : allNodes) {
                // The first purpose.

/*
                if (node.getInfo() != null) {
                    if (node.getInfo().getNodeInfo() != null) if (node.getInfo().getNodeInfo().getWord() != null) {
                        wordsOnlyNodes.add(node);
                    }
                }
*/

                // Now, just locate the parent, and add this node as child to its parent.
                String parentId = mapIdToNode.get(node.getInfo().getId()).parentId;

                // if (parentId != null) {
                if (!parentId.equals("0")) {
                    mapIdToNode.get(parentId).node.addChild(node);
                }
                else {
                    root.addChild(node);
                }

            } // end of for loop.

            // Add antecedent information
/*
            for (String id : this.mapAntecedentOf.keySet()) {
                String idAntecedent = mapAntecedentOf.get(id);
                BasicConstructionNode currentNode = mapIdToNode.get(id).node;
                if (idAntecedent != null) // and it never should be null...
                {
                    BasicConstructionNode antecedentNode = mapIdToNode.get(idAntecedent).node;
                    currentNode.setAntecedent(antecedentNode);
                }
            }
*/

            if (root.getChildren() == null)
                throw new ParserRunException("It looks as if the parser has returned a cyclic graph, instead of a tree.");
            if (root.getChildren().size() == 0)
                throw new ParserRunException("It looks as if the parser has returned a cyclic graph, instead of a tree.");
            if (root.getChildren().size() == 1)
                root = root.getChildren().get(0); // replace the artificial root, by the root specified by Minipar.

        } catch (NullPointerException e) // should never happen.
        {
            throw new ParserRunException("Malformed Construction node was created, and caused NullPointerException", e);
        } catch (UnsupportedPosTagStringException e) {
            e.printStackTrace();
            throw new ParserRunException("Unsupported part-of-speech tag. See nested exception", e);
        }
    }

    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#getMutableParseTree()
      */
    public BasicConstructionNode getMutableParseTree() throws ParserRunException {
        if (null == this.root)
            throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

        return this.root;
    }

    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#getNodesOrderedByWords()
      */
    public ArrayList<BasicConstructionNode> getNodesOrderedByWords() throws ParserRunException {
        if (null == this.root)
            throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

        return this.wordsOnlyNodes;
    }

    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#getNodesAsList()
      */
    public ArrayList<BasicConstructionNode> getNodesAsList() throws ParserRunException {
        if (null == this.root)
            throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

        return this.allNodes;
    }


    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#getParseTree()
      */
    public BasicNode getParseTree() throws ParserRunException {
        if (null == this.root)
            throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

        this.rootOfImmutable = AbstractNodeUtils.copyTree(root, new BasicNodeConstructor());
        return this.rootOfImmutable;
    }


    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#reset()
      */
    public void reset() {
        cleanSentence();
    }


    /*
      * (non-Javadoc)
      * @see ac.biu.nlp.nlp.instruments.parse.Parser#cleanUp()
      */
    public void cleanUp() {
        cleanSentence();
    }


    protected String features = null;
    protected String sentence = null;
    protected ArrayList<BasicConstructionNode> allNodes;
    protected ArrayList<BasicConstructionNode> wordsOnlyNodes;
    protected BasicConstructionNode root;
    protected BasicNode rootOfImmutable = null; // can stay null until somebody
    // wants the immutable tree.
    protected HashMap<String, String> mapAntecedentOf; // map ID to ID.
    protected int serial = 1;

    public static void main(String[] args) {
        TextProParser prova = new TextProParser("/home/aprosio/textpro-sw/", "malt", "/home/aprosio/tp-ita/italian-parser-1.7.1/", "ISO-8859-1");
        prova.init();
        prova.setSentence("La mamma mangia la pesca");
        try {
            prova.parse();
            System.out.println(prova.tokens);
            System.out.println("");

            System.out.println(prova.root);
            System.out.println(prova.root.getChildren());
            System.out.println("");
            for (BasicConstructionNode node : prova.allNodes) {
                System.out.println(node);
                System.out.println(node.getChildren());
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
