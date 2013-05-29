package edu.umd.cs.guitar.eventhandlers;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import edu.umd.cs.guitar.gui.GraphBuilder;
import edu.umd.cs.guitar.gui.WindowTestNode;
import edu.umd.cs.guitar.helper.FileImporter;
import edu.umd.cs.guitar.helper.PrefixTree;
import edu.umd.cs.guitar.helper.TestCaseNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>WindowTestNodeEventHandler</b> is a handler for the WindowTestNode that just zooms in on a clicked WindowTestNode despite
 * what part of the node is click (ie overlay information) but the edges have a different handler.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class WindowTestNodeEventHandler extends PBasicInputEventHandler{

	/** The canvas where the WindowTestNode is located.*/
	private PCanvas can;
	/** The file Importer*/
	private FileImporter input;
	/** The list model*/
	private DefaultListModel listModel;
	/** The graph builder*/
	private GraphBuilder graph;
	
	/**
	 * The constructor.
	 * 
	 * @param can canvas that the WindowTestNode is displayed on
	 */
	public WindowTestNodeEventHandler(PCanvas can, GraphBuilder graph){
		super();
		this.can = can;
		this.input = graph.getFileImporter();
		this.listModel = graph.getListModel();
		this.graph = graph;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseClicked(PInputEvent e){
		super.mouseClicked(e);
		e.setHandled(true);
		Object temp = e.getPickedNode();
		WindowTestNode window;
		if(temp instanceof WindowTestNode){
			window = (WindowTestNode)temp;
		}else{
			window = (WindowTestNode)((PNode)temp).getParent();
		}
		if(GraphBuilder.getDeleteMode()){
			String name = "t" + getTestCaseName(window.getParentNode()) + "_" + window.getEventID();
			ArrayList<String> list = input.deleteTestCaseFiles(name);
			for(String fname : list){
				listModel.removeElement(fname);
			}
			graph.getTestCase().deleteTestCases(list);
			boolean redraw = false;
			ArrayList<String> curTestDisp = graph.getCurTestDisp();
			for(String s : list){
				if(curTestDisp.contains(s)){
					redraw = true;
					curTestDisp.remove(s);
				}
			}
			if(redraw){
				graph.getLayerTestCase().removeAllChildren();
				graph.getLayerEdgeTestCase().removeAllChildren();
			}
			if(redraw && curTestDisp.size() != 0){
				PrefixTree tree = graph.getTestCase().getPrefixTree(curTestDisp);
				graph.handleNewTree(tree);
			}
		}else{
			PBounds wbound = window.getGlobalFullBounds();
			wbound = new PBounds(wbound.getX()-40, wbound.getY()-40, wbound.getWidth()+80, wbound.getHeight()+80);
			can.getCamera().animateViewToCenterBounds(wbound, true, 1000);
		}
	}
	
	private String getTestCaseName(TestCaseNode start){
		String ret = "";
		if(start != null){
			TestCaseNode cur = start;
			do{
				ret = ret + "_" + cur.getEventID();
				cur = cur.getParent();
			}while(cur != null);
		}
		return ret;
	}
}
