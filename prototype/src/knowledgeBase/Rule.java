package knowledgeBase;

import java.util.Enumeration;
import java.util.Hashtable;

public class Rule {
	
	private Hashtable<String,String> conditions = new Hashtable<String,String>();//TODO: only OR right now
	private Hashtable<String,String> consequences = new Hashtable<String,String>();//only AND
	
	public Rule() {
		this.conditions = new Hashtable<String,String>();
		this.consequences = new Hashtable<String,String>();
	}

	public boolean satisfies(Hashtable<String,String> facts){
		Enumeration<String> it = conditions.keys();
		while (it.hasMoreElements()){
			String cur = it.nextElement();//iterate over conditions
			if (facts.get(cur) != null && facts.get(cur) == conditions.get(cur)){
				return true;//OR-logic: return if any condition is true
			}
		}
		return false;
	}
	
	public Hashtable<String,String> apply(Hashtable<String,String> facts){
		//return a new version of facts that has the consequences of this rule in it
		assert(satisfies(facts));//Just to be sure, may be removed (TODO)
		Hashtable<String,String> next = new Hashtable<String,String>(facts);
		Enumeration<String> it = consequences.keys();
		while (it.hasMoreElements()){
			String cur = it.nextElement();
			next.put(cur, consequences.get(cur));
		}
		return next;
	}
	
	public void addCondition(String factName, String factValue){
		conditions.put(factName, factValue);
	}
	
	public void addConsequence(String factName, String factValue){
		consequences.put(factName, factValue);
	}
	
	public String toString(){
		String s = "";
		Enumeration<String> it = conditions.keys();
		while (it.hasMoreElements()){
			String cur = it.nextElement();
			s += "(" + cur + "==" + conditions.get(cur) + ")";
			if (it.hasMoreElements())
				s += " OR ";
		}
		s += " --> ";
		it = consequences.keys();
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
