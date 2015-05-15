package com.snapdonate.app.model;

public class Faqs {
	private String mAutoId;
	private String mQuestion;
	private String mAnswer;

	public Faqs() {
	}

	public Faqs(String autoId, String question, String answer) {
		super();
		this.mAutoId = autoId;
		this.mQuestion = question;
		this.mAnswer = answer;
	}

	public String getAutoId() {
		return mAutoId;
	}

	public void setAutoId(String autoId) {
		this.mAutoId = autoId;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		this.mQuestion = question;
	}

	public String getAnswer() {
		return mAnswer;
	}

	public void setAnswer(String answer) {
		this.mAnswer = answer;
	}

	@Override
	public String toString() {
		return "Faqs [autoId=" + mAutoId + ", question=" + mQuestion
				+ ", answer=" + mAnswer + ", getAutoId()=" + getAutoId()
				+ ", getQuestion()=" + getQuestion() + ", getAnswer()="
				+ getAnswer() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
}
