import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.HistoryRefactoringMiner;
import org.refactoringminer.api.RMService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Main {

	public static void main(String[] args) {
		RMService<Repository> gitService = new GitServiceImpl<Repository>();
		HistoryRefactoringMiner<Repository> miner = new GitHistoryRefactoringMinerImpl<Repository>();
		
		try {
			Repository repo = gitService.cloneIfNotExists(
			    //"tmp/refactoring-toy-example",
			    //"https://github.com/danilofes/refactoring-toy-example.git");
					"C://tmp/gitfiles/joda-time",
				    "https://github.com/JodaOrg/joda-time.git");
/**/
			miner.detectAll(repo, "master", new RefactoringHandler<RevCommit>() {
			  @Override
			  public void handle(RevCommit commitData, List<Refactoring> refactorings) {
			    System.out.println("Refactorings at " + commitData.getId().getName());
			    for (Refactoring ref : refactorings) {
			      System.out.println(ref.toString());
			      
			      if (ref.getBeforeLocationInfo() != null)
				      System.out.println("BEFORE: " + (int)(ref.getBeforeLocationInfo().getStartLine()+1) + " - " 
						      + (int)(ref.getBeforeLocationInfo().getEndLine()+1)
						      + " - " +  ref.getBeforeLocationInfo().getFilePath());
				      System.out.println("AFTER: " + (int)(ref.getAfterLocationInfo().getStartLine()+1) + " - " 
				      + (int)(ref.getAfterLocationInfo().getEndLine()+1)
				      + " - " +  ref.getAfterLocationInfo().getFilePath());
				      
				      		      			      
			    }
			  }
			});
			
			/*
			miner.detectAtCommit(repo, "https://github.com/danilofes/refactoring-toy-example.git",
				    "05c1e773878bbacae64112f70964f4f2f7944398", new RefactoringHandler() {
				  @Override
				  public void handle(RevCommit commitData, List<Refactoring> refactorings) {
				    System.out.println("Refactorings at " + commitData.getId().getName());
				    for (Refactoring ref : refactorings) {
				      System.out.println(ref.toString());	

				      if (ref.getBeforeLocationInfo() != null)
				      System.out.println("BEFORE: " + ref.getBeforeLocationInfo().getStartLine() + " - " 
						      + ref.getBeforeLocationInfo().getEndLine());
				      System.out.println("AFTER: " + ref.getAfterLocationInfo().getStartLine() + " - " 
				      + ref.getAfterLocationInfo().getEndLine());				      
				      
				      //ref.getRefactoringType().get
				      
				      
				    }
				  }
				});*/
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
