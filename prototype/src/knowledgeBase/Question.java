package knowledgeBase;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

public class Question {

	public class Option{
		public String description;
		public Hashtable<String,String> consequences;
		//the consequences are basically just a small knowledge base, which can be merged with the known facts.
		
		public Option(String description, Hashtable<String,String> consequences) {
			this.description = description;
			this.consequences = consequences;
		}
		
		public Hashtable<String,String> select(Hashtable<String,String> kb){
			//applies all consequences to the knowledge base
			Hashtable<String,String> next = new Hashtable<String,String>(kb.size());
			Iterator<String> factNames = consequences.keySet().iterator();
			while (factNames.hasNext()) {
				String cur = factNames.next();
				next.put(cur, consequences.get(cur));
			}
			return next;
		}
	}
	
	private String question;
	private ArrayList<Option> options;
	
	public Question(String question){
		this.question = question;
		this.options = new ArrayList<Option>();
	}
	
	//----
	
	public void addOption(String description, Hashtable<String,String> consequences){
		options.add(new Option(description, consequences));
	}
	
	public boolean leadsToFact(String factName){
		//returns whether this question can assign a value to factName
		Iterator<Option> opsIt = options.iterator();
		while (opsIt.hasNext()){
			//Iterate over all options
			Option cur = opsIt.next();
			Iterator<String> factNames = cur.consequences.keySet().iterator();
			while(factNames.hasNext()){
				//Iterate over its consequences
				String fact = factNames.next();
				if (fact.equals(factName)){
					//is factName in the consequences?
					return true;
				}
			}
		}
		return false;
	}
	
	public int leadsToFactTimes(String factName){
		//returns whether this question can assign a value to factName
		Iterator<Option> opsIt = options.iterator();
		int cnt = 0;
		while (opsIt.hasNext()){
			//Iterate over all options
			Option cur = opsIt.next();
			Iterator<String> factNames = cur.consequences.keySet().iterator();
			while(factNames.hasNext()){
				//Iterate over its consequences
				String fact = factNames.next();
				if (fact.equals(factName)){
					//is factName in the consequences?
					++cnt;
				}
			}
		}
		return cnt;
	}
	
	public boolean leadsToGoal(String goalName, String goalValue){
		//returns whether this question can assign the desired value to factName
		Iterator<Option> opsIt = options.iterator();
		while (opsIt.hasNext()){
			//Iterate over all options
			Option cur = opsIt.next();
			Iterator<String> factNames = cur.consequences.keySet().iterator();
			while(factNames.hasNext()){
				//Iterate over its consequences
				String fact = factNames.next();
				if (fact.equals(goalName) && cur.consequences.get(fact).equals(goalValue)){
					//is factName in the consequences?
					return true;
				}
			}
		}
		return false;
	}
	
	public int leadsToGoalTimes(String goalName, String goalValue){
		//returns whether this question can assign the desired value to factName
		int cnt = 0;
		Iterator<Option> opsIt = options.iterator();
		while (opsIt.hasNext()){
			//Iterate over all options
			Option cur = opsIt.next();
			Iterator<String> factNames = cur.consequences.keySet().iterator();
			while(factNames.hasNext()){
				//Iterate over its consequences
				String fact = factNames.next();
				if (fact.equals(goalName) && cur.consequences.get(fact).equals(goalValue)){
					//is factName in the consequences?
					++cnt;
				}
			}
		}
		return cnt;
	}
	
	public void ask(){
		System.out.println("Q: " + question);
		int opNum = 0;
		Iterator<Option> ops = options.iterator();
		while(ops.hasNext()){
			System.out.println(opNum++ + ": " + ops.next().description);
		}
		Scanner scan = new Scanner(System.in);
		int choice = Integer.MAX_VALUE;
		do {
			System.out.print("==> Your choice? ");

			try {
				choice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e){
				System.out.println("Input not a number!");
				choice = Integer.MAX_VALUE;
			}

			if (choice >= options.size()){
				System.out.println("Not a valid input (range=[0," + (options.size()-1) + "]), try again...");
			}
		} while (choice >= options.size());
		scan.close();
	}
	
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public ArrayList<Option> getOptions() {
		return options;
	}
	public void setOptions(ArrayList<Option> options) {
		this.options = options;
	}
	
	
	
	
}
