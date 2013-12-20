/*
* A class to represent all the questions in the game.
*/

import java.util.*;

public class Question {
	//Holds the question
	private String question;
	//Holds the answer to the question
	private String answer;

	//Do nothing...
	public Question() {}
	
	public Question(String question, String answer) {
		this.question = question;
		this.answer = answer;		
	}
	
	public void setQuestion(String question,String answer) {
		this.question = question;
		this.answer = answer;
	}

	public String getQuestion() {
		return this.question;
	}

	public String getAnswer() {
		return this.answer;
	}

}

