package kr.co.example.firebaseregister;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

    private DatabaseReference mDatabase;

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

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis();
                handler.post(timerRunnable);
                isRunning = true;
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                handler.removeCallbacks(timerRunnable);
                elapsedTime += System.currentTimeMillis() - startTime;
                saveElapsedTime(elapsedTime);
                isRunning = false;
            }
        });
    }

    private void saveElapsedTime(long timeInMillis) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timeInSeconds = timeInMillis / 1000;
            mDatabase.child("users").child(userId).child("learningTime").setValue(timeInSeconds)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TimerActivity", "학습 시간이 성공적으로 저장되었습니다.");
                        } else {
                            Log.e("TimerActivity", "학습 시간 저장 실패");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("TimerActivity", "학습 시간 저장 중 오류 발생", e));
        } else {
            Log.e("TimerActivity", "사용자가 로그인되지 않았습니다.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}
