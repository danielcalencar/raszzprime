package br.ufrn.raszz.miner.szz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.Repository;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.miner.Miner;
import br.ufrn.raszz.model.RefacToolType;
import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.SZZImplementationType;
import br.ufrn.raszz.persistence.DAOType;
import br.ufrn.raszz.persistence.FactoryDAO;
import br.ufrn.raszz.persistence.SzzDAO;
import br.ufrn.razszz.connectoradapter.GitRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SvnRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import core.connector.service.GitService;
import core.connector.service.SvnService;
import core.connector.service.impl.GitServiceImpl;
import core.connector.service.impl.SvnServiceImpl;

public class RaSZZ extends Miner {

	private static final Logger log = Logger.getLogger(RaSZZ.class);
	private SzzDAO szzDAO;
	private SzzRepository repository;

	//{{{ main()
	public static void main(String[] args) throws Exception {
		RaSZZ szz = new RaSZZ();
		String projectTokens = szz.getProperty("git_projects","/backhoe.properties");
		String gitUrlsTokens = szz.getProperty("git_repos","/backhoe.properties");
		String svnUrlsTokens = szz.getProperty("svn_repos","/backhoe.properties");
		String[] projects = projectTokens.split(",");
		String[] gitUrls = gitUrlsTokens.split(",");
		String[] svnUrls = svnUrlsTokens.split(",");
		for(int i=0; i < projects.length; i++){
			String project = projects[i];
			String remoteUrl = null;
			log.info("running SZZ for project: " + project);
			RepositoryType repoType = RepositoryType.GIT;
			SZZImplementationType szzType = SZZImplementationType.RASZZ;

			switch(repoType){
				case GIT: remoteUrl = gitUrls[i];
					  break;
				case SVN: remoteUrl = svnUrls[i];
					  break;
			}
			log.info("and remoteUrl: " + remoteUrl);
			szz.init(project, remoteUrl, repoType, szzType, false, null);
		}
		//String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase" }; 
		//String[] projects = { "Geronimo", "Hadoop Common", "HBase" };
		//String[] projects = { "Mahout", "OpenJPA", "Pig", "Tuscany" };
		//String[] projects = { "jfreechart", "commons-lang", "commons-math"}; //svn
		//String[] projects = { "joda-time", "closure-compiler" , "mockito" }; //git
		//String[] projects = {"mockito"};
	}	
	//}}}
	
