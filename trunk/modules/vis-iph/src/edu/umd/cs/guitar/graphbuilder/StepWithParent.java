package edu.umd.cs.guitar.graphbuilder;

import edu.umd.cs.guitar.gen.StepType;

public class StepWithParent {

	private String dna;
	private String parent;
	private StepType step;
	
	public StepWithParent(String parent, String dna, StepType step) {
		this.parent = parent;
		this.dna = dna;
		this.step = step;
	}
	

	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getDna() {
		return dna;
	}
	public void setDna(String dna) {
		this.dna = dna;
	}
	public void setStep(StepType step) {
		this.step = step;
	}
	public StepType getStep() {
		return step;
	}
	
	
}
