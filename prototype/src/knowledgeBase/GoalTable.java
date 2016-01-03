package knowledgeBase;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

public class GoalTable {
	
	private Hashtable<String,Hashtable<String,String>> table; 
	//In goals, one name may have multiple goal values.
	//This is represented by a table within a table: the first key is the name.
	//The second key is a corresponding value, and the value of the inner table is the description.
	
	public GoalTable(){
		this.table = new Hashtable<String, Hashtable<String,String>>();
	}
	
	public GoalTable(GoalTable gt){
		this.table = new Hashtable<String,Hashtable<String,String>>(gt.table);
	}
	
	public void addGoal(String name, String value, String description){
		if(table.containsKey(name)){
			table.get(name).put(value,description);
		}else{
			Hashtable<String,String> val = new Hashtable<String,String>(1);
			val.put(value,description);
			table.put(name, val);
		}
	}
	
	public Set<String> get(String name){
		//return the possible goal values
		if (table.containsKey(name)){
			return table.get(name).keySet();
		} else {
			return null;
		}
		
	}
	
	public String getDescription(String name, String value){
		return table.get(name).get(value);
	}
	
	public void removeGoal(String name){
		table.remove(name);
	}
	
	public Enumeration<String> getNames(){
		return table.keys();
	}
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer();
		Enumeration<String> names = table.keys();
		while (names.hasMoreElements()){
			String next = names.nextElement();
			Enumeration<String> values = table.get(next).keys();
			while(values.hasMoreElements()){
				String val = values.nextElement();
				s.append(next + " == " + val + " (" + table.get(next).get(val) + '\n');
			}
		}
		return s.toString();
	}
	
}
