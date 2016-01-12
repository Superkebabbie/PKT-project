package knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import knowledgeBase.Question.Option;
import logic.TruthState;
import tree.Leaf;
import tree.Node;

public class KnowledgeBase {

	private GoalTable goals;					//facts that are to be proven. Look at GoalTable for more details.
	private Hashtable<String,String> facts;		//facts that are known. String/String (name/value) pairs.
	private ArrayList<Question> questions;		//questions that can be asked. Look at Question for more details.
	private ArrayList<Rule> rules;				//rules that can be applied. Look at Rule for more details. 
	private LinkedHashMap<TruthState,String> comments; //comments that are added to a final answer whenever it applies.
	
	public KnowledgeBase() {
		this.goals = new GoalTable();
		this.facts = new Hashtable<String,String>();
		this.questions = new ArrayList<Question>();
		this.rules = new ArrayList<Rule>();
		this.comments = new LinkedHashMap<TruthState,String>();
	}
	
	public KnowledgeBase(KnowledgeBase kb){
		//copy constructor
		this.goals = new GoalTable(kb.goals);
		this.facts = new Hashtable<String,String>(kb.facts);
		this.questions = new ArrayList<Question>(kb.questions);
		this.rules = new ArrayList<Rule>(kb.rules);
		this.comments = new LinkedHashMap<TruthState,String>(kb.comments);
	}
	
	public KnowledgeBase(String xmlFilePath){
		this(XMLReader.read(xmlFilePath));
	}
	
	
	public void addGoal(String name, String value, String description){
		//add a goal
		goals.addGoal(name, value, description);
	}
	
	public void removeGoal(String name){
		//remove a goal
		goals.removeGoal(name);
	}
	
	public void addComment(TruthState condition, String text){
		//add a comment
		comments.put(condition, text);
	}
	
	public boolean goalProven(String name){
		//return whether a specific goal is proven (correct value assigned)
		return facts.get(name) != null && goals.get(name).contains(facts.get(name));
	}
	
	public boolean goalDisproven(String name){
		//return whether a specific goal is disproven (wrong value assigned)
		return facts.get(name) != null && !goals.get(name).contains(facts.get(name));
	}
	
	public String firstGoalDone(){
		//Return the first goal that is proven, or null if none are.
		Enumeration<String> goalsIt = goals.getNames();
		while (goalsIt.hasMoreElements()){
			String name = goalsIt.nextElement();
			if (goalProven(name)){
				return name;
			}
		}
		return null;
	}
	
	public Hashtable<String,String> allGoalsDone(){
		//Return all goals that are proven, can be empty table.
		Enumeration<String> goalsIt = goals.getNames();
		Hashtable<String,String> goalsProven = new Hashtable<String,String>(1);
		while (goalsIt.hasMoreElements()){
			String name = goalsIt.nextElement();
			if (goalProven(name)){
				goalsProven.put(name, facts.get(name));
			}
		}
		return goalsProven;
	}
	
	public boolean allGoalsDisproven(){
		//return true if no more goals are valid (have a wrong value assigned)
		Enumeration<String> goalsIt = goals.getNames();
		while (goalsIt.hasMoreElements()){
			String name = goalsIt.nextElement();
			if (!goalDisproven(name)){
				return false;
			}
		}
		return true;
	}

	public void addQuestion(Question q){
		//add a question to the knowledge base
		questions.add(q);
	}
	
	public void removeQuestion(Question q){
		//remove a question from the knowledge base
		questions.remove(q);
	}
	
	public void selectAction(Option o){
		//if an action is selected, you can apply its consequences
		Enumeration<String> consequences = o.consequences.keys();
		while (consequences.hasMoreElements()){
			String cName = consequences.nextElement();
			addFact(cName, o.consequences.get(cName));
		}
	}
	
	public void putFact(String name, String value){
		//add a fact or overwrite an existing fact
		facts.put(name, value);
	}
	
	public void addFact(String name, String value){
		//adds a fact if that fact wasn't already defined, else do nothing
		if (facts.get(name) == null){
			facts.put(name,value);
		}else{
			System.err.println("Attempted to add existing fact: " + name);
		}
	}
	
	public void removeFact(String name){
		//remove a fact from the knowledge base
		facts.remove(name);
	}
	
	public String getFactValue(String name){
		//return the value of a fact
		return facts.get(name);
	}
	
	public void addRule(Rule r){
		//add a rule to the knowledge base
		rules.add(r);
	}
	
	public void removeRule(Rule r){
		//remove a rule from the knowledge base
		rules.remove(r);
	}
	
	public GoalTable getGoals() {
		return goals;
	}
	
