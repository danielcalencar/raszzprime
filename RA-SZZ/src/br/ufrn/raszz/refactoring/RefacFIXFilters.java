package br.ufrn.raszz.refactoring;

import java.util.List;
import java.util.stream.Collectors;

import br.ufrn.raszz.model.RefElement;

public class RefacFIXFilters {
	
	/*public static List<RefElement> getRefac_RENAME_METHOD (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_METHOD")
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number) 
			    		 || (!r.getElementtype().equals("CALLER") && (number >= r.getBeforestartline() && number < r.getBeforestartscope()))
			    		    )
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && (number == r.getBeforestartscope()  
			    		 || (number == r.getBeforestartscope()+1 && (c.contains("class") || c.contains("interface") || c.contains("enum")))))
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_EXTRACT_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((r.getElementtype().equals("CALLER") && r.getBeforestartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_EXTRACT_INTERFACE (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_INTERFACE")
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getBeforesimpleName()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getBeforestartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_EXTRACT_SUPERCLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_SUPERCLASS")
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getBeforesimpleName()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getBeforestartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_INLINE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("INLINE_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((r.getElementtype().equals("CALLER") && r.getBeforestartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_ATTRIBUTE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("MOVE_ATTRIBUTE") || r.getRefactoringtype().equals("MOVE_OPERATION"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getBeforestartline() && number <= r.getBeforeendline()) 
			    		 || (r.getElementtype().equals("CALLER") && r.getBeforestartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
				     .filter(r -> r.getRefactoringtype().equals("MOVE_CLASS") 
				    		 && r.getRevision().equals(rev)
				    		 && r.getBeforepathfile().equals(path)
				    		 && ((c.contains("class") && c.contains(r.getBeforesimpleName()))				    			 
				    				|| (c.contains("interface") && c.contains(r.getBeforesimpleName())) 
				    				|| (c.contains("enum") && c.contains(r.getBeforesimpleName())))				    						
				    		).collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_PULL_UP_DOWN (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("PULL_") //PULL_UP_ATTRIBUTE, PULL_UP_OPERATION, PULL_DOWN_ATTRIBUTE, PULL_DOWN_OPERATION
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getBeforestartline() && number <= r.getBeforeendline()) 
			    		 )
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		List<RefElement> result = refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getBeforenestingLevel() == 0 && number >= 1 && (number <= r.getBeforestartscope() || (number == (r.getBeforestartscope()+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
		result.addAll(refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision() == rev
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getBeforenestingLevel() >= 0 && number >= r.getBeforestartline() && (number <= r.getBeforestartscope() || (number == (r.getBeforestartscope()+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList()));
		result.addAll(refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().contains("MOVE_RENAME_CLASS") || r.getRefactoringtype().contains("RENAME_CLASS")) 
			    		 && r.getRevision() == rev
			    		 && r.getBeforepathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && ((c.contains("class") && c.contains(r.getAftersimplename()))     			 
				    				|| (c.contains("interface") && c.contains(r.getAftersimplename())) 
				    				|| (c.contains("enum") && c.contains(r.getAftersimplename())))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList()));
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_SOURCE_FOLDER (List<RefElement> refacSet, String path, String rev) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("MOVE_SOURCE_FOLDER")
			    		 || r.getRefactoringtype().equals("RENAME_PACKAGE")
			    		 || r.getRefactoringtype().equals("MOVE_CLASS"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getBeforepathfile().equals(path))
			     .collect(Collectors.toList());
	}*/

}
