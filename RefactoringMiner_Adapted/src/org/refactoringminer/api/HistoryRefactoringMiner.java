package org.refactoringminer.api;

import java.util.List;

import org.eclipse.jgit.lib.Repository;

/**
 * Detect refactorings in the git history.
 * @param <T>
 * 
 */
public interface HistoryRefactoringMiner<T> {

	/**
	 * Iterate over each commit of a git repository and detect all refactorings performed in the
	 * entire repository history. Merge commits are ignored to avoid detecting the same refactoring 
	 * multiple times.
	 * 
	 * @param repository A git repository (from JGit library).
	 * @param branch A branch to start the log lookup. If null, commits from all branches are analyzed.
	 * @param handler A handler object that is responsible to process the detected refactorings and
	 *                control when to skip a commit. 
	 * @throws Exception propagated from JGit library.
	 */
	void detectAll(T repository, String branch, RefactoringHandler handler) throws Exception;

	/**
	 * Iterate over commits between two release tags of a git repository and detect refactorings performed
	 * in the entire repository history. Merge commits are ignored to avoid detecting the same refactoring 
	 * multiple times.
	 * 
	 * @param repository A git repository (from JGit library).
	 * @param startTag An annotated tag to start the log lookup.
	 * @param endTag An annotated tag to end the log lookup.
	 * @param handler A handler object that is responsible to process the detected refactorings and
	 *                control when to skip a commit. 
	 * @throws Exception propagated from JGit library.
	 */
	void detectBetweenTags(T repository, String startTag, String endTag, RefactoringHandler handler)
			throws Exception;
	
	/**
	 * Iterate over commits between two commits of a git repository and detect refactorings performed
	 * in the entire repository history. Merge commits are ignored to avoid detecting the same refactoring 
	 * multiple times.
	 * 
	 * @param repository A git repository (from JGit library).
	 * @param startCommitId The SHA key that identifies the commit to start the log lookup.
	 * @param endCommitId The SHA key that identifies the commit to end the log lookup.
	 * @param handler A handler object that is responsible to process the detected refactorings and
	 *                control when to skip a commit. 
	 * @throws Exception propagated from JGit library.
	 */
	void detectBetweenCommits(T repository, String startCommitId, String endCommitId, RefactoringHandler handler)
			throws Exception;
	
	/**
	 * Fetch new commits from the remote repo and detect all refactorings performed in these
	 * commits.
	 * 
	 * @param repository A git repository (from JGit library).
	 * @param handler A handler object that is responsible to process the detected refactorings and
	 *                control when to skip a commit. 
	 * @throws Exception propagated from JGit library.
	 */
	void fetchAndDetectNew(T repository, RefactoringHandler handler) throws Exception;

	/**
	 * Detect refactorings performed in the specified commit. 
	 * 
	 * @param repository A git repository (from JGit library).
	 * @param cloneURL The clone URL of the repository.
	 * @param commitId The SHA key that identifies the commit.
	 * @param handler A handler object that is responsible to process the detected refactorings. 
	 */
	void detectAtCommit(T repository, String cloneURL, String commitId, String folder, RefactoringHandler handler);

	/**
	 * @return An ID that represents the current configuration for the Refactoring Miner algorithm in use.
	 */
	String getConfigId();
}
