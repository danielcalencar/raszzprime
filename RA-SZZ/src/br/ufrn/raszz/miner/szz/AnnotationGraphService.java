package br.ufrn.raszz.miner.szz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.model.SZZImplementationType;
import br.ufrn.raszz.model.szz.AnnotationGraphModel;
import br.ufrn.raszz.model.szz.BugIntroducingCode;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import br.ufrn.raszz.persistence.SzzDAO;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import org.eclipse.jgit.errors.MissingObjectException;

public abstract class AnnotationGraphService implements Runnable {

	protected static final Logger log = Logger.getLogger(AnnotationGraphService.class);
	protected static SzzDAO szzDAO;
	protected SzzRepository repository;
	protected String project;
	protected String repoUrl;
	protected List<BugIntroducingCode> bicodes;
	protected List<String> linkedRevs;
	protected String debugPath;
	protected String debugContent;
	protected SZZImplementationType szzType;
	protected boolean isTest;
	protected List<String> processedRevisions = new ArrayList<String>();
	protected int threadId;

	public AnnotationGraphService(SzzRepository repository, SzzDAO szzDao,
			String project, List<String> linkedRevs, String repoUrl, 
			String debugPath, String debugContent, SZZImplementationType szzType, boolean isTest,
			List<String> processedRevisions, int threadId) {
		this.repository = repository;
		this.setDao(szzDao);
		this.project = project;
		this.repoUrl = repoUrl;
		this.linkedRevs = linkedRevs;
		this.debugPath = debugPath;
		this.debugContent = debugContent;
		this.szzType = szzType;
		this.isTest = isTest;
		this.processedRevisions = processedRevisions;
		this.threadId = threadId;
	}

	private static void setDao(SzzDAO dao){
		szzDAO = dao;
	}

	@Override
	public void run() {
		try {
			log.info(Thread.currentThread().getName() + "-" + project + " is running.");
			long startTime = System.nanoTime();
			buildAnnotationGraph(linkedRevs);
			long endTime = System.nanoTime();
			log.info("duration: " + (endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean buildAnnotationGraph(List<String> linkedRevs) throws Exception {

		//List<String> processedRevisions = isTest? null: szzDAO.getAllRevisionProcessed(project);
		log.info("Project " + project + " starting analysis with " + szzType);
		log.info(String.format("%d linked revisions found...",linkedRevs.size()));
		long count = 1;
		for (String i : linkedRevs) {
			try {
				//specific revisions to ignore here.
				if(i.equals("dece1ccfeccfe6e331829e88d1d12c991f2a3d21") || i.equals("806ce6a360c10773207b508409152df0d5d4eb8a")){
					log.info(String.format("ignoring revision %s as it leads to crazy memory errors",i));
					continue;
				}
				if (!isTest) {
					//in case we needed to stop the process
					if (processedRevisions.contains(i)) {
						log.info(String.format("revindex:%d: Revision %s was processed already!",count,i));
						count++;
						continue;
					}
				}
				AnnotationGraphModel model = new AnnotationGraphModel();			
				bicodes = new ArrayList<BugIntroducingCode>();
				List<String> paths = repository.getChangedPaths(i);
				for(String path: paths) {
					//log.info("path: " + path + "(#" + i + ")");
					if (debugPath != null && !path.equals(debugPath)) continue;	
					try {
						final LinkedList<SzzFileRevision> szzFileRevisions = repository.extractSZZFilesFromPath(repoUrl, path, i, false);
						if (szzFileRevisions == null) continue;
						AnnotationGraphBuilder agb = new AnnotationGraphBuilder();
						model = agb.buildLinesModel(repository, szzFileRevisions, repoUrl, project);
						traceBack(model,szzFileRevisions);
						log.info( "bics: " + bicodes.size()); 
					} catch (Exception e) {
						log.error("Error in the path: " + path);
						continue;
					}
				}
				synchronized(szzDAO){
					Transaction tx = szzDAO.beginTransaction();
					for(BugIntroducingCode bicode : bicodes){
						szzDAO.insertBugIntroducingCode(bicode, szzType);
					}
					szzDAO.insertProjectRevisionsProcessed(project, i);
					tx.commit();
				}
			} catch (MissingObjectException e) {
				log.error(String.format("skipping revision %s because it is a bad object",i));
				szzDAO.insertProjectRevisionsProcessed(project, i);
			} catch (ArrayIndexOutOfBoundsException aie) {
				log.error(String.format("skipping revision %s because of an index bound error",i));
				szzDAO.insertProjectRevisionsProcessed(project, i);
			}
			log.info(String.format("thread:%d: %d processed revisions of %d for project %s", threadId, count++, linkedRevs.size(), project));
		}
		return true;
	}		
	
	protected abstract void traceBack(AnnotationGraphModel model, 
			LinkedList<SzzFileRevision> fileRevisions) throws Exception;
	
}
