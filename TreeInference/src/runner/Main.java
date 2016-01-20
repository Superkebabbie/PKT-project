package runner;

import java.io.File;

import knowledgeBase.KnowledgeBase;
import tree.Tree;
import treeUI.TreeFrame;

public class Main {
	
	public static void main(String[] args){
		if(args.length > 0){
			String path = args[0];
			
			File f = new File(path);
			if(f.exists()){
				if(!path.substring(path.length()-4).equals(".xml")){
					System.out.println("WARNING: File extension is not .xml! This program only supports knowledge bases in XML format");
				}

				long startTime = System.currentTimeMillis();
				
				KnowledgeBase kb = new KnowledgeBase(path);//"testkb.xml"
				System.out.println(kb);
				
				Tree decTree = new Tree(kb.solve());
				decTree.save(path.substring(0,path.length()-3) + "json");
				
				long stopTime = System.currentTimeMillis();
				
				if(args.length > 1 && Boolean.parseBoolean(args[1])){
					TreeFrame treeUI = new TreeFrame(); // Create the GUI for the tree
				    treeUI.setTree(decTree); // Add the tree to the GUI
				}
				
				System.out.println("\nDONE!\n(runtime " + (stopTime-startTime) + "ms)");
			}else{
				System.out.println("ERROR: File not found!");
			}
		}else{
			System.out.println("ERROR: first argument invalid!");
			System.out.println("Usage: java -m TreeInference <KB_PATH> [useGUI:true|false]");//TODO awaiting name
		}
	}
}
