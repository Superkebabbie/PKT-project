package inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import knowledgeBase.Question;
import knowledgeBase.Question.Option;
import tree.Leaf;
import tree.Node;

public class Engine {
	
	public Node solve(String goalName, String goalValue, Hashtable<String,String> facts, ArrayList<Question> questions){
		//facts is the facts knowledge base, where the key is the name and the value is the value of a fact.
		if (facts.get(goalName).equals(goalValue)){
			System.out.println("Goal Derived!");
			return new Leaf("SUCCES!");
		}
		if (facts.get(goalName) != null){
			//A value was assigned to the fact, but not the right one, stop.
			System.out.println("Incorrect value for Goal!");
			return new Leaf("INCORRECT, FAIL...");
		}
		if (questions.size() == 0){
			//No more questions to ask
			System.out.println("Out of Questions!");
			return new Leaf("OUT OF QUESTIONS, FAIL...");
		}
		
		Question bestQuestion = findBestQuestion(goalName, goalValue, questions);//find the best question, it will be asked in this node
		HashMap<String,Node> branches = new HashMap<String,Node>(questions.size());//this will hold the rest of the tree following each option's selection
		
		Iterator<Option> opsIt = bestQuestion.getOptions().iterator();
		while(opsIt.hasNext()){ //Iterate over all options
			Option o = opsIt.next();
			ArrayList<Question> questionAsked = new ArrayList<Question>(questions);//copy arraylist
			questionAsked.remove(bestQuestion);//remove the question from it.
			Node consequentialNode = solve(goalName,goalValue,o.select(facts),questionAsked);//<== RECURSION
			branches.put(o.description, consequentialNode);
		}
		
		return new Node(bestQuestion.getQuestion(), branches);
	}
	
	public Question findBestQuestion(String goalName, String goalValue, ArrayList<Question> questions){
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
	
	
	
	
}
