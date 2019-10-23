package br.ufrn.raszz.miner.rminer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.hibernate.Transaction;
import org.refactoringminer.api.HistoryRefactoringMiner;
import org.refactoringminer.api.RMService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.rm1.SvnHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.model.RefacToolType;
import br.ufrn.raszz.persistence.DAOType;
import br.ufrn.raszz.persistence.FactoryDAO;
import br.ufrn.raszz.persistence.RefacDAO;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;

public class RMinerService<T> {

	private RefacDAO refacDAO;
	private SzzRepository repository;

	public RMinerService(SzzRepository repository) {
		refacDAO = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getRefacDAO();
		this.repository = repository; 
	}
	
	public List<RefElement> executeRMiner(String project, String commitId, String revisionType) {		
		List<RefElement> refElements = null;
		switch (repository.getConnectorType()) {
		case GIT:
			refElements = executeGit(repository.getUrl(), repository.getRepositoryFolder(), commitId, project, revisionType);
			refacDAO.insertRefacRevisionsProcessed(project, commitId, RefacToolType.RMINER);
			break;
		case SVN:
			refElements =  executeSvn(repository.getUrl(), repository.getUser(), repository.getPassword(), project, commitId, repository.getRepositoryFolder(), revisionType);
			refacDAO.insertRefacRevisionsProcessed(project, commitId, RefacToolType.RMINER);
			break;
		}
		Transaction tx = refacDAO.beginTransaction();
		tx.commit();
		return refElements;		
	}

	private List<RefElement> executeGit(String repoUrl, String repoFolder, String commitId, String project, String revisionType) {		
		RMService<Repository> gitService = new GitServiceImpl<Repository>();
		HistoryRefactoringMiner<Repository> miner = new GitHistoryRefactoringMinerImpl<Repository>();
		List<Refactoring> result = new ArrayList<>();
		try {
			Repository repository = gitService.cloneIfNotExists(repoFolder, repoUrl);
			miner.detectAtCommit(repository, repoUrl, commitId, null, new RefactoringHandler<T>() {
				  @Override
				  public void handle(T commitData, List<Refactoring> refactorings) {
					  result.addAll(refactorings);
				  }
				});			
			return saveResults(result, project, repoFolder, commitId, revisionType);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<RefElement> executeSvn(String repoUrl, String user, String password, String project, String commitId,
			String folder, String revisionType) {
		
		SvnService svnService = new SvnServiceImpl();
		HistoryRefactoringMiner<SVNRepository> miner = new SvnHistoryRefactoringMinerImpl<SVNRepository>();
		List<Refactoring> result = new ArrayList<>();
		
		try {
			SVNRepository repository = svnService.openRepository(repoUrl, user, password);
			
			miner.detectAtCommit(repository, repoUrl, commitId, folder, new RefactoringHandler<T>() {
				  @Override
				  public void handle(T commitData, List<Refactoring> refactorings) {
					  result.addAll(refactorings);
				  }
				});
			return saveResults(result, project, folder, commitId, revisionType);			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<RefElement> saveResults(List<Refactoring> refactorings, String project, String folder, String commitId, String revisionType)
			throws IOException {
		synchronized (refacDAO) {
			List<RefElement> refElements = new ArrayList<RefElement>();
			for (Refactoring refactoring : refactorings) {
				RefactoringDataBuilder refacDataBuilder = new RefactoringDataBuilder();
				List<RefElement> elements = refacDataBuilder.prepareElement(refactoring, project, commitId);

				Transaction tx = refacDAO.beginTransaction();
				for (RefElement element : elements) {
					refacDAO.saveRefacResults(element, revisionType);
				}
				if(!tx.wasCommitted()) tx.commit();
				refElements.addAll(elements);

			}
			if (refactorings != null)
				System.err.println("============ (" + refactorings.size()
						+ " RMINER refactoring data saved with success for commitId: " + commitId + ") ===========");
			return refElements;
		}
	}

	
}
