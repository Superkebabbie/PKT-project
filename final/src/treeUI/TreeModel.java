package treeUI;

import java.util.Observable;

import tree.Tree;

public class TreeModel extends Observable {
	private Tree tree;
	
	// The tree model contains a tree and nodes used for drawing in TreePanel
	public TreeModel(){
		tree = new Tree();
	}
	
	public void setModel(Tree tree){
		this.tree = tree;
	}

	public Tree getModel() {
		return tree;
	}
	
}
