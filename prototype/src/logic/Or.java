package logic;

import java.util.ArrayList;
import java.util.Iterator;

import knowledgeBase.KnowledgeBase;

public class Or implements TruthState {
	
	private ArrayList<TruthState> elements;
	
	public Or(){
		this.elements = new ArrayList<TruthState>();
	}
	
	public Or(ArrayList <TruthState> elements) {
		this.elements = elements;
	}

	@Override
	public boolean valuate(KnowledgeBase kb) {
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (cur.valuate(kb)){
				return true;
			}
		}
		return false;
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
			s.append(" OR " + cur.toString());
		}
		s.append(")");
		return s.toString();
	}
}
