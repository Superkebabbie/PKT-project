package tree;

public class Leaf extends Node {

	public Leaf(String desc){
		super(desc);
	}
	
	@Override
	public String serialize(int tabCount) {
		String json = "{" + "\"End\": \"" + getQuestion() + "\"}";
		
		return json;
	}
}
