package com.meojike.truelse;

public class TruElse {
    private String question;
    private boolean answer;

    public TruElse(String question, boolean answer) {
        this.question = question;
        this.answer = answer;
    }

    public TruElse() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
