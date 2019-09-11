package gr.uom.java.xmi.diff;

import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLClass;

public class RenameClassRefactoring implements Refactoring {
	private UMLClass originalClass;
	private UMLClass renamedClass;
	
	public RenameClassRefactoring(UMLClass originalClassName,  UMLClass renamedClassName) {
		this.originalClass = originalClassName;
		this.renamedClass = renamedClassName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(originalClass);
		sb.append(" renamed to ");
		sb.append(renamedClass);
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.RENAME_CLASS;
	}
	
	public UMLClass getOriginalClass() {
		return originalClass;
	}

	public UMLClass getRenamedClass() {
		return renamedClass;
	}

	public String getOriginalClassName() {
		return originalClass.getName();
	}

	public String getRenamedClassName() {
		return renamedClass.getName();
	}

	@Override
	public LocationInfo getBeforeLocationInfo() {
		return originalClass.getLocationInfo();
	}

	@Override
	public LocationInfo getAfterLocationInfo() {
		return renamedClass.getLocationInfo();		
	}
	
}
