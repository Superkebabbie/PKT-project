package inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import knowledgeBase.Question;
import knowledgeBase.Question.Option;
import knowledgeBase.Rule;
import tree.Leaf;
import tree.Node;

public abstract class SolveEngine {

	public static Node solve(String goalName, String goalValue, ArrayList<Question> questions, ArrayList<Rule> rules){
		return solveRec(goalName, goalValue, new Hashtable<String,String>(), questions, rules);
	}

	private static Node solveRec(String goalName, String goalValue, Hashtable<String,String> facts, ArrayList<Question> questions, ArrayList<Rule> rules){
		//facts is the facts knowledge base, where the key is the name and the value is the value of a fact.
		facts = forwardChain(rules, facts);//update everything that is derivable

		if (facts.get(goalName) != null && facts.get(goalName).equals(goalValue)){
			return new Leaf("SUCCES!");
		}
		if (facts.get(goalName) != null){
			//A value was assigned to the fact, but not the right one, stop.
			return new Leaf("INCORRECT, FAIL...");
		}
		if (questions.size() == 0){
			//No more questions to ask
			return new Leaf("OUT OF QUESTIONS, FAIL...");
		}

		Question bestQuestion = findBestQuestion(goalName, goalValue, questions);//find the best question, it will be asked in this node
		HashMap<String,Node> branches = new HashMap<String,Node>(questions.size());//this will hold the rest of the tree following each option's selection

		Iterator<Option> opsIt = bestQuestion.getOptions().iterator();
		while(opsIt.hasNext()){ //Iterate over all options
			Option o = opsIt.next();
			ArrayList<Question> questionAsked = new ArrayList<Question>(questions);//copy arraylist
			questionAsked.remove(bestQuestion);//remove the question from it.
			Node consequentialNode = solveRec(goalName,goalValue,o.select(facts),questionAsked, new ArrayList<Rule>(rules));//<== RECURSION
			branches.put(o.description, consequentialNode);
		}

		return new Node(bestQuestion.getQuestion(), branches);
	}

	private static Question findBestQuestion(String goalName, String goalValue, ArrayList<Question> questions){
		//find the best question to ask - based on how many option give the desired value.
		Question best;
		int bestCnt;
		Iterator<Question> qs = questions.iterator();
		if(qs.hasNext()){
			//init best
			best = qs.next();
			bestCnt = best.leadsToGoalTimes(goalName, goalValue); 
		}else{
			return null;
		}
		while(qs.hasNext()){
			Question next = qs.next();
			int nextCnt = next.leadsToGoalTimes(goalName, goalValue);
			if (nextCnt > bestCnt){
				best = next;
				bestCnt = nextCnt;
			}
		}
		return best;

	}

	private static Hashtable<String,String> forwardChain(ArrayList<Rule> rules, Hashtable<String,String> facts){
		//Derive everything that is derivable right now
		Hashtable<String,String> next = new Hashtable<String,String>(facts); //the updated kb
		boolean hasApplied;
		
		do {
			Iterator<Rule> it = rules.iterator();
			ArrayList<Rule> toRemove = new ArrayList<Rule>();
			hasApplied = false;
			while(it.hasNext()){
				Rule r = it.next();
				if (r.satisfies(next)){
					next = r.apply(next);
					toRemove.add(r);//rule has been used, can be removed! Can't remove while iterating though!
					hasApplied = true;
					System.out.println("Inferred: " + r);
				}
			}
			rules.removeAll(toRemove);
		} while(hasApplied);//if nothing has been changed, we are done. If one iteration is done and facts contains new facts, new derivables may surface!
		return next;
	}




}
