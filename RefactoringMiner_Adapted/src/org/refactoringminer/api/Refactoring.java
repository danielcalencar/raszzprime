package org.refactoringminer.api;

import java.io.Serializable;

import gr.uom.java.xmi.LocationInfo;

public interface Refactoring extends Serializable {

	public RefactoringType getRefactoringType();
	
	public String getName();

	public String toString();
	
	public LocationInfo getBeforeLocationInfo();
	
	public LocationInfo getAfterLocationInfo();
	
	/*protected long firststatementline;
	private String content;
	private String firststatement;*/
	//private long callerline;*
	
}