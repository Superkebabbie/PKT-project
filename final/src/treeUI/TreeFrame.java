package treeUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import tree.Node;
import tree.Tree;

@SuppressWarnings("serial")
public class TreeFrame extends JFrame implements Observer, KeyListener {
	
	private TreePanel treePanel;
	private TreeModel treeModel;
	private String filePath;
	private JLabel textLabel;
	private JMenuItem removeVertexItem;
	private JMenuItem addEdgeItem;
	private JMenuItem removeEdgeItem;
	private JMenuItem renameItem;
	private JMenuItem resizeItem;
	
	public TreeFrame() {
		// Creating the treeUI
		super("TreeUI v4.20");
		//createMenuBar();
		//createStatusBar();
		treePanel = new TreePanel();
		add(treePanel);
		//addEdgeItem.setEnabled(false);
		//removeVertexItem.setEnabled(false);
		//removeEdgeItem.setEnabled(false);
		//renameItem.setEnabled(false);
		// resizeItem.setEnabled(false);
		
		addKeyListener(this);
		enableSystemLooks();
		setSize(1200, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Creating a menu bar with the tabs "file", "edit", "view" and "help"
	 * each with graph options belonging to the menu, including fancy separators.
	 */
	private void createMenuBar(){
		// Initialising the menubar
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu view = new JMenu("View");
		JMenu help = new JMenu("Help");
		JMenuItem newItem = new JMenuItem("New");
		JMenuItem openItem = new JMenuItem("Open...");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		JMenuItem addVertexItem = new JMenuItem("Add Vertex");
		JMenuItem refreshItem = new JMenuItem("Refresh");
		JMenuItem searchItem = new JMenuItem("Search Vertex...");
		JMenuItem shortcutsItem = new JMenuItem("Shortcuts");
		JMenuItem aboutItem = new JMenuItem("About");

		this.addEdgeItem = new JMenuItem("Add Edge");
		this.removeVertexItem = new JMenuItem("Delete Vertex");
		this.removeEdgeItem = new JMenuItem("Delete Edge");
		this.renameItem = new JMenuItem("Rename...");
		this.resizeItem = new JMenuItem("Resize...");
		
		/**
		 * Actions for items under "file" in the menu bar.
		 */
		newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuNew();
            }
        });
		
		openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuOpen();
            }
        });
		
		saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuSave();
            }
        });
		
		saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuSaveAs();
            }
        });
		
		/**
		 * Actions for items under "edit" in the menu bar.
		 */
		
		addVertexItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuAddVertex();
            }
        });
		
		addEdgeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuAddEdge();
            }
        });
		
		removeVertexItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuRemoveVertex();
            }
        });
		
		removeEdgeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuRemoveEdge();
            }
        });
		
		renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuRename();
            }
        });
		
		resizeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuResize();
            }
        });
		
		/**
		 * Actions for items under "view" in the menu bar.
		 */
		
		searchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuSearch();
            }
        });
		
		refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuRefresh();
            }
        });
		
		/**
		 * Actions for items under "help" in the menu bar.
		 */
		
		shortcutsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuShortcuts();
            }
        });
		
		aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                menuAbout();
            }
        });
		
		// Finalising the menubar
		file.add(newItem);
		file.add(openItem);
		file.addSeparator();
		file.add(saveItem);
		file.add(saveAsItem);
		menuBar.add(file);
		edit.add(addVertexItem);
		edit.add(addEdgeItem);
		edit.add(removeVertexItem);
		edit.add(removeEdgeItem);
		edit.addSeparator();
		edit.add(resizeItem);
		edit.add(renameItem);
		menuBar.add(edit);
		view.add(searchItem);
		view.add(refreshItem);
		menuBar.add(view);
		help.add(shortcutsItem);
		help.add(aboutItem);
		menuBar.add(help);
		this.setJMenuBar(menuBar);
	}
	
	/**
	 * Creating a status bar at the bottom which shows text to inform
	 * the user about actions that are or should be performed
	 */
	private void createStatusBar(){
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(textPanel, BorderLayout.SOUTH);
		textPanel.setPreferredSize(new Dimension(getWidth(), 20));
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textLabel = new JLabel("");
		textLabel.setHorizontalAlignment(SwingConstants.LEFT);
		textPanel.add(textLabel);
	}
	
	/**
	 * Methods for actions under "file" in the menu bar.
	 */
	
	private void menuNew(){
		// TODO:
		// Make an actual new model
		setText("I'm not doing anything - fix me in the code!");
		//setText("New graph created!");
		
		repaint();
	}
	
	private void menuOpen(){
		// Use the JFileChooser to find the path to a user defined file to load
		setText("Opening file...");
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        ".txt files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	// Clear current model, only then load new model
	    	menuNew();
	    	filePath = chooser.getSelectedFile().getAbsolutePath();
	    	
	    	// TODO:
	    	// Perform the actual loading!
	    	setText("I'm not doing anything - fix me in the code!");
	    	//setText("Tree loaded!");
	    	
	    } else {
	    	setText("Opening cancelled!");
	    }
	    revalidate();
	    repaint();
	}
	
	private void menuSave(){
		if(filePath==null){
			// If no file has been opened yet, "save" button acts as "save as" button
			menuSaveAs();
		} else {

	    	// TODO:
	    	// Perform the actual saving!
			setText("I'm not doing anything - fix me in the code!");
	    	//setText("File successfully saved to " + filePath);
		}
	}
	
	private void menuSaveAs(){
		// Use the JFileChooser to find the path to a user defined file to load
		setText("Saving file...");
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        ".txt files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	// Make sure the file will be properly saved with extension
	    	String path = chooser.getSelectedFile().getAbsolutePath();
	    	if(!path.endsWith(".txt")){
	    		path += ".txt";
	    	}
	    	filePath = path;
	    	
	    	// TODO:
	    	// Perform the actual saving!
	    	
	    	//setText("File successfully saved to " + filePath);
	    } else {
	    	setText("Saving cancelled!");
	    }
	    revalidate();
	    repaint();
	}
	
	/**
	 * Methods for actions under "edit" in the menu bar.
	 */
	
	private void menuAddVertex(){
		// FIXME
		
	}
	
	private void menuAddEdge(){
		// FIXME
		
	}
	
	public void menuRemoveVertex(){
		// FIXME
		
	}
	
	public void menuRemoveEdge(){
		// FIXME
		
	}
	
	public void menuRename(){
		// FIXME
		
        revalidate();
        repaint();
	}
	
	public void menuResize(){
		
        revalidate();
        repaint();
	}
	
	/**
	 * Methods for actions under "view" in the menu bar.
	 */
	
	private void menuSearch(){
		// FIXME
		
	}
	
	private void menuRefresh(){
		// Simple tool to make sure everything that is drawn is refreshed
		revalidate();
		repaint();
		treePanel.revalidate();
		treePanel.repaint();
		setText("That was refreshing!");
	}
	
	/**
	 * Methods for actions under "help" in the menu bar.
	 */
	
	private void menuShortcuts(){
		JOptionPane.showMessageDialog(null, "CTRL + N:                   Create new graph\nCTRL + O:                   Open graph from file\nCTRL + S:                   Save graph\nCTRL + SHIFT + S:    Save graph as...\n\nCTRL + V:                   Add vertex\nCTRL + SHIFT + V:    Remove vertex\nDELETE:                      Remove vertex\nCTRL + E:                   Add edge\nCTRL + SHIFT + E:    Remove edge\n\nCTRL + R:                   Rename vertex\nCTRL + Z:                   Resize vertex\nCTRL + F:                   Search vertex\nF5:                               Refresh", "Shortcuts", JOptionPane.PLAIN_MESSAGE);
	}
	
	private void menuAbout(){
		JOptionPane.showMessageDialog(null, "This program was created by Joost Doornkamp, Jeroen Langhorst & Herman Groenbroek for the course \"Knowledge Technology Practical\".", "TreeUI ©, 2015-2016", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Getters and setters for manipulating the file path.
	 * @param path is the path to the file that was last opened; null if no file has been opened
	 */
	
	public void setFilePath(String path){
		filePath = path;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void setText(String text){
		textLabel.setText(text);
	}
	
	private void enableSystemLooks(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			System.err.println("Could not set LookAndFeel: forget about it.");
		}
		
	}
	
	public void setTree(Tree tree){
		treeModel = new TreeModel();
		if(tree != null){
			treeModel.setModel(tree);
			treePanel.setModel(treeModel);
		}
		else
			System.err.println("Tree given to the GUI model is empty!");
	}
	
	private void changeEditingMenuItems(Node node){
		if(node != null){
			// Node selected; show following editing items
			addEdgeItem.setEnabled(true);
			removeVertexItem.setEnabled(true);
			removeEdgeItem.setEnabled(true);
			renameItem.setEnabled(true);
			resizeItem.setEnabled(true);
		} else {
			// No node selected; following editing items are not relevant
			addEdgeItem.setEnabled(false);
			removeVertexItem.setEnabled(false);
			removeEdgeItem.setEnabled(false);
			renameItem.setEnabled(false);
			resizeItem.setEnabled(false);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// Tree Model is updated
		
		
		// The "Object", if not null, will always be a Node
		changeEditingMenuItems((Node) arg);
		
		revalidate();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Keyboard shortcuts are defined here
		if(e.isControlDown())
			treePanel.setCtrlPressed(true);
			
			/*
			if(e.isShiftDown()){
				// CTRL + SHIFT + <KEY>
				if(e.getKeyCode() == KeyEvent.VK_S){
					menuSaveAs();
					break control;
				} else if(e.getKeyCode() == KeyEvent.VK_E){
					menuRemoveEdge();
					break control;
				} else if(e.getKeyCode() == KeyEvent.VK_V){
					menuRemoveVertex();
					break control;
				}
				// CTRL + <KEY>
			} else if(e.getKeyCode() == KeyEvent.VK_N){
				menuNew();
			} else if(e.getKeyCode() == KeyEvent.VK_S){
				menuSave();
			} else if(e.getKeyCode() == KeyEvent.VK_O){
				menuOpen();
			} else if(e.getKeyCode() == KeyEvent.VK_V){
				menuAddVertex();
			} else if(e.getKeyCode() == KeyEvent.VK_F){
				menuSearch();
			} else if(e.getKeyCode() == KeyEvent.VK_E){
				menuAddEdge();
			} else if(e.getKeyCode() == KeyEvent.VK_R){
				menuRename();
			} else if(e.getKeyCode() == KeyEvent.VK_Z){
				menuResize();
			}
			// <KEY>
		} else if(e.getKeyCode() == KeyEvent.VK_DELETE){
			menuRemoveVertex();
		} else if(e.getKeyCode() == KeyEvent.VK_F5){
			menuRefresh();
		} else {
			setText("");
		}*/
	}

	@Override
	public void keyReleased(KeyEvent e) {
		treePanel.setCtrlPressed(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Empty: do nothing
	}

}
