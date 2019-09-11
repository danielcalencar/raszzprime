package org.refactoringminer.api;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Simple service to make git related tasks easier.  
 *
 */
public interface RMService<T> {

	/**
	 * Clone the git repository given by {@code cloneUrl} only if is does not exist yet in {@code folder}.
	 * 
	 * @param folder The folder to store the local repo.
	 * @param cloneUrl The repository URL.
	 * @return The repository object (JGit library).
	 * @throws Exception propagated from JGit library.
	 */
	Repository cloneIfNotExists(String folder, String cloneUrl/*, String branch*/) throws Exception;
	
	Repository openRepository(String folder) throws Exception;

	int countCommits(T repository, String branch) throws Exception;

	void checkout(T repository, String commitId) throws Exception;

	RevWalk fetchAndCreateNewRevsWalk(T repository) throws Exception;

	RevWalk fetchAndCreateNewRevsWalk(T repository, String branch) throws Exception;

	RevWalk createAllRevsWalk(T repository) throws Exception;

	RevWalk createAllRevsWalk(T repository, String branch) throws Exception;

	RevWalk createRevsWalkBetweenTags(T repository, String startTag, String endTag) throws Exception;

	RevWalk createRevsWalkBetweenCommits(T repository, String startCommitId, String endCommitId) throws Exception;

	void fileTreeDiff(T repository, RevCommit currentCommit, List<String> filesBefore, List<String> filesCurrent, Map<String, String> renamedFilesHint, boolean detectRenames) throws Exception;
}
