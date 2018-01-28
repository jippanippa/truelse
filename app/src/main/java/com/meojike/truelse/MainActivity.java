package com.meojike.truelse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    private Button mTrueButton;
    private Button mFalseButton;
    private TextView mQuestionTextView;
    private TextView mScoreTextView;
    private ProgressBar mProgressBar;
    private CustomDataProcessor mCustomDataProcessor;
    private int score;

    private AlphaAnimation mInAnimation;
    private AlphaAnimation mOutAnimation;
    private FrameLayout mProgressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mQuestionTextView = findViewById(R.id.question_text_view);
        mScoreTextView = findViewById(R.id.score);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBarHolder = findViewById(R.id.progressBarHolder);

            turnAnimationOn();

            mCustomDataProcessor = new CustomDataProcessor(FirebaseDatabase.getInstance(), "questions");
            mCustomDataProcessor.getDatabaseReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mCustomDataProcessor.getAndSetData(dataSnapshot);
                    mQuestionTextView.setText(mCustomDataProcessor.getCurrentTruElseInstance().getQuestion());
                    mScoreTextView.setText("Счёт: " + score + " / " + mCustomDataProcessor.getQuestionsCount());
                    turnAnimationOff();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });


        if(savedInstanceState != null) {
            score = savedInstanceState.getInt("ScoreKey");
            mCustomDataProcessor.setQuestionIndex(savedInstanceState.getInt("QuestionIndexKey"));
        } else {
            score = 0;
        }

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true, mCustomDataProcessor.getCurrentTruElseInstance());
                updateQuestion();
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false, mCustomDataProcessor.getCurrentTruElseInstance());
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        if((mCustomDataProcessor.allQuestionsWereAsked())) {
            mScoreTextView.setText("Счёт: " + score + " / " + mCustomDataProcessor.getQuestionsCount());
            mProgressBar.incrementProgressBy(mCustomDataProcessor.getProgressIncrement());
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Вопросы закончились!");
            alert.setCancelable(false);
            alert.setMessage("Вы набрали " + score + " очков");
            alert.setPositiveButton("Закрыть приложение", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alert.setNegativeButton("Заново", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onCreate(null);
                }
            });
            alert.show();
        } else {

            mCustomDataProcessor.switchToNextQuestion();
            mQuestionTextView.setText(mCustomDataProcessor.getCurrentTruElseInstance().getQuestion());
            mScoreTextView.setText("Счёт: " + score + " / " + mCustomDataProcessor.getQuestionsCount());
            mProgressBar.incrementProgressBy(mCustomDataProcessor.getProgressIncrement());

        }
    }

    private void checkAnswer(boolean userSelection, TruElse truElseInstance) {

        if(userSelection == truElseInstance.getAnswer()) {
            Toast.makeText(getApplicationContext(), R.string.correct_toast, Toast.LENGTH_SHORT).show();
            score += 1;
        } else {
            Toast.makeText(getApplicationContext(), R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ScoreKey", score);
        outState.putInt("QuestionIndexKey", mCustomDataProcessor.getQuestionIndex());
    }

    public void turnAnimationOn() {
        mInAnimation = new AlphaAnimation(0f, 1f);
        mInAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(mInAnimation);
        mProgressBarHolder.setVisibility(View.VISIBLE);
        mTrueButton.setVisibility(View.INVISIBLE);
        mFalseButton.setVisibility(View.INVISIBLE);
        mScoreTextView.setVisibility(View.INVISIBLE);
    }

    public void turnAnimationOff() {
        mOutAnimation = new AlphaAnimation(1f, 0f);
        mOutAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(mOutAnimation);
        mProgressBarHolder.setVisibility(View.GONE);
        mTrueButton.setVisibility(View.VISIBLE);
        mFalseButton.setVisibility(View.VISIBLE);
        mScoreTextView.setVisibility(View.VISIBLE);
    }
}
