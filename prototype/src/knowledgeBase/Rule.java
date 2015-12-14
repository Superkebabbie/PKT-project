package knowledgeBase;

import java.util.Enumeration;
import java.util.Hashtable;

import logic.TruthState;

public class Rule {
	
	private TruthState condition;
	private Hashtable<String,String> consequences;;//only AND
	
	public Rule() {
		this.condition = null;
		this.consequences = new Hashtable<String,String>();
	}

	public boolean satisfies(KnowledgeBase kb){
		return condition.valuate(kb);
	}
	
	public void apply(KnowledgeBase kb){
		//return a new version of facts that has the consequences of this rule in it
		assert(satisfies(kb));//Just to be sure, may be removed (TODO)
		Enumeration<String> it = consequences.keys();
		while (it.hasMoreElements()){
			String cur = it.nextElement();
			kb.addFact(cur, consequences.get(cur));
		}
	}
	
	public TruthState getCondition() {
		return condition;
	}

	public void setCondition(TruthState condition) {
		this.condition = condition;
	}

	public void addConsequence(String factName, String factValue){
		consequences.put(factName, factValue);
	}
	
	public String toString(){
		String s = "";
		s += condition.toString();
		s += " --> ";
		Enumeration<String> it = consequences.keys();
		while (it.hasMoreElements()){
			String cur = it.nextElement();
			s += "(" + cur + "==" + consequences.get(cur) + ")";
			if (it.hasMoreElements())
				s += " AND ";
		}
		return s;
	}
	
	public boolean leadsToGoal(String goalName, String goalValue){
		if (consequences.get(goalName) != null){
			return consequences.get(goalName).equals(goalValue);
		}
		return false;
	}
	
}
