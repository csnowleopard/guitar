package Readers;

import java.util.ArrayList;
import java.util.HashMap;

import edu.umd.cs.piccolo.PLayer;

/**
 * This class is an object representation of a TestCase for the Android
 * GUITAR software.
 * 
 * @author Chris Carmel
 *
 */
public class TestCase {
	
	/**
	 * String title of the TestCase.
	 */
	private String title;
	
	/**
	 * TestCaseStep ArrayList representing the steps of this TestCase.
	 */
	private ArrayList<TestCaseStep> steps;
	
	/**
	 * Boolean representing the validity of this TestCase.
	 */
	private boolean valid;
	
	/**
	 * PLayer representing this TestCase on the PVisualizationCanvas.
	 */
	private PLayer selfLayer;
	
	/**
	 * Constructs an empty TestCase.
	 */
	public TestCase() {
		this.title = null;
		this.steps = new ArrayList<TestCaseStep>();
		this.valid = true;
		this.selfLayer = null;
	}
	
	/**
	 * Constructs and empty TestCase with the given title.
	 * 
	 * @param title		value to title this TestCase
	 */
	public TestCase(String title) {
		this.title = title;
		this.steps = new ArrayList<TestCaseStep>();
		this.valid = true;
		this.selfLayer = null;
	}
	
	/**
	 * Returns the title of this TestCase.
	 * 
	 * @return		the title of this TestCase
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Returns the TestCaseStep ArrayList of this TestCase.
	 * 
	 * @return		the TestCaseStep ArrayList of this TestCase
	 */
	public ArrayList<TestCaseStep> getSteps() {
		return steps;
	}
	
	/**
	 * Returns a boolean representing this TestCases validity.
	 * 
	 * @return		a boolean representing this TestCases validity
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Returns the PLayer representing this TestCase on the PVisualizationCanvas.
	 * 
	 * @return		the PLayer representing this TestCase on the PVisualizationCanvas
	 */
	public PLayer getSelfLayer() {
		return selfLayer;
	}
	
	/**
	 * Sets the title of this TestCase to this incoming value.
	 * 
	 * @param title		value to set this TestCase's title to
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets this TestCase's TestCaseStep ArrayList to the incoming value.
	 * 
	 * @param steps		value to set this TestCase's TestCaseStep ArrayList to
	 */
	public void setSteps(ArrayList<TestCaseStep> steps) {
		this.steps = steps;
	}
	
	/**
	 * Sets this TestCase's validity to the incoming value.
	 * 
	 * @param valid		value to set this TestCase's validity to
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	/**
	 * Sets the PLayer representing this TestCase to the incoming value.
	 * 
	 * @param selfLayer		value to set the PLayer representing this TestCase to
	 */
	public void setSelfLayer(PLayer selfLayer) {
		this.selfLayer = selfLayer;
	}
	
	/**
	 * Returns the title of this TestCase.
	 */
	public String toString() {
		return title;
	}

	/**
	 * Processes the validity of each step of this TestCase, and thus also this TestCase.
	 * 
	 * @param vd		VisualizationData used to process this TestCase's validity
	 */
	public void processValidity(VisualizationData vd) {
		HashMap<String, Event> eventsMap = vd.getEventsMap();
		TestCaseStep currStep = steps.get(0);
		Event previousEvent = eventsMap.get(currStep.getEventID());
		
		currStep.setValid(true);
		
		for (int i = 1; i < steps.size(); i++) {
			currStep = steps.get(i);
			if (currStep.isReachingStep()) {
				if (previousEvent.getReachingEdgesFromSelfTo().contains(currStep.getEventID())) {
					currStep.setValid(true);
				} else {
					currStep.setValid(false);
					this.setValid(false);
				}
			} else {
				if (previousEvent.getNormalEdgesFromSelfTo().contains(currStep.getEventID())) {
					currStep.setValid(true);
				} else {
					currStep.setValid(false);
					this.setValid(false);
				}
			}
			previousEvent = vd.getEventsMap().get(currStep.getEventID());;
		}
	}
}
