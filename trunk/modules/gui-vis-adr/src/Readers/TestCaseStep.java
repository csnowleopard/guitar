package Readers;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This is an object class representing a step of a given TestCase.
 * 
 * @author Chris Carmel
 *
 */
public class TestCaseStep {

	/**
	 * String representing the ID of the Event incurred in this TestCaseStep.
	 */
	private String eventID;
	
	/**
	 * Boolean representing this TestCaseStep's reaching status.
	 */
	private boolean reachingStep;
	
	/**
	 * Boolean representing this TestCaseStep's validity.
	 */
	private boolean valid;
	
	/**
	 * TestCase representing this TestCaseStep's parent TestCase.
	 */
	private TestCase testCase;
	
	/**
	 * PPath representing this TestCaseStep on the PLayer of this TestCaseStep's
	 * parent TestCase.
	 */
	private PPath selfEdge = null;

	/**
	 * Constructs a new TestCaseStep belonging to the given TestCase.
	 * 
	 * @param testCase		TestCase that the new TestCaseStep belongs to
	 */
	public TestCaseStep(TestCase testCase) {
		this.eventID = null;
		this.reachingStep = false;
		this.valid = true;
		this.testCase = testCase;
		this.selfEdge = new PPath();
	}

	/**
	 * Returns this TestCaseSteps' EventID.
	 * 
	 * @return		this TestCaseSteps' EventID.
	 */
	public String getEventID() {
		return eventID;
	}
	
	/**
	 * Returns a boolean representing this TestCaseStep's reaching status.
	 *  
	 * @return		a boolean representing this TestCaseStep's reaching status
	 */
	public boolean isReachingStep() {
		return reachingStep;
	}

	/**
	 * Returns a boolean representing this TestCaseStep's validity.
	 * 
	 * @return		a boolean representing this TestCaseStep's validity
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Returns the parent TestCase of this TestCaseStep.
	 * 
	 * @return		the parent TestCase of this TestCaseStep
	 */
	public TestCase getTestCase() {
		return testCase;
	}
	
	/**
	 * Returns the PPath representing this TestCaseStep on it's parent TestCase's PLayer.
	 * 
	 * @return		the PPath representing this TestCaseStep on it's parent TestCase's PLayer
	 */
	public PPath getSelfEdge() {
		return selfEdge;
	}

	/**
	 * Sets this TestCaseStep's EventID to the incoming value.
	 * 
	 * @param eventID		value to set this TestCaseStep's EventID to
	 */
	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	/**
	 * Sets this TestCaseStep's reaching status to the incoming value.
	 * 
	 * @param reachingStep		value to set this TestCaseStep's reaching status to
	 */
	public void setReachingStep(boolean reachingStep) {
		this.reachingStep = reachingStep;
	}
	
	/**
	 * Sets this TestCaseStep's validity to the incoming value.
	 * 
	 * @param valid		value to set this TestCaseStep's validity to 
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	/**
	 * Sets this TestCaseStep's PPath to the incoming value.
	 * 
	 * @param selfEdge		value to set this TestCaseStpe's PPath to
	 */
	public void setSelfEdge(PPath selfEdge) {
		this.selfEdge = selfEdge;
	}
	
	/**
	 * Returns the EventID of this TestCaseStep.
	 */
	public String toString() {
		return eventID;
	}

}
