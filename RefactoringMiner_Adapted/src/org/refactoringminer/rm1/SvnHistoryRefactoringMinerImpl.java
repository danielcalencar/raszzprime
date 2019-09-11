package org.refactoringminer.rm1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.refactoringminer.api.HistoryRefactoringMiner;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;

public class SvnHistoryRefactoringMinerImpl<T> extends HistoryRefactoringMinerImpl<T> implements HistoryRefactoringMiner<T> {

	protected List<Refactoring> detectRefactorings(SvnService svnService, String tempFolder, SVNRepository repository, final RefactoringHandler<T> handler, SVNLogEntry currentCommit) throws Exception {
		List<Refactoring> refactoringsAtRevision;
		long commitId = currentCommit.getRevision();
	    List<String> filesBefore = new ArrayList<String>();
	    List<String> filesCurrent = new ArrayList<String>();
		Map<String, String> renamedFilesHint = new HashMap<String, String>();
						
		svnService.fileTreeDiff(repository, currentCommit, filesBefore, filesCurrent, renamedFilesHint, false);
		if (filesBefore.isEmpty() || filesCurrent.isEmpty()) {
		    return Collections.emptyList();
		}
		
		UMLModel currentUMLModel; 
		UMLModel parentUMLModel;
		
		// Checkout and build model for current commit
	    File folderAfter = new File(tempFolder, "v1\\" + commitId);
	    if (folderAfter.exists()) {
	        logger.info(String.format("Analyzing code after (%s) ...", commitId) + " " + new Date());
			currentUMLModel = createModel(folderAfter, filesCurrent);
	    } else {
	    	svnService.checkout(repository,folderAfter.getAbsolutePath(),filesCurrent,commitId);
	    	logger.info(String.format("Analyzing code after (%s) ...", commitId) + " " + new Date());
        	currentUMLModel = createModel(folderAfter, filesCurrent);
	    }
	    
	    // Checkout and build model for parent commit
	    Long parentCommitId = currentCommit.getRevision() - 1;
		File folderBefore = new File(tempFolder, "v0\\" + commitId);
		if (folderBefore.exists()) {
		    logger.info(String.format("Analyzing code before (%s) ...", parentCommitId)+ " " + new Date());
		    parentUMLModel = createModel(folderBefore, filesBefore);
		} else {
			svnService.checkout(repository,folderBefore.getAbsolutePath(),filesBefore,parentCommitId);
		    logger.info(String.format("Analyzing code before (%s) ...", parentCommitId)+ " " + new Date());
		    parentUMLModel = createModel(folderBefore, filesBefore);
		}

		// Diff between currentModel e parentModel
		refactoringsAtRevision = parentUMLModel.diff(currentUMLModel, renamedFilesHint).getRefactorings();
		refactoringsAtRevision = filter(refactoringsAtRevision);
		handler.handle(commitId+"", refactoringsAtRevision);
		handler.handle((T)currentCommit, refactoringsAtRevision);

		return refactoringsAtRevision;
	}

	@Override
	public void detectAtCommit(T repo, String cloneURL, String commitId, String folder, RefactoringHandler handler) {
		SVNRepository repository = (SVNRepository) repo;
		
		List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		SvnService svnService = new SvnServiceImpl();
				
		try {
			repository.log(new String[] { "" }, logEntries, Long.parseLong(commitId), Long.parseLong(commitId), true, false);
			SVNLogEntry currentCommit = logEntries.get(0);
			logEntries.clear();
			this.detectRefactorings(svnService, folder, repository, handler, currentCommit);
		} catch (Exception e) {
			logger.warn(String.format("Ignored revision %s due to error", commitId), e);
			handler.handleException(commitId, e);
		}
	}
	
	protected List<Refactoring> filter(List<Refactoring> refactoringsAtRevision) {
		if (this.refactoringTypesToConsider == null) {
			return refactoringsAtRevision;
		}
		List<Refactoring> filteredList = new ArrayList<Refactoring>();
		for (Refactoring ref : refactoringsAtRevision) {
			if (this.refactoringTypesToConsider.contains(ref.getRefactoringType())) {
				filteredList.add(ref);
			}
		}
		return filteredList;
	}
	
	@Override
	public void detectAll(T repo, String branch, final RefactoringHandler handler) throws Exception {
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void fetchAndDetectNew(T repo, final RefactoringHandler handler) throws Exception {
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}

	protected UMLModel createModel(File projectFolder, List<String> files) throws Exception {
		return new UMLModelASTReader(projectFolder, files).getUmlModel();
	}

	@Override
	public void detectBetweenCommits(T repo, String startCommitId, String endCommitId,
			RefactoringHandler handler) throws Exception {
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void detectBetweenTags(T repository, String startTag, String endTag, RefactoringHandler handler)
			throws Exception {
		throw new java.lang.UnsupportedOperationException("Not supported yet.");		
	}
	
	@Override
	public String getConfigId() {
	    return "RM1";
	}
}
