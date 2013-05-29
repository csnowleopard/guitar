package edu.umd.cs.guitar.sandbox;

import javax.swing.JFrame;


public class GraphDriver extends JFrame{

	 
	private static final long serialVersionUID = 1L;

	public GraphDriver() {
	        setTitle("iGUITAR Visual");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        GraphTest graphEditor = new GraphTest(500, 500);
	        getContentPane().add(graphEditor);
	        pack();
	        setVisible(true);
	}
	
	public static void main(String[] args) {
		new GraphDriver();
	}

}
