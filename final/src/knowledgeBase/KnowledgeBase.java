package knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
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
		this.comments = kb.comments;//copy by reference! Comments should be immutable.
	}

	public KnowledgeBase(String xmlFilePath){
		this(XMLReader.read(xmlFilePath));
	}


	public void addGoal(String name, String value, String description){
		//add a goal
		goals.addGoal(name, value, description);
	}

	public void removeGoal(TruthState ts){
		//remove a goal
		goals.removeGoal(ts);
	}

	public void addComment(TruthState condition, String text){
		//add a comment
		comments.put(condition, text);
	}

	public String firstGoalDone(){
		//Return the description of the first goal that is proven, or null if none are.
		Iterator<TruthState> goalsIt = goals.iterator();
		while (goalsIt.hasNext()){
			TruthState g = goalsIt.next();
			if (g.proven(this)){
				return goals.getDescription(g);
			}
		}
		return null;
	}

	public ArrayList<String> allGoalsDone(){
		//Return all goals that are proven, can be empty table.
		Iterator<TruthState> goalsIt = goals.iterator();
		ArrayList<String> goalsProven = new ArrayList<String>(1);
		while (goalsIt.hasNext()){
			TruthState g = goalsIt.next();
			if (g.proven(this)){
				goalsProven.add(goals.getDescription(g));
			}
		}
		return goalsProven;
	}

	public boolean allGoalsDisproven(){
		//return true if no more goals are valid (have a wrong value assigned)
		boolean allGoalsDisproven = true;
		Iterator<TruthState> goalsIt = goals.iterator();
		ArrayList<TruthState> toRemove = new ArrayList<TruthState>();
		while (goalsIt.hasNext()){
			TruthState g = goalsIt.next();
			if (!g.disproven(this)){
				//this goal is not disproven, so we know there are still goals provable.
				allGoalsDisproven = false;
			}else{
				//this goal is disproven, no point in keeping it. We throw it out.
				toRemove.add(g);
			}
		}
		goals.removeAll(toRemove);
		return allGoalsDisproven;
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
		Iterator<String> consequences = o.consequences.keySet().iterator();
		while (consequences.hasNext()){
			String cName = consequences.next();
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

	public ArrayList<Rule> getRules(){
		return rules;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public LinkedHashMap<TruthState, String> getComments() {
		return comments;
	}

	//--- SOLVING ------
	public Node solve(){
		backwardChain();
		return solveRec();
	}
	
	public Node solveRec(){
		if(firstGoalDone() != null){
			//proven a goal!
			StringBuffer s = new StringBuffer("");
			Iterator<String> goalsProven = allGoalsDone().iterator();
			while (goalsProven.hasNext()){
				String desc = goalsProven.next();
				s.append(desc);
				if (goalsProven.hasNext())
					s.append(", ");
			}
			//System.out.println(this);
			return new Leaf(s.toString() + applicableComments());
		}
		if (allGoalsDisproven()){
			//A wrong value was assigned to the goal facts, but not the right one, stop.
			//System.out.println("\n" + this);
			if(goals.getDefaultGoal() != null){
				return new Leaf(goals.getDefaultGoal());
			}
			//If no default goal was placed return a 'default' fail message.
			return new Leaf("I ran out of options, I don't know what to do...");
		}

		Question bestQuestion = findBestQuestion();
		if (questions.size() == 0 || bestQuestion == null){
			//No more questions to ask
			//System.out.println("\n" + this);
			if(goals.getDefaultGoal() != null){
				return new Leaf(goals.getDefaultGoal());
			}
			return new Leaf("I ran out of questions, I don't know what to do...");
		}

		LinkedHashMap<String,Node> branches = new LinkedHashMap<String,Node>(questions.size());//this will hold the rest of the tree following each option's selection

		Iterator<Option> os = bestQuestion.getOptions().iterator();
		while(os.hasNext()){ //Iterate over all options
			KnowledgeBase next = new KnowledgeBase(this); //copy the knowledge base
			next.removeQuestion(bestQuestion); //remove the asked question from it.
			Option o = os.next();
			next.selectAction(o); //update facts to new facts given by the selected option
			branches.put(o.description, next.solveRec());//<== RECURSION
		}

		return new Node(bestQuestion.getQuestion(), branches);
	}

	private Question findBestQuestion(){
		//find the best question to ask - based on ratio of many options give the desired value.
		//returns null if no useful questions exist
		Question best = null;
		double bestFitness = 0;
		Iterator<Question> qs = questions.iterator();
		while(qs.hasNext()){
			Question q = qs.next();
			Iterator<Option> os = q.getOptions().iterator();
			double fitness = 0;
			while(os.hasNext()){
				Option o = os.next();
				Iterator<TruthState> gs = goals.iterator();
				while(gs.hasNext()){
					TruthState g = gs.next();
					fitness += g.factsUsefulness(o.getConsequences());
				}
			}
			if (fitness > bestFitness){
				best = q;
				bestFitness = fitness;
			}
		}
		return best;

	}

	private void backwardChain(){
		//derive all possible sub-goals that lead to a goal and add them to the goals
		//run once before solving!
		Iterator<TruthState> gs = new GoalTable(goals).iterator();//have to work on a copy, or else ConcurrentModExc.
		while (gs.hasNext()){
			TruthState g = gs.next();
			ArrayList<TruthState> subgoals = g.backwardChain(this);
			Iterator<TruthState> sgs = subgoals.iterator();
			while(sgs.hasNext()){
				//add all subgoals found to goals
				TruthState sg = sgs.next();
				goals.addGoal(sg, goals.getDescription(g));
			}
		}
		//After backward chaining, rules are no longer useful and can be cleared out!
	}

	private String applicableComments(){
		StringBuilder s = new StringBuilder();
		Iterator<TruthState> conditions = comments.keySet().iterator();
		while(conditions.hasNext()){
			TruthState cur = conditions.next();
			if(cur.proven(this)){
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
