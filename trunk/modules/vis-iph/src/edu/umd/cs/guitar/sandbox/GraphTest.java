package edu.umd.cs.guitar.sandbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.umd.cs.guitar.gen.*;
import edu.umd.cs.guitar.graphbuilder.EFGBuilder;
import edu.umd.cs.guitar.helper.iGUITARHelper;
import edu.umd.cs.guitar.parser.XMLParser;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;

public class GraphTest extends PCanvas {

	private static final long serialVersionUID = 1L;

	
	
	public GraphTest(int width, int height) {
	        /*setPreferredSize(new Dimension(width, height));
	        int numNodes = 50;
	        int numEdges = 50;

	        // Initialize, and create a layer for the edges
	        // (always underneath the nodes)
	        PLayer nodeLayer = getLayer();
	        PLayer edgeLayer = new PLayer();
	        getRoot().addChild(edgeLayer);
	        getCamera().addLayer(0, edgeLayer);
	        Random random = new Random();
	        
	        EFGBuilder gb = new EFGBuilder("file/gui.xml" ,"file/efg.xml");
	        
	        // populate all nodes
	        for (String one: gb.getAdjacencyList().keySet()){
	        	 float x = random.nextInt(width);
		         float y = random.nextInt(height);
		         PPath node = PPath.createEllipse(x, y, 30, 30);
		         node.addAttribute("edges", new ArrayList());
		         nodeLayer.addChild(node);
		
	        }
	        
	        // populate all nodes
	        int i = 0;
	        for (String one: gb.getAdjacencyList().keySet()){
	        	ArrayList<String> a = gb.getAdjacencyList().get(one);
	        	int j = 0;
	        	for (String two: a){
	        		PNode node1 = nodeLayer.getChild(i);
	 	            PNode node2 = nodeLayer.getChild(j);
	 	            PPath edge = new PPath();
	 	            ((ArrayList)node1.getAttribute("edges")).add(edge);
	 	            ((ArrayList)node2.getAttribute("edges")).add(edge);
	 	            edge.addAttribute("nodes", new ArrayList());
	 	            ((ArrayList)edge.getAttribute("nodes")).add(node1);
	 	            ((ArrayList)edge.getAttribute("nodes")).add(node2);
	 	            edgeLayer.addChild(edge);
	 	            updateEdge(edge);
	 	            
	 	            j++;
	        	}
	        	i++;
	        }*/
	        
	       
			
        	/* 
	        for (ComponentType component: ((ContainerType) guiStruc.getGUI().get(0).getContainer().getContents().getWidgetOrContainer().get(0)).getContents().getWidgetOrContainer()){
	        	 float x = random.nextInt(width);
		         float y = random.nextInt(height);
		         PPath node = PPath.createEllipse(x, y, 20, 20);
		         for (PropertyType prop: component.getAttributes().getProperty()){
		        	 if (prop.getName().equals("ID")){
		        		 node.setName(prop.getValue().get(0));
		        		 System.out.println(prop.getValue().get(0));
		        	 }
		         }
		         node.addAttribute("edges", new ArrayList());
		         nodeLayer.addChild(node);
	        }
	        */
	        
	        // Create some random nodes
	        // Each node's attribute set has an
	        // ArrayList to store associated edges
	        /*
	        for (int i = 0; i < numNodes; i++) {
	            float x = random.nextInt(width);
	            float y = random.nextInt(height);
	            PPath node = PPath.createEllipse(x, y, 20, 20);
	            node.addAttribute("edges", new ArrayList());
	            nodeLayer.addChild(node);
	        }
	        */
	        
	        // Create some random edges
	        // Each edge's attribute set has an
	        // ArrayList to store associated nodes
	        /*
	        for (int i = 0; i < numEdges; i++) {
	            int n1 = random.nextInt(numNodes);
	            int n2 = random.nextInt(numNodes);

	            // Make sure we have two distinct nodes.
	            while (n1 == n2) {
	                n2 = random.nextInt(numNodes);
	            }
	            
	            PNode node1 = nodeLayer.getChild(n1);
	            PNode node2 = nodeLayer.getChild(n2);
	            PPath edge = new PPath();
	            ((ArrayList)node1.getAttribute("edges")).add(edge);
	            ((ArrayList)node2.getAttribute("edges")).add(edge);
	            edge.addAttribute("nodes", new ArrayList());
	            ((ArrayList)edge.getAttribute("nodes")).add(node1);
	            ((ArrayList)edge.getAttribute("nodes")).add(node2);
	            edgeLayer.addChild(edge);
	            updateEdge(edge);
	        }
	        */
	        // Create event handler to move nodes and update edges
	        /*nodeLayer.addInputEventListener(new PDragEventHandler() {
	            {
	                PInputEventFilter filter = new PInputEventFilter();
	                filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
	                setEventFilter(filter);
	            }

	            public void mouseEntered(PInputEvent e) {
	                super.mouseEntered(e);
	                if (e.getButton() == MouseEvent.NOBUTTON) {
	                    e.getPickedNode().setPaint(Color.RED);
	                }
	            }
	            
	            public void mouseExited(PInputEvent e) {
	                super.mouseExited(e);
	                if (e.getButton() == MouseEvent.NOBUTTON) {
	                    e.getPickedNode().setPaint(Color.WHITE);
	                }
	            }
	            
	            protected void startDrag(PInputEvent e) {
	                super.startDrag(e);
	                e.setHandled(true);
	                e.getPickedNode().moveToFront();
	            }
	            
	            protected void drag(PInputEvent e) {
	                super.drag(e);
	                
	                ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
	                for (int i = 0; i < edges.size(); i++) {
	                   updateEdge((PPath) edges.get(i));
	                }
	            }
	        });*/
	    }
	 
	
	
	 public void updateEdge(PPath edge) {
	        // Note that the node's "FullBounds" must be used
	        // (instead of just the "Bounds") because the nodes
	        // have non-identity transforms which must be included
	        // when determining their position.


	        PNode node1 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(0);
	        PNode node2 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(1);
	        Point2D start = node1.getFullBoundsReference().getCenter2D();
	        Point2D end = node2.getFullBoundsReference().getCenter2D();
	        edge.reset();
	        edge.moveTo((float)start.getX(), (float)start.getY());
	        edge.lineTo((float)end.getX(), (float)end.getY());
	    }
	    
}
