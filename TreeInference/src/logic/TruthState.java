package logic;

import java.util.ArrayList;
import java.util.HashMap;

import knowledgeBase.KnowledgeBase;

public interface TruthState {
	public boolean proven(KnowledgeBase kb);
	public boolean disproven(KnowledgeBase kb);
	public ArrayList<TruthState> backwardChain(KnowledgeBase kb);
	public void add(TruthState ts);
	public double factsUsefulness(HashMap<String,String> facts);//for use with the consequences of a question to see whether that question is good to ask.
	public TruthState cloneDeep();
}
