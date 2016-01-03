package runner;

import knowledgeBase.KnowledgeBase;
import tree.Tree;

public class Main {
	
	public static void main(String[] args){
		
		/* TEST TREE GEN
		Tree t = new Tree(new Node("What do you think of the first question?")); //Create a tree with a root Node
		Leaf l1 = new Leaf("Hello World"); //Create leaf nodes
		Leaf l2 = new Leaf("Well sure.");
		
		Node n = new Node("How Come?");
		n.addAnswer("Because why not?", l2);
		n.addAnswer("Nevermind, it was good.", l1);
		
		t.getRoot().addAnswer("Good!", l1); //make answers in the root that point to the leaf node
		t.getRoot().addAnswer("Unexpected!", n);
		
		t.save("test.txt");
		
		*/
		
		/* A word from the author:
		 * The code provided here was made kind of hastily and experimentally, and now at the end we
		 * look back and think "dang, that is some confusing code". This is what you'll get as our
		 * prototype, but we'll likely rewrite most of this at some point. There are also several TODO-
		 * markers spread out over the code that indicate some sort of suboptimal solution which still have to
		 * be corrected. And - not a small point - we planned on making a backward chaining algorithm but
		 * kind of accidentally made a forward one, which we will keep for now as we discovered that backward
		 * chaining will require a bit more thought. The important thing is that now the prototype is finished
		 * we have developed a good or at least better idea of how the whole system should work.
		 * 
		 * The code below uses manual construction of a knowledge base and gives that to the solveEngine. The
		 * idea is to automate this from XML, but that we did not have time for yet. It does however export the
		 * tree that the solver constructs to JSON which will be readable by the website; this output is printed
		 * to the console as well as saved to test.txt (see below). To make a bit clearer how we construct the
		 * knowledge base everything below has been commented when it is first used, we hope that will help you
		 * understand.
		 * 
		 * - Joost, Herman & Jeroen
		 */
		
		/* TEST KB GEN
		
		KnowledgeBase kb = new KnowledgeBase();
		
		Question q = new Question("I am obvious!");//Create a question
		Hashtable<String,String> c = new Hashtable<String,String>();//create some consequences of an option
		c.put("END", "TRUE");
		q.addOption("Click me!", c);//add the consequences to the option (first argument is option text)
		c = new Hashtable<String,String>();//reset
		c.put("ACTIVATE_RULE", "TRUE");
		q.addOption("Or me!", c);//This option triggers a rule (see below) which also leads to END = TRUE
		c = new Hashtable<String,String>();
		c.put("END", "FALSE");
		q.addOption("But not me!", c);
		q.addOption("I give no useful information.", new Hashtable<String,String>());
		kb.addQuestion(q);
		
		Rule r = new Rule();//add a rule
		Or or = new Or();
		or.add(new Fact("ACTIVATE_RULE", "TRUE"));
		or.add(new Fact("ARBITRARY","WHYNOT"));
		r.setCondition(or);//if ACTIVATE_RULE is set to TRUE
		r.addConsequence("END", "TRUE");//then END may be set to TRUE
		kb.addRule(r);
		
		r = new Rule();
		r.setCondition(new Fact("SECOND", "HELP"));//The value can be any string
		r.addConsequence("END", "TRUE");
		kb.addRule(r);
		
		q = new Question("You are about to fail, want help?");
		c = new Hashtable<String,String>();
		c.put("SECOND", "HELP");
		q.addOption("Help me!", c);
		c = new Hashtable<String,String>();//no consequences
		q.addOption("I want to fail!", c);
		kb.addQuestion(q);
		
		q = new Question("Want to do this the hard way, or the easy way?");
		c = new Hashtable<String,String>();
		c.put("WAY", "HARD");
		q.addOption("Hard way!", c);
		c = new Hashtable<String,String>();
		c.put("WAY", "EASY");
		q.addOption("Easy way!", c);
		kb.addQuestion(q);
		
		kb.addGoal("END", "TRUE", "Ended the old fashion way!");
		kb.addGoal("WAY", "EASY", "Ended the new, easy way!");
		
		Tree decTree = new Tree(kb.solve()); //solve the created problem and store the tree
		decTree.save("test.json");//export to JSON (also echoes file)
		
		*/
		
		KnowledgeBase kb = new KnowledgeBase("knowledge-base-example.xml");
		System.out.println(kb);
		Tree decTree = new Tree(kb.solve());
		decTree.save("test.json");
		
		//TreeFrame treeUI = new TreeFrame(); // Create the GUI for the tree
		//treeUI.setTree(decTree); // Add the tree to the GUI
	}
}