	//{{{ public void init()
	public void init(String project, String remoteUrl, RepositoryType repoType,
			SZZImplementationType szzType, boolean isTest, String[]
			debugInfos) throws Exception {
		String user = this.getProperty("user","/backhoe.properties");
		String password = this.getProperty("password","/backhoe.properties");		
		String tmpfolder =  this.getProperty("tmpfolder","/backhoe.properties");
		String gitRepos = this.getProperty("git_repos","/backhoe.properties");
		boolean isDefect4j = Boolean.valueOf(this.getProperty("is_defect4j","/backhoe.properties"));
		//String[] urls = null;
		String localUrl = null;
		switch (repoType) {
		case GIT:
			//if (projects[0].equals("closure-compiler"))
			//	url = "https://github.com/google/closure-compiler.git";
			//if (projects[0].equals("mockito"))
			//	url = "https://github.com/mockito/mockito.git";
			//if (projects[0].equals("joda-time"))
			//	url = "https://github.com/JodaOrg/joda-time.git";
			//urls = gitRepos.split(","); 
			tmpfolder += "gitfiles"+ File.separator + project; //TODO nao dar rodar mais um assim
			localUrl = tmpfolder + ".git";
			break;
		case SVN:
			tmpfolder += "svnfiles"+ File.separator;
			//localUrl = this.getProperty("svn_url","/backhoe.properties");
                        localUrl = remoteUrl;
			break;
		}	
		boolean entireDb = Boolean.valueOf(this.getProperty("entire_db", "/backhoe.properties"));
		String linkedRev = null, debugPath = null, debugContent = null;
		if (isTest) {
			linkedRev = debugInfos[0];
			debugPath = debugInfos[1]; 
			debugContent = debugInfos[2];
		} 
		
		Map<String, Object> p = new HashMap<String, Object>();
		try {
			p.put("user", user);
			p.put("password", password);
			p.put("tmpfolder", tmpfolder);
			p.put("repoType", repoType);
			p.put("szzType", szzType);
			p.put("repoUrl", localUrl);
			p.put("remoteUrl", remoteUrl);
			p.put("project", project);
			p.put("entireDb", entireDb);
			p.put("isTest", isTest);			
			p.put("debugRev", linkedRev);
			p.put("debugPath", debugPath);
			p.put("debugContent", debugContent);
			p.put("isDefect4j", isDefect4j);			
			this.setParameters(p);
			this.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	//}}}
	
	//{{{ performSetup()
	@Override
	public void performSetup() throws Exception {
		log.info("perform setup ... ");
		try {
			String user = (String) this.getParameters().get("user");
			String password = (String) this.getParameters().get("password");
			String tmpfolder = (String) this.getParameters().get("tmpfolder");
			RepositoryType repoType = (RepositoryType) this.getParameters().get("repoType");
			String repoUrl = (String) this.getParameters().get("repoUrl");
			String remoteUrl = (String) this.getParameters().get("remoteUrl");
			
			switch (repoType) {
			case GIT:
				GitService gitService = new GitServiceImpl();
				log.info("cloning from remote: " + remoteUrl);
				Repository gitRepository = gitService.cloneIfNotExists(tmpfolder, remoteUrl); //TODO Pode melhorar!
				repository = new GitRepositoryAdapter(gitRepository, repoUrl, tmpfolder);
				break;
			case SVN:
				SvnService svnService = new SvnServiceImpl();
				SVNRepository svnRepository = svnService.openRepository(repoUrl, user, password);
				repository = new SvnRepositoryAdapter(svnRepository, user, password, repoUrl, tmpfolder);
				break;
			}			
			
			szzDAO = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getSzzDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//}}}

	//{{{ performMining(){
	@Override
	public void performMining() throws Exception {
		log.info("perform mining...");
		final boolean buildAnnotationGraph = Boolean.valueOf(getProperty("build_graph", "/backhoe.properties"));
		//final boolean findBugIntroducingChanges = Boolean.valueOf(getProperty("find_bug_code", "./backhoe.properties"));
		if (buildAnnotationGraph) 
			buildAnnotationGraph();
	}
	//}}}
	
	//{{{ configureInputRevisions(){{{
	private List<String> configureInputRevisions(String project) {
		boolean entireDb = (Boolean) this.getParameters().get("entireDb");
		boolean isTest = (Boolean) this.getParameters().get("isTest");	
		boolean isDefect4j = (Boolean) this.getParameters().get("isDefect4j");	
		
		List<String> linkedRevs = null;
		if (isTest) {
			String linkedTestRev = (String) this.getParameters().get("debugRev");
			linkedRevs = new ArrayList<String>();
			linkedRevs.add(linkedTestRev);		
		} else {		
			synchronized(szzDAO){		
				if (isDefect4j){						
					log.info("getting linked revisions from defect4j!");
					linkedRevs = szzDAO.getGitLinkedRevision(project);
				}
				else {
					log.info("getting linked revisions!");
					linkedRevs = (entireDb)? szzDAO.getLinkedRevisions(project):
											 szzDAO.getLinkedRevisionsWProcessedBIC(project);							
				}
			}	
		}
		return linkedRevs;
	}
	//}}}
	
	// {{{ buildAnnotationGraph()
	private void buildAnnotationGraph() throws Exception {
		try {
			String project = (String) this.getParameters().get("project");
			String repoUrl = (String) this.getParameters().get("repoUrl");			
			String debugPath = (String) this.getParameters().get("debugPath");
			String debugContent = (String) this.getParameters().get("debugContent");	
			boolean isTest = (Boolean) this.getParameters().get("isTest");	
			
			//for (int j = 0; j < projects.length; j++) {			
			List<String> linkedRevs = configureInputRevisions(project);				
			SZZImplementationType szzType = (SZZImplementationType) this.getParameters().get("szzType");		
			AnnotationGraphService worker = null;
			switch (szzType) {
				case RASZZ:
					RefacToolType refacTool = RefacToolType.BOTH;
					worker = new TraceBackRaSZZ(repository, szzDAO, project, 
							linkedRevs, repoUrl, debugPath, debugContent, szzType, refacTool, isTest);
					break;
				case MASZZ:
					worker = new TraceBackMaSZZ(repository, szzDAO, project, 
							linkedRevs, repoUrl, debugPath, debugContent, szzType, isTest);
					break;									
			}
			worker.run();
			//}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	//}}}
}
