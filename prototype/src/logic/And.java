package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import knowledgeBase.KnowledgeBase;

public class And implements TruthState {
	
	private ArrayList<TruthState> elements;
	
	public And(){
		this.elements = new ArrayList<TruthState>();
	}
	
	public And(ArrayList<TruthState> elements) {
		this.elements = new ArrayList<TruthState>(elements);
	}
	
	@Override
	public TruthState cloneDeep() {
		return new And(elements);
	}

	@Override
	public boolean proven(KnowledgeBase kb) {
		//if you can find one element not proven, you can't say it is proven yet.
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (!cur.proven(kb)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean disproven(KnowledgeBase kb) {
		//inverted proven, if you can find one element disproven you can say this is disproven.
		Iterator<TruthState> els = elements.iterator();
		while (els.hasNext()){
			TruthState cur = els.next();
			if (cur.disproven(kb)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ArrayList<TruthState> backwardChain(KnowledgeBase kb){
		//the conditions of an AND are all the conditions of its elements; we have to make all possible combination that satisfy it
		//ANDs will make a lot of initial conditions but are easily disproven (and removed)
		ArrayList<TruthState> conditions = new ArrayList<TruthState>(1);
		ArrayList<ArrayList<TruthState>> poss = new ArrayList<ArrayList<TruthState>>();

		//gather all elements and their conditions
		Iterator<TruthState> es = elements.iterator();
		int idx = 0;
		while(es.hasNext()){
			TruthState e = es.next();
			poss.add(idx, new ArrayList<TruthState>());
			poss.get(idx).add(e);
			poss.get(idx).addAll(e.backwardChain(kb));
			++idx;
		}
		
		//powerlist
		Iterator<ArrayList<TruthState>> ps = poss.iterator();
		idx = 0;
		if(ps.hasNext()){
			Iterator<TruthState> p = ps.next().iterator();
			while(p.hasNext()){
				//init
				And subCon = new And();
				subCon.add(p.next());
				conditions.add(subCon);
			}
		}
		while(ps.hasNext()){
			ArrayList<TruthState> p = ps.next();
			conditions = copyLists(conditions, p.size());
			for(int i = 0; i < conditions.size(); ++i){
				conditions.get(i).add(p.get(i%p.size()));
			}
		}
		return conditions;
	}
	
	public ArrayList<TruthState> copyLists(ArrayList<TruthState> lists, int cnt){
		//copy the list cnt times (copied elements come right after the original)
		ArrayList<TruthState> copied = new ArrayList<TruthState>(lists.size()*cnt);
		Iterator<TruthState> ls = lists.iterator();
		while(ls.hasNext()){
			TruthState l = ls.next();
			for(int i = 0; i < cnt; ++i){
				copied.add(l.cloneDeep());//WARNING copy by reference!
			}
		}
		return copied;
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

	@Override
	public double factsUsefulness(HashMap<String, String> facts) {
		//And greatly diminishes the usefulness of a set of facts as it needs all elements to be proven
		if(elements.size() == 0)
			return 0;
		int factsProven = 0;
		Iterator<TruthState> es = elements.iterator();
		while(es.hasNext()){
			factsProven += es.next().factsUsefulness(facts);
		}
		return ((double) factsProven / elements.size());
	}

}
