package org.refactoringminer.rm1;

import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.refactoringminer.api.HistoryRefactoringMiner;
import org.refactoringminer.api.RMService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HistoryRefactoringMinerImpl<T> implements HistoryRefactoringMiner<T> {

	Logger logger = LoggerFactory.getLogger(HistoryRefactoringMinerImpl.class);
	protected Set<RefactoringType> refactoringTypesToConsider = null;
	
	public HistoryRefactoringMinerImpl() {
		this.setRefactoringTypesToConsider(
			RefactoringType.RENAME_CLASS,
			RefactoringType.MOVE_CLASS,
			RefactoringType.MOVE_SOURCE_FOLDER,
			RefactoringType.RENAME_METHOD,
			RefactoringType.EXTRACT_OPERATION,
			RefactoringType.INLINE_OPERATION,
			RefactoringType.MOVE_OPERATION,
			RefactoringType.PULL_UP_OPERATION,
			RefactoringType.PUSH_DOWN_OPERATION,
			RefactoringType.MOVE_ATTRIBUTE,
			RefactoringType.PULL_UP_ATTRIBUTE,
			RefactoringType.PUSH_DOWN_ATTRIBUTE,
			RefactoringType.EXTRACT_INTERFACE,
			RefactoringType.EXTRACT_SUPERCLASS,
			RefactoringType.EXTRACT_AND_MOVE_OPERATION,
			RefactoringType.RENAME_PACKAGE
		);
	}

	public void setRefactoringTypesToConsider(RefactoringType ... types) {
		this.refactoringTypesToConsider = new HashSet<RefactoringType>();
		for (RefactoringType type : types) {
			this.refactoringTypesToConsider.add(type);
		}
	}
}
