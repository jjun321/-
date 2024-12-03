package kr.co.example.firebaseregister;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TimerActivity extends AppCompatActivity {
    private TextView timerText;
    private Button startButton, pauseButton;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private boolean isRunning = false;
    private long elapsedTime = 0L;

    private DatabaseReference mDatabase; // Firebase Database Reference

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime + elapsedTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hours = minutes / 60;
            minutes = minutes % 60;

            timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    handler.post(timerRunnable);
                    isRunning = true;
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    handler.removeCallbacks(timerRunnable);
                    elapsedTime += System.currentTimeMillis() - startTime;
                    saveElapsedTime(elapsedTime);  // 학습 시간 저장
                    isRunning = false;
                }
            }
        });
    }

    private void saveElapsedTime(long timeInSeconds) {
        // 현재 로그인한 사용자의 ID 가져오기
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Realtime Database에 학습 시간 저장
        mDatabase.child("users").child(userId).child("learningTime").setValue(timeInSeconds);
    }
}
