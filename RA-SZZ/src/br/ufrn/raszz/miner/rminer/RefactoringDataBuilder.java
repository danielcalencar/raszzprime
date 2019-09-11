package br.ufrn.raszz.miner.rminer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.model.RefacToolType;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ConvertAnonymousClassToTypeRefactoring;
import gr.uom.java.xmi.diff.ExtractAndMoveOperationRefactoring;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import gr.uom.java.xmi.diff.MovedClassToAnotherSourceFolder;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.RenamePackageRefactoring;

public class RefactoringDataBuilder {
	
	public List<RefElement> prepareElement(Refactoring refactoring, String project, String commitId) {
		
		List<RefElement> refElements = new ArrayList<RefElement>();
		RefElement refEl;
		
		switch (refactoring.getRefactoringType()) {
			case RENAME_CLASS:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Class"); 
				RenameClassRefactoring renameClass = (RenameClassRefactoring) refactoring;
				refEl.setEntityafter("class " + renameClass.getRenamedClass().getSimpleName());
				refEl.setAftersimpleName(renameClass.getRenamedClass().getSimpleName());
				refEl.setBeforesimpleName(renameClass.getOriginalClass().getSimpleName());
				refEl.setEntitybefore("class " + renameClass.getOriginalClass().getSimpleName());
				refEl.setBeforecontent(renameClass.getOriginalClass().getContent());
				refEl.setAftercontent(renameClass.getRenamedClass().getContent());
				refElements.add(refEl);
			break;
			case RENAME_METHOD:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Method"); 
				RenameOperationRefactoring renameOperation = (RenameOperationRefactoring) refactoring;
				refEl.setBeforesimpleName(renameOperation.getOriginalOperation().getName());
				refEl.setEntitybefore(renameOperation.getOriginalOperation().toString());
				refEl.setBeforecontent(renameOperation.getOriginalOperation().getContent());
				refEl.setAftercontent(renameOperation.getRenamedOperation().getContent());
				refEl.setAftersimpleName(renameOperation.getRenamedOperation().getName());
				refEl.setEntityafter(renameOperation.getRenamedOperation().toString());				
				refElements.add(refEl);
			break;
			case MOVE_SOURCE_FOLDER:
				MoveSourceFolderRefactoring moveSourceFolderRefactoring = (MoveSourceFolderRefactoring) refactoring;
				List<MovedClassToAnotherSourceFolder> movedClassesToAnotherSourceFolder = moveSourceFolderRefactoring.getMovedClassesToAnotherSourceFolder();
				for (MovedClassToAnotherSourceFolder movedClass : movedClassesToAnotherSourceFolder) {
					refEl = init(refactoring, project, commitId);
					refEl.setElementtype("Class"); 
					
					refEl.setAfterpathfile(movedClass.getMovedClass().getSourceFile());
					refEl.setEntityafter("class " + movedClass.getMovedClass().getSimpleName()); 
					refEl.setAftersimpleName(movedClass.getMovedClass().getSimpleName());
					refEl.setAfterstartline(movedClass.getMovedClass().getLocationInfo().getStartLine()+1);
					refEl.setAfterstartscope(movedClass.getMovedClass().getLocationInfo().getStartFirstStatement()); 			
					refEl.setAfterendline(movedClass.getMovedClass().getLocationInfo().getEndLine()+1);
					
					refEl.setBeforepathfile(movedClass.getOriginalClass().getLocationInfo().getFilePath());
					refEl.setEntitybefore("class " + movedClass.getOriginalClass().getSimpleName());
					refEl.setBeforesimpleName(movedClass.getOriginalClass().getSimpleName());
					refEl.setBeforestartline(movedClass.getOriginalClass().getLocationInfo().getStartLine()+1);
					refEl.setBeforestarscope(movedClass.getOriginalClass().getLocationInfo().getStartFirstStatement()); 			
					refEl.setBeforeendline(movedClass.getOriginalClass().getLocationInfo().getEndLine()+1);
					
					refEl.setBeforecontent(movedClass.getOriginalClass().getContent());
					refEl.setAftercontent(movedClass.getMovedClass().getContent());
					
					refElements.add(refEl);
				}				
				break;	
			case RENAME_PACKAGE:
				RenamePackageRefactoring renamePackageRefactoring = (RenamePackageRefactoring) refactoring;
				List<MoveClassRefactoring> moveClassRefactoring = renamePackageRefactoring.getMoveClassRefactorings();
				for (MoveClassRefactoring movedClass : moveClassRefactoring) {
					refEl = init(refactoring, project, commitId);
					refEl.setElementtype("Class"); 
					
					refEl.setAfterpathfile(movedClass.getMovedClass().getSourceFile());
					refEl.setEntityafter("class " + movedClass.getMovedClass().getSimpleName());
					refEl.setAftersimpleName(movedClass.getMovedClass().getSimpleName());
					refEl.setAfterstartline(movedClass.getMovedClass().getLocationInfo().getStartLine()+1);
					refEl.setAfterstartscope(movedClass.getMovedClass().getLocationInfo().getStartFirstStatement()); 			
					refEl.setAfterendline(movedClass.getMovedClass().getLocationInfo().getEndLine()+1);
					
					refEl.setBeforepathfile(movedClass.getOriginalClass().getLocationInfo().getFilePath());
					refEl.setEntitybefore("class " + movedClass.getOriginalClass().getSimpleName());
					refEl.setBeforesimpleName(movedClass.getOriginalClass().getSimpleName());
					refEl.setBeforestartline(movedClass.getOriginalClass().getLocationInfo().getStartLine()+1);
					refEl.setBeforestarscope(movedClass.getOriginalClass().getLocationInfo().getStartFirstStatement()); 				
					refEl.setBeforeendline(movedClass.getOriginalClass().getLocationInfo().getEndLine()+1);
					
					refEl.setBeforecontent(movedClass.getOriginalClass().getContent());
					refEl.setAftercontent(movedClass.getMovedClass().getContent());
					
					refElements.add(refEl);
				}				
				break;
				
			case PUSH_DOWN_OPERATION:
			case PULL_UP_OPERATION:
			case MOVE_OPERATION:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Method"); 
				MoveOperationRefactoring moveOperationRefactoring = (MoveOperationRefactoring) refactoring;
				refEl.setBeforesimpleName(moveOperationRefactoring.getOriginalOperation().getName());
				refEl.setEntitybefore(moveOperationRefactoring.getOriginalOperation().toString());
				refEl.setAftersimpleName(moveOperationRefactoring.getMovedOperation().getName());
				refEl.setEntityafter(moveOperationRefactoring.getMovedOperation().toString());
				
				refEl.setBeforecontent(moveOperationRefactoring.getOriginalOperation().getContent());
				refEl.setAftercontent(moveOperationRefactoring.getMovedOperation().getContent());
				
				refElements.add(refEl);
				break;
				
			case PUSH_DOWN_ATTRIBUTE:
			case PULL_UP_ATTRIBUTE:
			case MOVE_ATTRIBUTE:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Attribute"); 
				MoveAttributeRefactoring moveAttribute = (MoveAttributeRefactoring) refactoring;
				refEl.setBeforesimpleName(moveAttribute.getOriginalAttribute().getName());
				refEl.setEntitybefore(moveAttribute.getOriginalAttribute().toString());
				refEl.setAftersimpleName(moveAttribute.getMovedAttribute().getName());
				refEl.setEntityafter(moveAttribute.getMovedAttribute().toString());
				
				refEl.setBeforecontent(moveAttribute.getOriginalAttribute().getContent());
				refEl.setAftercontent(moveAttribute.getMovedAttribute().getContent());
				
				refElements.add(refEl);
				break;
				
			case MOVE_CLASS:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Class"); 
				MoveClassRefactoring moveClass = (MoveClassRefactoring) refactoring;
				refEl.setEntityafter("class " + moveClass.getMovedClass().getSimpleName());
				refEl.setAftersimpleName(moveClass.getMovedClass().getSimpleName());
				refEl.setBeforesimpleName(moveClass.getOriginalClass().getSimpleName());
				refEl.setEntitybefore("class " + moveClass.getOriginalClass().getSimpleName());
				
				refEl.setBeforecontent(moveClass.getOriginalClass().getContent());
				refEl.setAftercontent(moveClass.getMovedClass().getContent());
				
				refElements.add(refEl);
				break;
			
			case EXTRACT_AND_MOVE_OPERATION:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Method"); 
				ExtractAndMoveOperationRefactoring extractAndMoveOperation = (ExtractAndMoveOperationRefactoring) refactoring;
				refEl.setBeforesimpleName(extractAndMoveOperation.getSourceOperationBeforeExtraction().getName());
				refEl.setEntitybefore(extractAndMoveOperation.getSourceOperationBeforeExtraction().toString());
				refEl.setAftersimpleName(extractAndMoveOperation.getExtractedOperation().getName());
				refEl.setEntityafter(extractAndMoveOperation.getExtractedOperation().toString());
				
				refEl.setBeforecontent(extractAndMoveOperation.getSourceOperationBeforeExtraction().getContent());
				refEl.setAftercontent(extractAndMoveOperation.getExtractedOperation().getContent());
				
				refElements.add(refEl);
				break;
				
			case EXTRACT_OPERATION:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Method"); 
				ExtractOperationRefactoring extractOperation = (ExtractOperationRefactoring) refactoring;
				refEl.setBeforesimpleName(extractOperation.getSourceOperationBeforeExtraction().getName());
				refEl.setEntitybefore(extractOperation.getSourceOperationBeforeExtraction().toString());
				refEl.setAftersimpleName(extractOperation.getExtractedOperation().getName());
				refEl.setEntityafter(extractOperation.getExtractedOperation().toString());
				
				refEl.setBeforecontent(extractOperation.getSourceOperationBeforeExtraction().getContent());
				refEl.setAftercontent(extractOperation.getExtractedOperation().getContent());
				
				refElements.add(refEl);
				break;
			
			case EXTRACT_SUPERCLASS:
			case EXTRACT_INTERFACE:				
				ExtractSuperclassRefactoring extractSuperclass = (ExtractSuperclassRefactoring) refactoring;
				Set<UMLClass> subclassSet = extractSuperclass.getUMLSubclassSet();
				UMLClass extractedClass = extractSuperclass.getExtractedClass();
				for (UMLClass subclass : subclassSet) {
					refEl = init(refactoring, project, commitId);
					if(extractSuperclass.getExtractedClass().isInterface()) {
						refEl.setElementtype("Interface");
						refEl.setEntityafter("interface " + extractedClass.getSimpleName());
						refEl.setEntitybefore("interface " + subclass.getSimpleName());
					} else {
						refEl.setElementtype("Class");
						refEl.setEntityafter("class " + extractedClass.getSimpleName());
						refEl.setEntitybefore("class " + subclass.getSimpleName());
					}					
					refEl.setAfterpathfile(extractedClass.getSourceFile());					
					refEl.setAftersimpleName(extractedClass.getSimpleName());
					refEl.setAfterstartline(extractedClass.getLocationInfo().getStartLine()+1);
					refEl.setAfterstartscope(extractedClass.getLocationInfo().getStartFirstStatement()); 			
					refEl.setAfterendline(extractedClass.getLocationInfo().getEndLine()+1);
					
					refEl.setBeforepathfile(subclass.getLocationInfo().getFilePath());					
					refEl.setBeforesimpleName(subclass.getSimpleName());
					refEl.setBeforestartline(subclass.getLocationInfo().getStartLine()+1);
					refEl.setBeforestarscope(subclass.getLocationInfo().getStartFirstStatement()); 			
					refEl.setBeforeendline(subclass.getLocationInfo().getEndLine()+1);
					
					refEl.setBeforecontent(subclass.getContent());
					refEl.setAftercontent(extractedClass.getContent());
					
					refElements.add(refEl);
				}
				break;			
								
			case INLINE_OPERATION:				
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Method"); 
				InlineOperationRefactoring inlineOperation = (InlineOperationRefactoring) refactoring;
				refEl.setBeforesimpleName(inlineOperation.getInlinedOperation().getName());
				refEl.setEntitybefore(inlineOperation.getInlinedOperation().toString());
				refEl.setAftersimpleName(inlineOperation.getTargetOperationAfterInline().getName());
				refEl.setEntityafter(inlineOperation.getTargetOperationAfterInline().toString());
				
				refEl.setBeforecontent(inlineOperation.getInlinedOperation().getContent());
				refEl.setAftercontent(inlineOperation.getTargetOperationAfterInline().getContent());
				
				refElements.add(refEl);
				break;
				
			case CONVERT_ANONYMOUS_CLASS_TO_TYPE:
				refEl = initWithLocation(refactoring, project, commitId);
				refEl.setElementtype("Class"); 
				ConvertAnonymousClassToTypeRefactoring convertAnonymousClassToType = (ConvertAnonymousClassToTypeRefactoring) refactoring;
				refEl.setEntityafter("class " + convertAnonymousClassToType.getAddedClass().getSimpleName());
				refEl.setAftersimpleName(convertAnonymousClassToType.getAddedClass().getSimpleName());
				refEl.setBeforesimpleName(convertAnonymousClassToType.getAnonymousClass().getSimpleName());
				refEl.setEntitybefore("class " + convertAnonymousClassToType.getAnonymousClass().getSimpleName());
				
				refEl.setBeforecontent(convertAnonymousClassToType.getAnonymousClass().getContent());
				refEl.setAftercontent(convertAnonymousClassToType.getAddedClass().getContent());
				
				refElements.add(refEl);
				break;
				
			default:
				refEl = init(refactoring, project, commitId);
				refEl.setElementtype("Element");
				refElements.add(refEl);
				//refEl.setEntityafter(refactoring.getEntityAfter().getVerboseFullLine()); 		//TODO
				//refEl.setEntitybefore(refactoring.getEntityBefore().getVerboseFullLine()); 	//TODO
			break;
		}		
		return refElements;
	}
	
