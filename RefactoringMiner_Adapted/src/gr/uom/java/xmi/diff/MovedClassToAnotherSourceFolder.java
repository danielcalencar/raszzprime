package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLClass;

public class MovedClassToAnotherSourceFolder {
	private String className;
	private String originalPath;
	private String movedPath;
	private UMLClass originalClass;
	private UMLClass movedClass;	

	public MovedClassToAnotherSourceFolder(String className, String originalPath, String movedPath, UMLClass originalClass, UMLClass movedClass) {
		this.className = className;
		this.originalPath = originalPath;
		this.movedPath = movedPath;
		this.originalClass = originalClass;
		this.movedClass = movedClass;
	}
		
	public String getOriginalPath() {
		return originalPath;
	}

	public String getMovedPath() {
		return movedPath;
	}

	public UMLClass getOriginalClass() {
		return originalClass;
	}

	public UMLClass getMovedClass() {
		return movedClass;
	}

	public RenamePattern getRenamePattern() {
		int separatorPos = separatorPosOfCommonSuffix('/', originalPath, movedPath);
		if (separatorPos == -1) {
			return new RenamePattern(originalPath, movedPath, originalClass, movedClass);
		}
		String original = originalPath.substring(0, originalPath.length() - separatorPos);
		String moved = movedPath.substring(0, movedPath.length() - separatorPos);
		return new RenamePattern(original, moved, originalClass, movedClass);
	}
	
	private int separatorPosOfCommonSuffix(char separator, String s1, String s2) {
		int l1 = s1.length();
		int l2 = s2.length();
		int separatorPos = -1; 
		int lmin = Math.min(s1.length(), s2.length());
		boolean equal = true;
		for (int i = 0; i < lmin; i++) {
			char c1 = s1.charAt(l1 - i - 1);
			char c2 = s2.charAt(l2 - i - 1);
			equal = equal && c1 == c2;
			if (equal && c1 == separator) {
				separatorPos = i;
			}
		}
		return separatorPos;
	}
}
