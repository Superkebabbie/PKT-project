import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;

/**
 * Copyright 'n stuffs by:
 * @author Joost Doornkamp
 * @author Jeroen Langhorst
 * @author Herman Groenbroek
 *
 */


/* PROGRAMMING NOTES
 * 
 * 'mainPanel' and 'progress' could very well be their own classes extending 'JPanel'
 * Actually, all of the code may very well be revised soon - this is a simple Hello World program
 * 
 */

public class Framework {
	// 
	static JFrame frame = new JFrame("Hello World"); // This is the main frame in which we find panels
	static JPanel mainPanel = new JPanel(); // This panel will update with questions 
	static JPanel progress = new JPanel(); // This panel will show the progress during the questionnaire
	
	public static void main(String[] args){
		// Set the program's 'Look And Feel' according to the OS it is used on
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.err.println("Invalid LookAndFeel, go complain to the programmers!");
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("Invalid LookAndFeel, go complain to the programmers!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Invalid LookAndFeel, go complain to the programmers!");
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Invalid LookAndFeel, go complain to the programmers!");
			e.printStackTrace();
		}
		
		// 'frame' description
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setPreferredSize(new Dimension(screenSize.width * 2 / 3, screenSize.height * 2 / 3));
		
		// 'mainPanel' description with JLabel
		JLabel helloLabel = new JLabel();
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date); 
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour < 12)
			helloLabel.setText("Good morning, world!");
		else if(hour < 18)
			helloLabel.setText("Good afternoon, world!");
		else
			helloLabel.setText("Good evening, world!");
		
		// Finalizing 'frame'
		mainPanel.add(helloLabel);
		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
}