	private RefElement init(Refactoring refactoring, String project, String commitId) {
		RefElement refEl = new RefElement(commitId, project);
		refEl.setRefactoringtype(refactoring.getRefactoringType().toString());
		refEl.setSummary(refactoring.toString());		
		refEl.setTool(RefacToolType.RMINER);		
		//printdebug(refEl);
		return refEl;
	}
	private RefElement initWithLocation(Refactoring refactoring, String project, String commitId) {
		RefElement refEl = init(refactoring, project, commitId);
	
		refEl.setAfterpathfile(refactoring.getAfterLocationInfo().getFilePath());		
		refEl.setAfterstartline(refactoring.getAfterLocationInfo().getStartLine()+1);
		refEl.setAfterendline(refactoring.getAfterLocationInfo().getEndLine()+1);
		refEl.setAfterstartscope(refactoring.getAfterLocationInfo().getStartFirstStatement());
		
		refEl.setBeforepathfile(refactoring.getBeforeLocationInfo().getFilePath());		
		refEl.setBeforestartline(refactoring.getBeforeLocationInfo().getStartLine()+1);
		refEl.setBeforeendline(refactoring.getBeforeLocationInfo().getEndLine()+1);
		refEl.setBeforestarscope(refactoring.getBeforeLocationInfo().getStartFirstStatement());
		
		return refEl;
	}
	
	private static void printdebug(RefElement refEl) {

		/*System.out.print(refEl.getRefactoringtype() + " | after: " + refEl.getEntityafter() + " | " + afterFolder
				+ refEl.getRevision() + refEl.getAfterpathfile());*/
		System.out.print(refEl.getSummary());

		System.out.print(" | [" + refEl.getAfterstartline() + "," + refEl.getAfterendline() + "("
				+ refEl.getAfterpathfile() + ")]");

		// System.out.println(refEl.getFirtstatement());
		System.out.println(" | before: " + refEl.getBeforestartline() + " | " + refEl.getBeforeendline() + "("
				+ refEl.getBeforepathfile() + ")]");

		//System.out.println(" | Callers (" + refEl.getCallers() + ")");

		/*
		for (RefCaller refCaller : refEl.getCallerList()) {
			System.out.println(" Caller: " + refCaller.getCallermethod() + " | " + refCaller.getCallerpath() + " | ["
					+ refCaller.getCallerstartline() + "," + refCaller.getCallerendline() + " ("
					+ refCaller.getCallerline() + ")]");
		}*/

	}

}