	public Hashtable<String, String> getFacts() {
		return facts;
	}

	
	//--- SOLVING ------
	public Node solve(){
		//facts is the facts knowledge base, where the key is the name and the value is the value of a fact.

		forwardChain();//update everything that is derivable

		if (firstGoalDone() != null){
			//A goal has been proven! Check whether more goals are as well, before reporting.
			Hashtable<String,String> goalsProven = allGoalsDone();
			StringBuffer s = new StringBuffer("");
			Enumeration<String> goalsEn = goalsProven.keys();
			while (goalsEn.hasMoreElements()){
				String name = goalsEn.nextElement();
				s.append(goals.getDescription(name, facts.get(name)));
				if (goalsEn.hasMoreElements())
					s.append(", ");
			}
			
			return new Leaf(s.toString() + applicableComments());
		}
		if (allGoalsDisproven()){
			//A value was assigned to the goal facts, but not the right one, stop.
			if(goals.getDefaultGoal() != null){
				return new Leaf(goals.getDefaultGoal());
			}
			//If no default goal was placed return a 'default' fail message.
			return new Leaf("I ran out of options, I don't know what to do...");
		}
		if (questions.size() == 0){
			//No more questions to ask
			if(goals.getDefaultGoal() != null){
				return new Leaf(goals.getDefaultGoal());
			}
			return new Leaf("I ran out of questions, I don't know what to do...");
		}
		Question bestQuestion = findBestQuestion();//find the best question, it will be asked in this node
		//TODO: if no questions can lead to a goal, stop. However, you need to keep track of rules for this; backward chaining.
		
		LinkedHashMap<String,Node> branches = new LinkedHashMap<String,Node>(questions.size());//this will hold the rest of the tree following each option's selection

		Iterator<Option> opsIt = bestQuestion.getOptions().iterator();
		while(opsIt.hasNext()){ //Iterate over all options
			KnowledgeBase next = new KnowledgeBase(this); //copy the knowledge base
			next.removeQuestion(bestQuestion); //remove the asked question from it.
			Option o = opsIt.next();
			next.selectAction(o); //update facts to new facts given by the selected option
			branches.put(o.description, next.solve());//<== RECURSION
		}

		return new Node(bestQuestion.getQuestion(), branches);
	}

	private Question findBestQuestion(){
		//find the best question to ask - based on how ratio of many options give the desired value.
		//returns null if no useful questions exist
		Question best = null;
		double bestFitness = 0;
		Iterator<Question> qs = questions.iterator();
		while(qs.hasNext()){
			Question next = qs.next();
			double fitness = next.leadsToGoalsTimes(this)/(double) next.getOptions().size();
			if (fitness > bestFitness){
				best = next;
				bestFitness = fitness;
			}
		}
		if (best == null)
			return questions.get(0);
		else
			return best;

	}

	private void forwardChain(){
		//Derive everything that is derivable right now
		boolean hasApplied;
		
		do {
			Iterator<Rule> it = rules.iterator();
			ArrayList<Rule> toRemove = new ArrayList<Rule>(2);
			hasApplied = false;
			while(it.hasNext()){
				Rule r = it.next();
				if (r.satisfies(this)){
					r.apply(this);
					toRemove.add(r);//rule has been used, can be removed! Can't remove while iterating though!
					hasApplied = true;
				}
			}
			rules.removeAll(toRemove);
		} while(hasApplied);//if nothing has been changed, we are done. If one iteration is done and facts contains new facts, new derivables may surface!
	}
	
	private String applicableComments(){
		StringBuilder s = new StringBuilder();
		Iterator<TruthState> conditions = comments.keySet().iterator();
		while(conditions.hasNext()){
			TruthState cur = conditions.next();
			if(cur.valuate(this)){
				s.append("\n\n" + comments.get(cur));
			}
		}
		return s.toString();
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("Knowledge Base:\n");
		s.append("> Goals:\n");
		s.append(goals.toString());
		
		s.append("> Facts:\n");
		Enumeration<String> keys = facts.keys();
		while(keys.hasMoreElements()){
			String name = keys.nextElement();
			s.append(name + " == " + facts.get(name) + '\n');
		}
		
		s.append("> Questions:\n");
		Iterator<Question> qs = questions.iterator();
		while(qs.hasNext()){
			s.append(qs.next().toString());
		}
		
		s.append("> Rules:\n");
		Iterator<Rule> rs = rules.iterator();
		while(rs.hasNext()){
			s.append(rs.next().toString() + '\n');
		}	
		
		s.append("> Comments:\n");
		Iterator<TruthState> cs = comments.keySet().iterator();
		while(cs.hasNext()){
			TruthState t = cs.next();
			s.append(t + " --> \"" + comments.get(t) + "\"");
		}
		
		return s.toString(); 
	}
}
