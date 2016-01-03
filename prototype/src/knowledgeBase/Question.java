package knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

public class Question {

	public class Option{
		public String description;
		public Hashtable<String,String> consequences;
		//the consequences are basically just a small knowledge base, which can be merged with the known facts.
		
		public Option(String description){
			this(description,new Hashtable<String,String>());
		}
		
		public Option(String description, Hashtable<String,String> consequences) {
			this.description = description;
			this.consequences = consequences;
		}
		
		public void addConsequence(String factName, String factValue){
			consequences.put(factName, factValue);
		}
		
		@Override
		public String toString(){
			return description + " ==> " + consequences.toString();
		}
	}
	
	private String question;
	private ArrayList<Option> options;
	
	public Question(){
		this("UNDEFINED");//Default to warning string
	}
	
	public Question(String question){
		this.question = question;
		this.options = new ArrayList<Option>();
	}
	
	//----
	
	public void addOption(String description, Hashtable<String,String> consequences){
		options.add(new Option(description, consequences));
	}
	
	public int leadsToGoalsTimes(KnowledgeBase kb){
		//returns whether this question can assign the desired value to factName
		int cnt = 0;
		Iterator<Option> opsIt = options.iterator();
		while (opsIt.hasNext()){
			//Iterate over all options
			Option cur = opsIt.next();
			Enumeration<String> factNames = cur.consequences.keys();
			while(factNames.hasMoreElements()){
				//Iterate over its consequences
				String fact = factNames.nextElement();
				if (kb.getGoals().get(fact) != null && kb.getGoals().get(fact).equals(cur.consequences.get(fact))){
					//If the consequence is a goal
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
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer(question + ":\n");
		Iterator<Option> ops = options.iterator();
		while(ops.hasNext()){
			Option o = ops.next();
			s.append('\t' + o.toString() + '\n');
		}
		return s.toString();
	}
	
}
