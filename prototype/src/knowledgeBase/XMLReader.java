package knowledgeBase;

import java.util.Hashtable;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import logic.And;
import logic.Fact;
import logic.Not;
import logic.Or;
import logic.TruthState;

public abstract class XMLReader {

	public static KnowledgeBase read(String xmlFilePath){
		KnowledgeBase kb = new KnowledgeBase();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			class KBHandler extends DefaultHandler {
				boolean goal = false;
				boolean answer = false;
				boolean rule = false;
				boolean condition = false;
				boolean fact = false;
				boolean consequence = false;
				boolean question = false;
				boolean option = false;
				boolean description = false;
				boolean comment = false;
				boolean text = false;
				boolean defaultGoal = false;
				
				String newGoalName;
				String newGoalValue;
				Rule newRule = new Rule();
				String newFactName;
				Stack<TruthState> newCondition;
				Question newQuestion = new Question();
				String newOptionDescription;
				Hashtable<String,String> newOptionConsequences = new Hashtable<String,String>();
				
				public void startDocument() throws SAXException{}

				public void endDocument() throws SAXException{}

				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					//System.out.println("Start " + qName);
					switch(qName){
					case("goal"):
						newGoalName = attributes.getValue("name");
						goal = true;
						break;
					case("answer"):
						newGoalValue = attributes.getValue("value");
						answer = newGoalValue != null;//guarantees attribute is present
						break;
					case("rule"):
						rule = true;
						break;
					case("if"):
						newCondition = new Stack<TruthState>();
						condition = true;
						break;
					case("and"):
						newCondition.push(new And());
						break;
					case("or"):
						newCondition.push(new Or());
						break;
					case("not"):
						newCondition.push(new Not());
						break;
					case("fact"):
						newFactName = attributes.getValue("name");
						fact = newFactName != null;
						break;
					case("then"):
						consequence = true;
						break;
					case("question"):
						question = true;
						break;
					case("option"):
						option = true;
						break;
					case("description"):
						description = true;
						break;
					case("comment"):
						comment = true;
						break;
					case("text"):
						text = true;
						break;
					case("default"):
						defaultGoal = true;
						break;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					switch(qName){
						case("goal"):
							goal = false;
							newGoalName = null;
							break;
						case("answer"):
							answer = false;
							newGoalValue = null;
							break;
						case("rule"):
							rule = false;
							newRule.setCondition(newCondition.pop());
							newCondition = null;
							kb.addRule(newRule);
							newRule = new Rule();
							break;
						case("if"):
							condition = false;
							break;
						case("and"):
							if(newCondition.size() > 1){
								TruthState and = newCondition.pop();//take the current layer from stack
								newCondition.peek().add(and);//nest it in the next layer (peels down to root)
							}
							break;
						case("or"):
							if(newCondition.size() > 1){
								TruthState or = newCondition.pop();
								newCondition.peek().add(or);
							}
							break;
						case("not"):
							if(newCondition.size() > 1){
								TruthState not = newCondition.pop();
								newCondition.peek().add(not);
							}
							break;
						case("fact"):
							fact = false;
							newFactName = new String();
							break;
						case("then"):
							consequence = false;
							break;
						case("question"):
							question = false;
							kb.addQuestion(newQuestion);
							newQuestion = new Question();
							break;
						case("option"):
							newQuestion.addOption(newOptionDescription, newOptionConsequences);
							newOptionDescription = null;
							newOptionConsequences = new Hashtable<String,String>();
							option = false;
							break;
						case("description"):
							description = false;
							break;
						case("comment"):
							comment = false;
							break;
						case("text"):
							text = false;
							break;
						case("default"):
							defaultGoal = false;
							break;
					}
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					if (rule){
						if (condition && fact){
							if(newCondition.empty()){
								newCondition.push(new Fact(newFactName,new String(ch, start, length)));
							}else{
								newCondition.peek().add(new Fact(newFactName,new String(ch, start, length)));
							}
						}
						if (consequence && fact){
							newRule.addConsequence(newFactName, new String(ch, start, length));
						}
					}
					if (goal && answer){
						kb.addGoal(newGoalName, newGoalValue, new String(ch,start,length));
					}
					if (defaultGoal){
						kb.getGoals().setDefaultGoal(new String(ch,start,length));
					}
					if(question){
						if(option){
							if(description){
								newOptionDescription = new String(ch, start, length);
							}
							if(consequence && fact){
								newOptionConsequences.put(newFactName, new String(ch, start, length));
							}
						}else{
							if(description){
								newQuestion.setQuestion(new String(ch, start, length));
							}
						}
					}
					if(comment){
						if(condition && fact){
							if(newCondition.empty()){
								newCondition.push(new Fact(newFactName,new String(ch, start, length)));
							}else{
								newCondition.peek().add(new Fact(newFactName,new String(ch, start, length)));
							}
						}
						if(consequence && text){
							kb.addComment(newCondition.pop(), new String(ch, start, length));
							newCondition = null;
						}
					}
				}
			}

			saxParser.parse(xmlFilePath, new KBHandler());
		} catch(Exception e){
			e.printStackTrace();
		}

		return kb;
	}
}
