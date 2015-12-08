package tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import interfaces.Serializable;

public class Node implements Serializable{
	
	private String question;
	private HashMap<String,Node> answers;
	
	public Node(){
		this("?");
	}
	
	public Node(String question){
		this(question,new HashMap<String,Node>(3));
	}
	
	public Node(String question, HashMap<String,Node> answers) {
		this.question = question;
		this.answers = answers;
	}
	
	public Node getSuccessor(String key){
		return answers.get(key);
	}
	
	public void addAnswer(String text, Node n){
		//add an answer possibility
		answers.put(text,n);
	}
	
	public void setSuccessor(String key, Node n){
		//change the Node that is pointed to by key to n
		answers.put(key,n);
	}
	
	public void removeAnswer(String key){
		answers.remove(key);
	}
	
	public String idxToKey(int i){
		//Convert an index from a conventional array into the corresponding key
		Iterator<String> keys = answers.keySet().iterator();
		while (keys.hasNext() && i > 0){
			keys.next();
			i--;
		}
		if(i==0 && keys.hasNext())
			return keys.next();
		else
			return null;//index out of bounds!
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public Set<String> getAnswers() {
		return answers.keySet();
	}

	@Override
	public String serialize(int tabCount) {
		String json = "{\n";
		json += Tree.tabs(tabCount) + "Question: " + question + ",\n";
		
		json += Tree.tabs(tabCount) + "Answers: [";
		Iterator<String> keys = answers.keySet().iterator();
		++tabCount;
		while (keys.hasNext()){
			String cur = keys.next();
			json += "\n" + Tree.tabs(tabCount) + "{";
			json += "\n" + Tree.tabs(++tabCount) + "Text: " + cur + ',';
			json += "\n" + Tree.tabs(tabCount) + "Node: " + answers.get(cur).serialize(tabCount+1);
			json += "\n" + Tree.tabs(--tabCount) + "}";
			if (keys.hasNext())//append a comma if not last item
				json += ',';
		}
		--tabCount;
		
		json += "\n" + Tree.tabs(tabCount) + "]\n";
		
		json += Tree.tabs(--tabCount) + "}";
		
		return json;
	}
	
}
