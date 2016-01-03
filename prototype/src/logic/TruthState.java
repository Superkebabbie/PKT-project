package logic;

import knowledgeBase.KnowledgeBase;

public interface TruthState {

	public boolean valuate(KnowledgeBase kb);
	public void add(TruthState ts);
}
