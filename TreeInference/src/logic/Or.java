package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import knowledgeBase.KnowledgeBase;

public class Or implements TruthState {
	
	private ArrayList<TruthState> elements;
	
	public Or(){
		this.elements = new ArrayList<TruthState>();
	}
	
	public Or(ArrayList <TruthState> elements) {
		this.elements = new ArrayList<TruthState>(elements);
	}
	
	@Override
	public TruthState cloneDeep() {
		return new Or(elements);
	}

	@Override
	public boolean proven(KnowledgeBase kb) {
		//if you can find one proven element, it is proven.
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (cur.proven(kb)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean disproven(KnowledgeBase kb) {
		//inverted proven, if you can find one element not disproven you can't say this is disproven.
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (!cur.disproven(kb)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public ArrayList<TruthState> backwardChain(KnowledgeBase kb){
		//the conditions of an OR are the conditions of one of the elements; we collect them all in a list
		ArrayList<TruthState> conditions = new ArrayList<TruthState>();
		Iterator<TruthState> es = elements.iterator();
		while(es.hasNext()){
			TruthState e = es.next();
			conditions.addAll(e.backwardChain(kb));
		}
		return conditions;
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
	
	@Override
	public double factsUsefulness(HashMap<String, String> facts) {
		//Ors are greatly benefitial for fitness as only one element has to be proven. We sum up the elements.
		double factsProven = 0;
		Iterator<TruthState> es = elements.iterator();
		while(es.hasNext()){
			factsProven += es.next().factsUsefulness(facts);
		}
		return factsProven;
	}

}
