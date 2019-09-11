package br.ufrn.raszz.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.ufrn.raszz.miner.refdiff.RefDiffService;
import br.ufrn.raszz.miner.rminer.RMinerService;
import br.ufrn.raszz.model.RefCaller;
import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.model.RefacToolType;
import br.ufrn.raszz.persistence.SzzDAO;
import br.ufrn.razszz.connectoradapter.SzzRepository;

public abstract class RefacOperations {
		
	public static boolean isRefac(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {	
		List<RefElement> result = filterRefacSet(refacSet, path, rev, number, adjindex, content);
		return (result.size() == 0)? false: true;
	}
	
	public static List<RefElement> filterRefacSet(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		List<RefElement> result = new ArrayList<RefElement>();
		result.addAll(RefacBICFilters.getRefac_RENAME_METHOD(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_RENAME_CLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_EXTRACT_OPERATION(refacSet, path, rev, number, adjindex));
		result.addAll(RefacBICFilters.getRefac_EXTRACT_INTERFACE(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_EXTRACT_SUPERCLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_EXTRACT_SUPERCLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_INLINE_OPERATION(refacSet, path, rev, number, adjindex));
		result.addAll(RefacBICFilters.getRefac_MOVE_ATTRIBUTE_OPERATION(refacSet, path, rev, number, adjindex));
		result.addAll(RefacBICFilters.getRefac_MOVE_CLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_PULL_UP_DOWN(refacSet, path, rev, number, adjindex));
		result.addAll(RefacBICFilters.getRefac_MOVE_RENAME_CLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_MOVE_SOURCE_FOLDER(refacSet, path, rev));
		result.addAll(RefacBICFilters.getRefac_CONVERT_ANONYMOUS_CLASS_TO_TYPE(refacSet, path, rev, number, adjindex, content));
		result.addAll(RefacBICFilters.getRefac_EXTRACT_AND_MOVE_OPERATION(refacSet, path, rev, number, adjindex));
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public static String prevRefacContent(List<RefElement> refacSet, String content) {
		String beforeContent = content;
		String[] aux;
		String bef, aft;
		for(RefElement refac: refacSet) {
			switch (refac.getRefactoringtype()) {
			case "RENAME_METHOD":
				aux = refac.getEntitybefore().split("\\(")[0].split(" ");
				bef = aux[aux.length-1];
				aux = refac.getEntityafter().split("\\(")[0].split(" ");
				aft = aux[aux.length-1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;
			case "RENAME_CLASS":
			/*case "MOVE_SOURCE_FOLDER":
			case "RENAME_PACKAGE":
			case "MOVE_CLASS":*/ //nao muda nada no conteudo nesse caso
				bef = refac.getEntitybefore().split(" ")[1];
				aft = refac.getEntityafter().split(" ")[1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;
			case "MOVE_RENAME_CLASS":
				aux = refac.getEntitybefore().split("\\(")[0].split(" ");
				bef = aux[aux.length-1];
				aux = refac.getEntityafter().split("\\(")[0].split(" ");
				aft = aux[aux.length-1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;	
			}
		}
		return beforeContent;
	}
	
	public static long[] prevRefacLines(List<RefElement> refacSet, SzzDAO szzDAO) {
		long[] interval = new long[2];
		for(RefElement refac: refacSet) {
			switch (refac.getElementtype()) {
			case "CALLER":
				if (refac.getRefactoringtype().equals("INLINE_OPERATION")) {
					RefElement refElement = szzDAO.getRefacBicBySummary(refac.getProject(), refac.getSummary());
					if (refElement != null) interval = matchBefore(refElement);
				} else {
					//interval = matchAfter(refac); TODO O que fazer? tentativa abaixo, é garantido?
					interval = szzDAO.getIntervalEquivBeforeCaller(refac.getRevision(), refac.getSummary(), refac.getAfterpathfile());
				}
				break;
			default:
				interval = matchBefore(refac);
				break;
			}
		}
		return interval;
	}
	
	private static long[] matchBefore(RefElement refac) {
		long[] interval = new long[2];
		if (refac.getRefactoringtype().equals("EXTRACT_OPERATION") ||
			refac.getRefactoringtype().equals("EXTRACT_AND_MOVE_OPERATION") ||				
			refac.getRefactoringtype().equals("INLINE_OPERATION")
			){ //primeira linha do escopo do método até a última linha do escopo
				interval[0] = refac.getBeforestartline(); //refac.getBeforestarscope();
				interval[1] = refac.getBeforeendline()>0?refac.getBeforeendline():refac.getBeforestartline();
			}
		if (refac.getRefactoringtype().equals("EXTRACT_INTERFACE") ||
			refac.getRefactoringtype().equals("EXTRACT_SUPERCLASS") ||				
			refac.getRefactoringtype().equals("MOVE_OPERATION") ||
			refac.getRefactoringtype().equals("PULL_UP_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("PULL_UP_OPERATION") ||
			refac.getRefactoringtype().equals("PUSH_DOWN_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("PUSH_DOWN_OPERATION")
		){ //primeira linha do javadoc até o final ou início do escopo
			interval[0] = refac.getBeforestartline();
			interval[1] = (refac.getBeforeendline() >0)? 
						refac.getBeforeendline(): (refac.getBeforestarscope()>1?refac.getBeforestarscope():refac.getBeforestartline());
		} 
		if (refac.getRefactoringtype().equals("MOVE_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("RENAME_METHOD") /*||
			refac.getRefactoringtype().equals("RENAME_CLASS") ||
			refac.getRefactoringtype().equals("MOVE_RENAME_CLASS")*/
		){ //a linha exata onde inicial o atributo, método ou classe movida/renomeada
			interval[0] = refac.getBeforestartline(); //refac.getBeforestarscope();
			interval[1] = (refac.getBeforestarscope() >1)? 
					refac.getBeforestarscope(): refac.getBeforeendline();
		}
		if (refac.getRefactoringtype().equals("MOVE_SOURCE_FOLDER") ||
				refac.getRefactoringtype().equals("MOVE_CLASS") ||
				refac.getRefactoringtype().equals("RENAME_PACKAGE") ||
				refac.getRefactoringtype().equals("RENAME_CLASS") ||
				refac.getRefactoringtype().equals("MOVE_RENAME_CLASS")
		){ //todo o escopo da classe
			interval[0] = refac.getBeforestartline(); //refac.getBeforestarscope();
			interval[1] = refac.getBeforeendline()>0?refac.getBeforeendline():refac.getBeforestartline();
		}
		return interval; 
	}
	
	private static long[] matchAfter(RefElement refac) {
		long[] interval = new long[2];
		if (refac.getRefactoringtype().equals("EXTRACT_OPERATION") ||
				refac.getRefactoringtype().equals("INLINE_OPERATION")
			){ //primeira linha do escopo do método até a última linha do escopo
				interval[0] = refac.getAfterstartline(); //refac.getBeforestarscope();
				interval[1] = refac.getAfterendline();
			}
		if (refac.getRefactoringtype().equals("EXTRACT_INTERFACE") ||
			refac.getRefactoringtype().equals("EXTRACT_SUPERCLASS") ||				
			refac.getRefactoringtype().equals("MOVE_OPERATION") ||
			refac.getRefactoringtype().equals("PULL_UP_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("PULL_UP_OPERATION") ||
			refac.getRefactoringtype().equals("PUSH_DOWN_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("PUSH_DOWN_OPERATION")
		){ //primeira linha do javadoc até o final ou início do escopo
			interval[0] = refac.getAfterstartline();
			interval[1] = (refac.getAfterendline() >0)? 
						refac.getAfterendline(): refac.getAfterstartscope();
		} 
		if (refac.getRefactoringtype().equals("MOVE_ATTRIBUTE") ||
			refac.getRefactoringtype().equals("RENAME_METHOD") /*||
			refac.getRefactoringtype().equals("RENAME_CLASS") ||
			refac.getRefactoringtype().equals("MOVE_RENAME_CLASS")*/
		){ //a linha exata onde inicial o atributo, método ou classe movida/renomeada
			interval[0] = refac.getAfterstartline(); //refac.getAfterstartscope();
			interval[1] = (refac.getAfterstartscope() >0)? 
					refac.getAfterstartscope(): refac.getAfterendline();
		}
		if (refac.getRefactoringtype().equals("MOVE_SOURCE_FOLDER") ||
				refac.getRefactoringtype().equals("MOVE_CLASS") ||
				refac.getRefactoringtype().equals("RENAME_PACKAGE") ||
				refac.getRefactoringtype().equals("RENAME_CLASS") ||
				refac.getRefactoringtype().equals("MOVE_RENAME_CLASS")
		){ //todo o escopo da classe
			interval[0] = refac.getAfterstartline(); //refac.getAfterstartscope();
			interval[1] = refac.getAfterendline();
		}
		return interval; 
	}
	
	public static String prevRefacPath(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		List<RefElement> result = filterRefacSet(refacSet, path, rev, number, adjindex, content);
				//getRefac_RENAME_CLASS(result, path, rev, number, adjindex, content);
		
		/*List<RefElement>*/ result = result.stream()
			     .filter(r -> !r.getElementtype().equals("CALLER"))
			     .collect(Collectors.toList());/**/
		
		return (result.size() != 0)? result.get(0).getBeforepathfile() : null;
	}
	
	/*
	public String prevRefacContent(String path, long prevrev, int prevnumber, int adjindex, String content) {
		List<Object[]> refac = szzDAO.getRefacBic(path, prevrev, prevnumber, adjindex, content);
		//revision, refactoringtype, entitybefore, entityafter, [4]elementtype, [5]afterstartline, [6]afterendline, [7]afterstartscope, aftersimplename
		if (refac.size() > 0) {
			Object[] ref = refac.get(0);
			if (ref[1].toString().equals("RENAME_METHOD")) {
				String[] aux = ref[2].toString().split("\\(")[0].split(" ");
				String bef = aux[aux.length-1];					
				aux = ref[3].toString().split("\\(")[0].split(" ");
				String aft = aux[aux.length-1];					
				return content.replaceFirst(aft, bef);
			}
		}
		return null;
	}*/
	
	public static List<RefElement> checkUpdateRefactoringDatabase(SzzRepository repository, String commitId, String project, RefacToolType refactool){
		String revisionType="run";	
		switch (refactool) {
		case REFDIFF:	
			RefDiffService refDiffService = new RefDiffService(repository);
			return refDiffService.executeRefDiff(project, commitId, revisionType);
		case RMINER: 	
			RMinerService rminerService = new RMinerService(repository);
			return rminerService.executeRMiner(project, commitId, revisionType);	
		default:
			return null;
		}		
	}	
	
	public static void checkRefactoringStored(SzzRepository repository, String commitId, String project, 
			RefacToolType refacTool, Map<RefacToolType, Map<String, String>> refacRevProcSet, List<RefElement> refacSet) throws Exception {
		//repository = repository.reset();
		if (refacTool == RefacToolType.BOTH) {								
			checkRefactoringStored(repository, commitId, project, RefacToolType.REFDIFF, refacRevProcSet, refacSet);
			checkRefactoringStored(repository, commitId, project, RefacToolType.RMINER, refacRevProcSet, refacSet);
		} else {		
			Map<String, String> refacToolRevProcSet = refacRevProcSet.get(refacTool);		
			if (/*refacToolRevProcSet == null ||*/ !refacToolRevProcSet.containsKey(commitId)) {
				List<RefElement> refElements = RefacOperations.checkUpdateRefactoringDatabase(repository, commitId, project, refacTool);
				if (refElements != null) {
					//refacSet.addAll(refElements);
					for (RefElement refElement : refElements) {
						refacSet.add(refElement);
						if (refElement.getCallerList() != null && refElement.getCallerList().size() > 0) {
							refacSet.addAll(refCallerSetToRefElementSet(refElement.getCallerList(), refElement.getEntitybefore()));
						}
					}				
				}
				//if (refacToolRevProcSet == null) refacToolRevProcSet = new HashMap<>();
				refacToolRevProcSet.put(commitId, project);
			}		
		}
	}
	
	private static List<RefElement> refCallerSetToRefElementSet(List<RefCaller> callers, String entityBefore){
		List<RefElement> refacs = new ArrayList<RefElement>();
		for (RefCaller caller : callers) {		
			if (caller.getType().equals("before")) continue;
			if (caller.getType().equals("after") && caller.getRefactoringtype().equals("INLINE_OPERATION")) continue;
			RefElement refac = new RefElement(caller.getRevision(), caller.getProject());
			refac.setRefactoringtype(caller.getRefactoringtype());
			refac.setEntityafter(caller.getEntityafter());
			refac.setEntitybefore(entityBefore);
			refac.setElementtype("CALLER");
			refac.setAfterstartline(caller.getCallerstartline());
			refac.setAfterendline(caller.getCallerendline());
			refac.setAfterstartscope(caller.getCallerline());
			refac.setAftersimpleName(caller.getSimplename());
			refac.setAfterpathfile(caller.getCallerpath());
			refac.setAfternestingLevel(caller.getNestingLevel());
			refac.setTool(caller.getTool());
			refac.setSummary(caller.getSummary());
			refacs.add(refac);
		}
		return refacs;
	}
	
	
	

}
