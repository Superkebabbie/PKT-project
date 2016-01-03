package logic;

import java.util.ArrayList;
import java.util.Iterator;

import knowledgeBase.KnowledgeBase;

public class And implements TruthState {
	
	private ArrayList<TruthState> elements;
	
	public And(){
		this.elements = new ArrayList<TruthState>();
	}
	
	public And(ArrayList <TruthState> elements) {
		this.elements = elements;
	}

	@Override
	public boolean valuate(KnowledgeBase kb) {
		assert(elements.size() >= 2);
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (!cur.valuate(kb)){
				return false;
			}
		}
		return true;
	}
	
	public void add(TruthState ts){
		elements.add(ts);
	}
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer("(");
		Iterator<TruthState> els = elements.iterator();
		TruthState cur = els.next();
		s.append(cur.toString());
		while (els.hasNext()){
			cur = els.next();
			s.append(" AND " + cur.toString());
		}
		s.append(")");
		return s.toString();
	}

}
