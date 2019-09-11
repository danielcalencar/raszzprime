package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLClass;

public class RenamePattern {
	private String originalPath;
	private String movedPath;
	private UMLClass originalClass;
	private UMLClass movedClass;
	
	public RenamePattern(String originalPath, String movedPath, UMLClass originalClass, UMLClass movedClass) {
		this.originalPath = originalPath;
		this.movedPath = movedPath;
		this.originalClass = originalClass;
		this.movedClass = movedClass;
	}

	public UMLClass getOriginalClass() {
		return originalClass;
	}

	public UMLClass getMovedClass() {
		return movedClass;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public String getMovedPath() {
		return movedPath;
	}

	public String toString() {
		return originalPath + "\t->\t" + movedPath;
	}
	
	public boolean equals(Object o) {
		if(this == o) {
    		return true;
    	}
    	if(o instanceof RenamePattern) {
    		RenamePattern pattern = (RenamePattern)o;
    		return this.originalPath.equals(pattern.originalPath) && this.movedPath.equals(pattern.movedPath);
    	}
    	return false;
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((movedPath == null) ? 0 : movedPath.hashCode());
		result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
		return result;
	}
}
