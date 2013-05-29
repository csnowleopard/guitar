package edu.umd.cs.guitar.helper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.umd.cs.guitar.graphbuilder.EFGBuilder;

/**
 * <b>PrefixTree</b> is the data structure read from when creating the prefix tree for 
 * the test case view. Instead of being in a traditional
 * tree form, I use arrays to specify the levels, then group the nodes by their parent on
 * that level, then of course store the nodes themselves. I also add empty nodes to make it so
 * that all parents on a row have the same number of children nodes. This will help with 
 * Spacing out the nodes in the visualization. In order to add root nodes use
 * the addRootNode method. For everything else use the addNode method. However, make sure to
 * move down a level when adding a node to a new level by using the moveDownRow method which 
 * basically updates the curRow counter. You have to manually move the counter down each time you
 * want to add a node to a lower level. Note the overall structure assumes that you only move down
 * to a new row once you are completely finished with the previous row.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class PrefixTree {

	private int maxHeight;
	private int maxWidth;
	private ArrayList<ArrayList<ArrayList<TestCaseNode>>> tree;
	private int curRow;
	private int workRow;
	private EFGBuilder efg;
	
	/**
	 * The constructor for the prefix tree
	 * 
	 * @param efg the EFG structure that allows the getting of the screen shot images
	 */
	public PrefixTree(EFGBuilder efg){
		maxHeight = 0;
		maxWidth = 0;
		tree = new ArrayList<ArrayList<ArrayList<TestCaseNode>>>();
		tree.add(new ArrayList<ArrayList<TestCaseNode>>());
		curRow = 0;
		workRow = -1;
		this.efg = efg;
	}
	
	
	/**
	 * Used to update the max Height and max width for the window so that i can space out the graphs properly.
	 * 
	 * @param height the height of the current node
	 * @param width the width of the current node
	 */
	private void updateMax(int height, int width){
		if(height > maxHeight){
			maxHeight = height;
		}
		if(width > maxWidth){
			maxWidth = width;
		}
	}
	
	/**
	 * When adding root nodes use this method and not the generic add because the root nodes should have the parent set to null
	 * and require a little special setup.
	 *
	 * @param windowID the window id
	 * @param eventID the event id
	 * @return the node that was just added to the prefix tree
	 */
	public TestCaseNode addRootNode(String windowID, String eventID){
		BufferedImage img = efg.getImageByView(windowID);
		updateMax(img.getHeight(), img.getWidth());
		TestCaseNode n = new TestCaseNode(null,windowID,eventID);
		n.setImg(img);
		ArrayList<TestCaseNode> root = new ArrayList<TestCaseNode>();
		root.add(n);
		tree.get(0).add(root);
		return n;
	}
	
	/**
	 * This is the basic add which assumes you are adding level by level so all nodes on the 
	 * same level are added at the same time. This is basically to make it easier for me to
	 * space the nodes out and start from the bottom. It also allows me to add place holders 
	 * for nodes that don't actually exist which helps me properly generate the view (ie the
	 * Spacing between nodes). So there are blank nodes whose only value is a reference to
	 * the parent node which is used just so i can traverse the tree upwards and determine
	 * spacing.
	 * 
	 * @param parent the parent node
	 * @param windowID the window id
	 * @param eventID the event id
	 * @return the node that was just added to the prefix tree
	 */
	public TestCaseNode addNode(TestCaseNode parent, String windowID, String eventID){
		ArrayList<ArrayList<TestCaseNode>> row = null;
		ArrayList<TestCaseNode> childrenGroup = null;
		TestCaseNode newNode = null;
		boolean foundGroup = false;
		if(curRow > tree.size() - 1){
			row = new ArrayList<ArrayList<TestCaseNode>>();
			ArrayList<ArrayList<TestCaseNode>> prev = tree.get(curRow-1);
			for(ArrayList<TestCaseNode> l : prev){
				for(TestCaseNode n : l){
					ArrayList<TestCaseNode> temp = new ArrayList<TestCaseNode>();
					temp.add(new TestCaseNode(n));
					n.addChildren(temp);
					row.add(temp);
				}
			}
			tree.add(row);
		}else{
			row = tree.get(curRow);
		}
		for(ArrayList<TestCaseNode> g : row){
			if(g.get(0).getParent().equals(parent)){
				childrenGroup = g;
				foundGroup = true;
				break;
			}
		}
		if(!foundGroup){
			int maxPerGroup = getMaxChildrenPerGroup(row);
			childrenGroup = new ArrayList<TestCaseNode>();
			for(int i = 0; i < maxPerGroup; i++){
				childrenGroup.add(new TestCaseNode(parent));
			}
			parent.addChildren(childrenGroup);
			row.add(childrenGroup);
		}
		newNode = findEmptyNode(childrenGroup);
		if(newNode != null){
			newNode.setParent(parent);
			newNode.setWindowID(windowID);
			newNode.setEventID(eventID);
		}else{
			newNode = new TestCaseNode(parent, windowID, eventID);
			childrenGroup.add(newNode);
			updateGroupSize(row, childrenGroup.size());
		}
		BufferedImage img = efg.getImageByView(windowID);
		newNode.setImg(img);
		updateMax(img.getHeight(), img.getWidth());
		return newNode;
	}
	
	/**
	 * This returns the maximum number of nodes per group (where the group is based on the
	 * parent node). It is used for updating the other groups with blank nodes.
	 * 
	 * @param row the row to check
	 * @return the max number of nodes in a group for that row
	 */
	private int getMaxChildrenPerGroup(ArrayList<ArrayList<TestCaseNode>> row){
		int max = 0;
		if(row.size() != 0){
			max = row.get(0).size();
		}
		return max;
	}
	
	/**
	 * Finds the first instance of a empty node in a group if any.
	 * 
	 * @param childrenGroup the group of nodes
	 * @return an empty node or null if no empty node
	 */
	private TestCaseNode findEmptyNode(ArrayList<TestCaseNode> childrenGroup){
		for(TestCaseNode n : childrenGroup){
			if(n.getWindowID() == null){
				return n;
			}
		}
		return null;
	}
	
	/**
	 * This is used to update the size of the other groups so that they all are the same
	 * size. Ie it appends groups with a smaller size with empty nodes.
	 * 
	 * @param row the row to check
	 * @param size the amount to add to each group if needed
	 */
	private void updateGroupSize(ArrayList<ArrayList<TestCaseNode>> row, int size){
		for(ArrayList<TestCaseNode> g : row){
			if(g.size() < size){
				int toAdd = size - g.size();
				TestCaseNode parent = g.get(0).getParent();
				for(int i = 0; i < toAdd; i++){
					g.add(new TestCaseNode(parent));
				}
			}
		}
	}
	
	/**
	 * Move the row counter down which is the direction you should always move 
	 * in when adding nodes to this tree structure.
	 */
	public void moveDownRow(){
		curRow++;
	}
	
	/**
	 * Getter for the whole tree so that it can be processes and turned into a visualization.
	 * 
	 * @return the tree structure
	 */
	public ArrayList<ArrayList<ArrayList<TestCaseNode>>> getTree(){
		return tree;
	}
	
	/**
	 * Grabs the rear of the tree decrementing the counter at each grab.
	 * The result is that each time one grabs the rear of the tree it will
	 * grab the row above the row returned in the previous grab.
	 * 
	 * @return the next row in the tree to be processed
	 */
	public ArrayList<ArrayList<TestCaseNode>> getRear(){
		if(workRow < 0){
			workRow = tree.size()-1;
		}
		ArrayList<ArrayList<TestCaseNode>> temp = tree.get(workRow);
		workRow--;
		return temp;
	}
	
	/**
	 * This gets number of levels in the tree.
	 * 
	 * @return number of levels in the tree
	 */
	public int size(){
		return tree.size();
	}
	
	/**
	 * This gets the max height of the nodes in the tree.
	 * 
	 * @return the max height of the nodes in the tree
	 */
	public int getHeight(){
		return maxHeight;
	}
	
	/**
	 * This gets the max width of the nodes in the tree.
	 * 
	 * @return the max width of the nodes in the tree
	 */
	public int getWidth(){
		return maxWidth;
	}
	
}
