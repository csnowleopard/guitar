package edu.umd.cs.guitar.sandbox;

import javax.swing.JFrame;

public class WireframeDriver {

	public static void main(String[] args) {
		WireframeDriver d = new WireframeDriver();
	}

	  public WireframeDriver(){
		  JFrame frame = new JFrame("Wireframe Test");
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  frame.getContentPane().add(new Wireframe());
		  frame.setSize(500,500);
		  frame.setVisible(true);  
	  }

	
	

}
