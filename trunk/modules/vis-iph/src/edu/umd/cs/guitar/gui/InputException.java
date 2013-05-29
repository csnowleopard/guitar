package edu.umd.cs.guitar.gui;

/**
 * This is used to pass messages to the GraphBuilder object when something goes wrong with reading in the 
 * required input files and information.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class InputException extends Exception{
	
	private static final long serialVersionUID = 1L;
	/** The error message*/
	private String message;
	
	/**
	 * The constructor for the exception
	 * @param msg the error message
	 */
	public InputException(String msg){
		super();
		message = msg;
	}
	
	/**
	 * Returns the error message
	 * @return the error message
	 */
	public String getMessage(){
		return message;
	}
}
