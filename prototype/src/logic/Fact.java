package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import knowledgeBase.KnowledgeBase;
import knowledgeBase.Rule;

public class Fact implements TruthState {

	private String name;
	private String value;
	private boolean negative;
	
	public Fact(String name, String value){
		this.name = name;
		if(value.startsWith("-")){
			this.negative = true;
			this.value = value.substring(1);
		}else{
			this.value = value;
		}
	}
	
	@Override
	public TruthState cloneDeep() {
		return new Fact(name,value);
	}
	
	@Override
	public boolean proven(KnowledgeBase kb) {
		//return whether the fact is true in the knowledgebase
		if(negative){
			return kb.getFacts().get(name) != null && !kb.getFacts().get(name).equals(value);
		}
		return kb.getFacts().get(name) != null && kb.getFacts().get(name).equals(value);
	}
	
	@Override
	public boolean disproven(KnowledgeBase kb) {
		//Return whether the fact has other value assigned than intended by this fact
		if(negative){
			return kb.getFacts().get(name) != null && kb.getFacts().get(name).equals(value);
		}
		return kb.getFacts().get(name) != null && !kb.getFacts().get(name).equals(value);
	}
	
	@Override
	public ArrayList<TruthState> backwardChain(KnowledgeBase kb){
		//return the conditions of any rules that lead to this fact.
		ArrayList<TruthState> conditions = new ArrayList<TruthState>();
		ArrayList<Rule> usedRules = new ArrayList<Rule>();
		Iterator<Rule> rs = kb.getRules().iterator();
		if(negative){
			while(rs.hasNext()){
				Rule r = rs.next();
				if(r.hasNegativeConsequence(name, value)){
					if(!(r.getCondition() instanceof And)){
						conditions.add(r.getCondition());
					}
					conditions.addAll(r.getCondition().backwardChain(kb));//RECURSION (find the conditions that lead to this rule!)
					usedRules.add(r);
				}
			}
		}else{
			while(rs.hasNext()){
				Rule r = rs.next();
				if(r.hasConsequence(name, value)){
					if(!(r.getCondition() instanceof And)){
						conditions.add(r.getCondition());
					}
					conditions.addAll(r.getCondition().backwardChain(kb));//RECURSION (find the conditions that lead to this rule!)
					usedRules.add(r);
				}
			}
		}
		kb.getRules().removeAll(usedRules);
		return conditions;
	}
	
	@Override
	public String toString(){
		if(negative){
			return "(" + name + " != " + value + ")";
		}
		return "(" + name + " == " + value + ")";
	}

	@Override
	public void add(TruthState ts) {
		//Hacky. Doesn't actually do anything, because it is the base case.
		//Required however to have XMLReader be able to call it.
	}

	@Override
	public double factsUsefulness(HashMap<String,String> facts) {
		//if this fact is in the consequences it is useful
		if(negative){
			if(facts.get(name) != null && !facts.get(name).equals(value)){
				return 1.0;
			}else{
				return 0.0;
			}
		}else{
			if(facts.get(name) != null && facts.get(name).equals(value)){
				return 1.0;
			}else{
				return 0.0;
			}
		}
	}

}
