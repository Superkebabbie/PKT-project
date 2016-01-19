package knowledgeBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import logic.Fact;
import logic.TruthState;

public class GoalTable {
	
	private HashMap<TruthState,String> table;
	private String defaultGoal = null;
	//all goals are a combination of some logical condition to be statisfied (a TruthState) and an associated solution (description; String)
	//there is also an optional default goal to which the inference can default if no goals could be proven. 
	
	public GoalTable(){
		this.table = new HashMap<TruthState,String>();
		this.defaultGoal = null;
	}
	
	public GoalTable(GoalTable gt){
		this.table = new HashMap<TruthState,String>(gt.table);
		this.defaultGoal = new String(gt.getDefaultGoal());
	}
	
	public void addGoal(TruthState ts, String description){
		table.put(ts, description);
	}
	
	public void addGoal(String name, String value, String description){
		table.put(new Fact(name, value), description);
	}
	
	public String getDescription(TruthState ts){
		return table.get(ts);
	}
	
	public void removeGoal(TruthState ts){
		table.remove(ts);
	}
	
	public void removeAll(Collection<TruthState> c){
		//remove all goals in c from the goals
		Iterator<TruthState> cs = c.iterator();
		while(cs.hasNext()){
			table.remove(cs.next());
		}
	}
	
	public ArrayList<TruthState> getGoals(){
		return new ArrayList<TruthState>(table.keySet());
	}
	
	public Iterator<TruthState> iterator(){
		return table.keySet().iterator();
	}
	
	public String getDefaultGoal() {
		return defaultGoal;
	}

	public void setDefaultGoal(String description) {
		this.defaultGoal = description;
	}
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer();
		Iterator<TruthState> keys = table.keySet().iterator();
		while (keys.hasNext()){
			TruthState next = keys.next();
			s.append(next + " ==> " + table.get(next) + "\n");
		}
		s.append("DEFAULT: " + defaultGoal + "\n");
		return s.toString();
	}
	
}
