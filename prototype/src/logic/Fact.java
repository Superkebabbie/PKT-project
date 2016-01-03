package logic;

import knowledgeBase.KnowledgeBase;

public class Fact implements TruthState {

	private String name;
	private String value;
	
	public Fact(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean valuate(KnowledgeBase kb) {
		//return whether the fact is true in the knowledgebase
		return kb.getFacts().get(name) != null && kb.getFacts().get(name).equals(value);
	}
	
	@Override
	public String toString(){
		return "(" + name + " == " + value + ")";
	}

	@Override
	public void add(TruthState ts) {
		//Hacky (TODO). Doesn't actually do anything, because it is the base case.
		//Required however to have XMLReader be able to call it.
	}

}
