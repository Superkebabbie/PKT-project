package treeUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import tree.Node;

public class TreePanel extends JPanel implements Observer, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	private TreeModel model;
	private ArrayList<TreePanel> panelListeners;
	private ArrayList<DrawingNode> drawingNodes;
	volatile private boolean isPanning = false;
	private int panX = 0, panY = 0, panXStart = 0, panYStart = 0, xStart = 500, yStart = 50; // panX and panY are changes in X and Y due to panning // xStart and yStart define the position of the root
	private int fontSize = 12;
	private boolean ctrlPressed = false;
	private int nodeBaseWidthDifference = 400;
	private final int NODE_HEIGHT_DIFFERENCE = 100;
	private final double NODE_WIDTH_DIFFERENCE_DECAY = 0.4;
	
	public TreePanel() {
		drawingNodes = new ArrayList<>();
		this.setModel(new TreeModel());
		model.addObserver(this);
		panelListeners = new ArrayList<>();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	public void addPanelListener(TreePanel listener){
		panelListeners.add(listener);
	}
	
	public ArrayList<TreePanel> getPanelListeners(){
		return panelListeners;
	}
	
	public void notifyListeners(){
		// Notify listener panels by giving them this updated panel
		for(TreePanel listener : panelListeners){
			listener.onNotify(this);
		}
	}
	
	public void onNotify(TreePanel panel){
		setModel(panel.getModel());
		revalidate();
		repaint();
	}
	
	public void setModel(TreeModel model){
		model.deleteObserver(this);
		this.model = model;
		model.addObserver(this);
		if(drawingNodes != null)
			drawingNodes.clear();
		setDrawingNodes(model.getModel().getRoot(), null, null, 0, 0, nodeBaseWidthDifference);
		repaint();
	}
	
	public TreeModel getModel(){
		return model;
	}
	
	// The class DrawingNode turns a tree node into an object with a position
	public class DrawingNode {
		public int x, y; // Starting positions for drawing the node
		public String q; // The question
		public String a; // The answer that leads to it
		public int lineFromX, lineFromY, lineToX, lineToY; // The position of the answer string
		public DrawingNode parent; // Reference to parent node
	}
	
	public ArrayList<DrawingNode> getDrawingNodes(){
		return drawingNodes;
	}
	
	private void setDrawingNodes(Node n, Node parent, DrawingNode drawingParent, int x, int y, double widthDifference){
		DrawingNode drawingNode = new DrawingNode();
		drawingNode.x = x;
		drawingNode.y = y;
		drawingNode.q = n.getQuestion();
		drawingNode.parent = drawingParent;
		if(parent != null){
			for(String a : parent.getAnswers()){
				if(parent.getSuccessor(a) == n)
					drawingNode.a = a;
			}
		}
		if(drawingNode.a == null)
			drawingNode.a = "?";
		drawingNodes.add(drawingNode);
		
		// Loop through all children of the node
		int numChildren = n.getAnswers().size(), count = 0;
		for(String answer : n.getAnswers()){
			setDrawingNodes(n.getSuccessor(answer), n, drawingNode, (int)(x - widthDifference + Math.round((count*(2 * widthDifference))/(numChildren-1))), y + NODE_HEIGHT_DIFFERENCE, widthDifference * NODE_WIDTH_DIFFERENCE_DECAY);
			count++;
		}
	}
	
	public void setCtrlPressed(boolean val){
		ctrlPressed = val;
	}
	
	// For every non-root node, paint the line to the node's parent
	private void paintLines(Graphics g, int x, int y){
		for(DrawingNode n : drawingNodes){
			if(n.parent != null){
				int lineFromX, lineToX, lineFromY, lineToY;
				lineFromX = n.parent.x + (g.getFontMetrics().stringWidth(n.parent.q) / 2);
				lineFromY = n.parent.y + ((g.getFontMetrics().getHeight() + 18) / 2);
				lineToX = n.x + (g.getFontMetrics().stringWidth(n.q) / 2);
				lineToY = n.y + ((g.getFontMetrics().getHeight() + 18) / 2);
				g.drawLine(x + lineFromX, y + lineFromY, x + lineToX, y + lineToY);
				g.drawString(n.a, (lineToX - g.getFontMetrics().stringWidth(n.a)/2) + x, n.y - (NODE_HEIGHT_DIFFERENCE / 6) + y);
			}
		}
	}
	
	// Paints all nodes, for each node refers to paintLines
	private void paintNodes(Graphics g, int x, int y){
		for(DrawingNode n : drawingNodes){
			String q = n.q;
			int width = g.getFontMetrics().stringWidth(q);
			g.setColor(Color.white);
			g.fillRect(n.x + x, n.y + y, width + 10 + (fontSize - 12), 30 + (fontSize - 12));
			g.setColor(Color.black);
			g.drawString(q, n.x + x + 5 + ((fontSize - 12)/2), n.y + y + 18 + (fontSize - 12));
		}
	}
	
	public void paintComponent(Graphics g){
		g.setFont(new Font("Arial", Font.PLAIN, fontSize));
		super.paintComponent(g);
		int x = xStart + panX, y = yStart + panY; // Update x and y with panning
		paintLines(g, x, y);
		paintNodes(g, x, y);
	}

	@Override
	public void update(Observable o, Object arg) {
		// The TreeModel is updated
		
		notifyListeners();
		revalidate();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Empty
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Empty
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Empty
	}

	
	
	@Override
	public void mousePressed(MouseEvent e) {
		// Empty
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isPanning = false;
		xStart += panX;
		yStart += panY;
		panX = 0;
		panY = 0;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!isPanning){
			panXStart = e.getX();
			panYStart = e.getY();
			isPanning = true;
		}
		panX = e.getX() - panXStart;
		panY = e.getY() - panYStart;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Empty
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(ctrlPressed){
			if(!(nodeBaseWidthDifference <= 50 && e.getWheelRotation()>0)){
				nodeBaseWidthDifference -= 50 * e.getWheelRotation();
				System.err.println(nodeBaseWidthDifference);
				drawingNodes.clear();
				setDrawingNodes(model.getModel().getRoot(), null, null, 0, 0, nodeBaseWidthDifference);
				repaint();
			}
		} else {
			if(!((fontSize < 8 && e.getWheelRotation()>0)||(fontSize > 72 && e.getWheelRotation()<0))){
				fontSize -= 2 * e.getWheelRotation();
				repaint();
			}
		}
	}
}