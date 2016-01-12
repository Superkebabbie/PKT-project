package tree;

public class Leaf extends Node {

	public Leaf(String desc){
		super(desc);//we use the question field of the superclass to store the text.
	}
	
	@Override
	public String serialize(int tabCount) {
		String json = "{" + "\"End\": \"" + getQuestion() + "\"}";
		
		return json;
	}
}
