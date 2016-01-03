package logic;

import knowledgeBase.KnowledgeBase;

public class Not implements TruthState {
	
	private TruthState contains;
	
	public Not(TruthState ts){
		this.contains = ts;
	}
	
	public Not(){
		this(null);
	}

	@Override
	public boolean valuate(KnowledgeBase kb) {
		return !contains.valuate(kb);
	}

	@Override
	public void add(TruthState ts) {
		this.contains = ts;
	}

	public void setContains(TruthState contains) {
		this.contains = contains;
	}
	
	@Override
	public String toString(){
		return "NOT " + contains.toString();
	}

}
