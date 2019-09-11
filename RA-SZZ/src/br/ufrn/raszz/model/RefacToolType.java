package br.ufrn.raszz.model;

public enum RefacToolType {
	
	REFDIFF("refdiff"), 
	RMINER("rminer"), 
	BOTH(null);

	private final String text;

	/**
	 * @param text
	 */
	RefacToolType(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
}
