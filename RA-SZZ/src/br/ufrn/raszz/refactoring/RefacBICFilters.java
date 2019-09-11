package br.ufrn.raszz.refactoring;

import java.util.List;
import java.util.stream.Collectors;

import br.ufrn.raszz.model.RefElement;

public class RefacBICFilters {
	
	public static List<RefElement> getRefac_RENAME_METHOD (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_METHOD")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number && content.contains(/*r.getAftersimplename()*/
			    				 r.getAftersimplename().split("\\(")[0].split(" ")[r.getAftersimplename().split("\\(")[0].split(" ").length-1])) 
			    		 || (!r.getElementtype().equals("CALLER") && (number >= r.getAfterstartline() && number < (r.getAfterstartscope()>1?r.getAfterstartscope():r.getAfterstartline())))
			    		    )
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getAfterstartline() <= (number+adjindex) && number <= r.getAfterendline()) 
					    		 || (r.getElementtype().equals("CALLER") && (r.getAfterstartscope() == number || number == r.getAfterstartscope()+1)
					    				 /*&& (c.contains("class") || c.contains("interface") || c.contains("enum"))*/
					    				 && c.contains(r.getAftersimplename()))))
					     .collect(Collectors.toList());
			    				 
			    				 /*number == r.getAfterstartscope()  
			    		 || (number == r.getAfterstartscope()+1 && (c.contains("class") || c.contains("interface") || c.contains("enum")))))
			     .collect(Collectors.toList());*/
	}
	
	public static List<RefElement> getRefac_EXTRACT_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && (/*(!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 ||*/  (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_EXTRACT_INTERFACE (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_INTERFACE")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getAftersimplename()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_EXTRACT_SUPERCLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_SUPERCLASS")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getAftersimplename()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_INLINE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("INLINE_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && (/*(!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 ||*/  (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_ATTRIBUTE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("MOVE_ATTRIBUTE") || r.getRefactoringtype().equals("MOVE_OPERATION"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((      !r.getElementtype().equals("CALLER") 
			    				 && (number+adjindex) >= r.getAfterstartline() 
			    				 && number <= (r.getAfterendline()>0?r.getAfterendline():r.getAfterstartline())
			    		    ) 
			    		 /*|| (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number)*/) //mover um metodo ou um atributo nao influencia nas chamadas 
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
				     .filter(r -> r.getRefactoringtype().equals("MOVE_CLASS") 
				    		 && r.getRevision().equals(rev)
				    		 && r.getAfterpathfile().equals(path)
				    		 && !r.getElementtype().equals("CALLER") //move class nao influencia na chamada da classe
				    		 /*&& (      (c.contains("class") && c.contains(r.getAftersimplename()))				    			 
				    				|| (c.contains("interface") && c.contains(r.getAftersimplename())) 
				    				|| (c.contains("enum") && c.contains(r.getAftersimplename()))
				    			)*/
				    		 && r.getAfterstartline() <= (number+adjindex) && number <= (r.getAfterendline()>0?r.getAfterendline():r.getAfterstartline())
				    		).collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_PULL_UP_DOWN (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("PULL_") //PULL_UP_ATTRIBUTE, PULL_UP_OPERATION, PULL_DOWN_ATTRIBUTE, PULL_DOWN_OPERATION
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 /*|| (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number)*/) //pode trazer chamada a linhas que nao foram alteradas, nesse caso só interessa as linhas removidas para colocar no método criado
			    		)
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		List<RefElement> result = refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getAfternestingLevel() == 0 && number >= 1 && (number <= (r.getAfterstartscope()>1?r.getAfterstartscope():r.getAfterstartline()) 
			    		 || (number == ((r.getAfterstartscope()>1?r.getAfterstartscope():r.getAfterstartline())+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 /*|| (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number)*/)
			    		)
			     .collect(Collectors.toList());
		result.addAll(refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision() == rev
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getAfternestingLevel() >= 0 && number >= r.getAfterstartline() && (number <= r.getAfterstartscope() || (number == (r.getAfterstartscope()+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 /*|| (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number)*/)
			    		)
			     .collect(Collectors.toList()));
		result.addAll(refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().contains("MOVE_RENAME_CLASS") /*|| r.getRefactoringtype().contains("RENAME_CLASS")*/) 
			    		 && r.getRevision() == rev
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && ((c.contains("class") && c.contains(r.getAftersimplename()))     			 
				    				|| (c.contains("interface") && c.contains(r.getAftersimplename())) 
				    				|| (c.contains("enum") && c.contains(r.getAftersimplename())))) 
			    		 /*|| (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number)*/)
			    		)
			     .collect(Collectors.toList()));
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_MOVE_SOURCE_FOLDER (List<RefElement> refacSet, String path, String rev) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("MOVE_SOURCE_FOLDER")
			    		 || r.getRefactoringtype().equals("RENAME_PACKAGE")
			    		 /*|| r.getRefactoringtype().equals("MOVE_CLASS")*/)
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && !r.getElementtype().equals("CALLER"))
			     .collect(Collectors.toList());
	}
	
	public static List<RefElement> getRefac_CONVERT_ANONYMOUS_CLASS_TO_TYPE (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("CONVERT_ANONYMOUS_CLASS_TO_TYPE"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && (!r.getElementtype().equals("CALLER") 
			    				 && (number >= r.getAfterstartline() && number < (r.getAfterstartscope()>1?r.getAfterstartscope():r.getAfterstartline()))
			    		    )
			    		)
			     .collect(Collectors.toList());
	}	
	
	public static List<RefElement> getRefac_EXTRACT_AND_MOVE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("EXTRACT_AND_MOVE_OPERATION"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") 
			    				 && (number+adjindex) >= r.getAfterstartline() && number <= (r.getAfterendline()>0?r.getAfterendline():r.getAfterstartline()))
			    			)
			    		)
			     .collect(Collectors.toList());
	}
	

}
