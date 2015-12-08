package tree;

import java.util.Iterator;
import java.util.Scanner;

public class TreeNavigator {
	//Runs through a question tree
	public TreeNavigator(){}

	public void run(Tree t){
		Node current = t.getRoot();
		while (!(current instanceof Leaf)){
			System.out.println('"' + current.getQuestion() + '"');

			//Print all answers
			Iterator<String> answers = current.getAnswers().iterator();
			int optionNumber = 0;
			while (answers.hasNext()){
				System.out.println(optionNumber++ + ": " + answers.next());
			}

			//Scan user input
			int choice = 0;
			
			Scanner scan = new Scanner(System.in);
			do {
				System.out.print("==> Your choice? ");

				try {
					choice = Integer.parseInt(scan.next());
				} catch (NumberFormatException e){
					System.out.println("Input not a number!");
					choice = Integer.MAX_VALUE;
				}

				if (choice >= current.getAnswers().size()){
					System.out.println("Not a valid input (range=[0," + (current.getAnswers().size()-1) + "]), try again...");
				}
			} while (choice >= current.getAnswers().size());
			scan.close();
			
			//Go to user selected node
			current = current.getSuccessor(current.idxToKey(choice));
		}
		//Arrived at leaf
		System.out.println("ARRIVED AT CONCLUSION:");
		System.out.println(current.getQuestion());
	}

}
