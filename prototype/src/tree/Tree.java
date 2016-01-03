package tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Tree {
	//A tree is basically just the root (a node), but this class can be used to add general data to a tree
	//and can help separating when working with multiple trees (while normally everything would be a Node).
	
	private Node root;
	
	public Tree(){
		this.root = new Node();
	}
	
	public Tree(Node root){
		this.root = root;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	public static String tabs(int cnt){
		String s = "";
		for(int i = 0; i<cnt; ++i){
			s += '\t';
		}
		return s;
	}
	
	public void save(String path){
		//Save the tree to a file at path
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new File(path));
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		String json = "{\n\t\"Tree\": " + root.serialize(2) + "\n}";
		System.out.print(json);
		
		json = json.replaceAll("\n", System.lineSeparator()); //update the newline character to system dependent line separator.
		out.write(json);
		out.flush();
		
		out.close();
	}
}
