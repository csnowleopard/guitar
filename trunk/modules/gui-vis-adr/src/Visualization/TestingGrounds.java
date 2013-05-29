package Visualization;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

	/* MousePanel.java

    This program adds the ability to respond to
    mouse events to the BasicPanel program.

    The mouse clicks are used to draw on the
    panel area using drawline instructions.

    mag-30Apr2008
*/

// Import the basic necessary classes.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TestingGrounds extends JPanel implements MouseListener{

    public TestingGrounds(){
        super();
        pointX=20;
        pointY=20;
        oldX=0;
        oldY=0;
        addMouseListener(this);   
    }

    int pointX, pointY, oldX, oldY;

    public void paintComponent(Graphics g){
    // Draw a line from the prior mouse click to new one.
        g.drawLine(oldX,oldY,pointX,pointY);
    }

    public void mouseClicked(MouseEvent mouse){
    // Copy the last clicked location into the 'old' variables.
        oldX=pointX;
        oldY=pointY;
    // Get the location of the current mouse click.
        pointX = mouse.getX();
        pointY = mouse.getY();
    // Tell the panel that we need to redraw things.
        repaint();
    }

/* The following methods have to be here to comply
   with the MouseListener interface, but we don't
   use them, so their code blocks are empty. */
    public void mouseEntered(MouseEvent mouse){ }   
    public void mouseExited(MouseEvent mouse){ }
    public void mousePressed(MouseEvent mouse){ }
    public void mouseReleased(MouseEvent mouse){ }

    public static void main(String arg[]){
        JFrame frame = new JFrame("MousePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250,250);

        TestingGrounds panel = new TestingGrounds();
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}


//	  public void paint(Graphics g) {
//	  
//	    Dimension d = this.getMaximumSize();
//	    g.setColor(Color.red);
//	    g.fillOval(0, 0, d.width, d.height);
//	      
//	  }
//	  
//	  public Dimension getMinimumSize() {
//	    return new Dimension(50, 100);  
//	  }
//
//	  public Dimension getPreferredSize() {
//	    return new Dimension(150, 300);  
//	  }
//
//	  public Dimension getMaximumSize() {
//	    return new Dimension(200, 400);  
//	  }
//}


//	public static void paint(Graphics graphics)
//	{
//        graphics.setColor(Color.white);
//        graphics.fillOval(50, 50, 100, 100);
//        graphics.setColor(Color.blue);
//        graphics.drawOval(50, 50, 100, 100);
//        graphics.drawRoundRect(10, 10, 100, 100, 5, 5);
//	}
//	public static void main(String[] args)
//	{
//		Canvas canvas = new Canvas();
//		JFrame frame = new JFrame();
//		frame.setSize(400, 400);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(canvas);
//		frame.setVisible(true);
//	}
//}
