/**
 * The flow of the system.
 * The most important interface here is {@link eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor},
 * which is where the actual work starts.
 * Read the comments in this interface and relevant classes.
 * <P>
 * In the following lines I will briefly describe the flow of the system.
 * The main functionality of the system is: Given a text-hypothesis pair,
 * the system has to detect whether the text entails the hypothesis.
 * This is done by "proving" the hypothesis from the text, using a sequence
 * of operations - a "proof" - and evaluating the proof's validity. Each
 * proof is represented by a feature-vector. So, given a text-hypothesis pair,
 * the system:
 * <OL>
 * <LI>Finds a proof of the hypothesis from the text</LI>
 * <LI>That proof is represented by a feature-vector</LI>
 * <LI>The system returns the proof (a description of the sequence
 * of the operations), and the feature vector.</LI>
 * </OL>
 * All of this is done by {@link eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor}.
 * <BR>
 * How {@link eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor} works?
 * This is done by some kind of a <B>search mechanism</B>, which finds
 * the "best" proof. <B>Every</B> search mechanism needs the following
 * functionality: Given a tree - generate all trees that can be generated
 * by applying a <B>single</B> operation on that tree. For example,
 * Given a tree, there might be a WordNet rule that can substitute
 * one of its node, and there might be an "insert node" operation that
 * can add a missing node (with respect to the hypothesis tree) into
 * the given tree. That functionality is given by the class
 * {@link eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations}.<BR>
 * Now, let's go on.
 * The {@link eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations}
 * is given a tree. But in addition it is given a list of operation-items
 * (which are types of operations). The class {@link eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations}
 * only returns generations that can be done by applying one of the
 * given operation items. For example, if the list of operation items
 * does not include "insert node" operation - than it will not
 * be applied. In principle - always any operation can be applied, and
 * thus the list should contain any possible operation-item. In practice
 * how ever the list is limited: Coreference operations are not
 * always applied, but only in the first three "iterations" (according
 * to the currently hard-coded class {@link eu.excitementproject.eop.biutee.rteflow.macro.DefaultOperationScript}.
 * You might ask your self what is "iteration"? Well, "iteration" is
 * interpreted according to the search algorithm, but in all of them
 * it indicates how many proof steps were performed on the given tree.
 * For example: if the given tree is one of the original text trees, then
 * it means that no operations were performed on it, and its "iteration"
 * is 0.
 * <BR>
 * 
 * 
 * 
 * <P>
 * This package also contains many classes and interfaces that are relatively
 * "general" in the system.
 * <BR>
 * Note that there are still some hard-coded strings related to parsers, in the
 * class {@link eu.excitementproject.eop.biutee.rteflow.micro.PathObservations}, so if
 * you want to change parser, you will have to deal with it.
 * 
 * @author Asher Stern
 */
package eu.excitementproject.eop.biutee.rteflow;

