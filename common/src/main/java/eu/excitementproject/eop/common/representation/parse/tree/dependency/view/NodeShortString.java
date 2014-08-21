package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public abstract class NodeShortString {
	private static final String ROOT_STR = "<ROOT>";
	public abstract <I extends Info> String toString(AbstractNode<? extends I, ?> node);
	
	public static class Rel extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR);
		}
	}
	
	public static class RelPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class RelCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
	
	public static class WordRel extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo());
		}
	}
	
	public static class WordRelPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo())+"/"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class WordRelCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo())+"/"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
}
