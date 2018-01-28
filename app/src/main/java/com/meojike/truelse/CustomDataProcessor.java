package com.meojike.truelse;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class CustomDataProcessor {

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    private DatabaseReference databaseReference;

    private int questionIndex = 0;
    private ArrayList<TruElse> questionBankArray = new ArrayList<>();
    private TruElse processedTruElseInstance;
    private int progressIncrement;


    public CustomDataProcessor(FirebaseDatabase firebaseDatabase, String childNode) {
        this.databaseReference = firebaseDatabase.getReference().child(childNode);
    }


    public void getAndSetData(DataSnapshot dataSnapshot) {
            for (DataSnapshot truElseSnapshot : dataSnapshot.getChildren()) {
                TruElse truElseUnit = truElseSnapshot.getValue(TruElse.class);
                questionBankArray.add(truElseUnit);
            }

        processedTruElseInstance = questionBankArray.get(questionIndex);
        progressIncrement = (int) Math.ceil(100.0 / questionBankArray.size());
    }

    public void switchToNextQuestion() {
          questionIndex++;
          processedTruElseInstance = questionBankArray.get(questionIndex);
    }

    public TruElse getCurrentTruElseInstance() {
        return processedTruElseInstance;
    }

    public int getQuestionsCount() {
        return questionBankArray.size();
    }

    public int getProgressIncrement() {
        return progressIncrement;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public boolean allQuestionsWereAsked() {
        return (getQuestionIndex() + 1) % getQuestionsCount() == 0;
    }

}
