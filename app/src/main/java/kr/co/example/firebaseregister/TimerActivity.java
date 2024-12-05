package kr.co.example.firebaseregister;

import android.content.SharedPreferences;
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
    private Button startButton, pauseButton, resetButton;

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
        resetButton = findViewById(R.id.resetButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // SharedPreferences에서 저장된 타이머 상태 복원
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("startTime", 0L);
        elapsedTime = prefs.getLong("elapsedTime", 0L);
        isRunning = prefs.getBoolean("isRunning", false);

        // 타이머 상태가 일시정지 상태인 경우, startTime을 이전 상태로 복원
        if (isRunning) {
            handler.post(timerRunnable); // 타이머가 실행 중이면 바로 실행
        } else {
            // 타이머가 멈춰있는 경우, 타이머 텍스트 업데이트
            long millis = elapsedTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hours = minutes / 60;
            minutes = minutes % 60;

            timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis();  // 타이머를 새로 시작하기 위해 startTime을 현재 시간으로 설정
                handler.post(timerRunnable);  // 타이머 시작
                isRunning = true;
                saveTimerState();  // 타이머 시작 시 상태 저장
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                handler.removeCallbacks(timerRunnable);  // 타이머 일시 정지
                elapsedTime += System.currentTimeMillis() - startTime;
                saveElapsedTime(elapsedTime);  // 누적 시간 저장
                isRunning = false;
                saveTimerState();  // 타이머 일시 정지 시 상태 저장
            }
        });

        resetButton.setOnClickListener(v -> {
            handler.removeCallbacks(timerRunnable);  // 타이머 정지
            elapsedTime = 0L;  // 누적 시간 초기화
            startTime = System.currentTimeMillis();  // 새로운 시작 시간으로 설정
            timerText.setText("00:00:00");  // 화면에 타이머 초기화 표시
            isRunning = false;
            saveTimerState();  // 타이머 초기화 시 상태 저장
        });
    }

    private void saveTimerState() {
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTime", startTime);
        editor.putLong("elapsedTime", elapsedTime);
        editor.putBoolean("isRunning", isRunning);
        editor.apply();
    }

    private void saveElapsedTime(long timeInMillis) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Firebase에서 기존 학습 시간 가져오기
            mDatabase.child("users").child(userId).child("learningTime")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Long existingTime = task.getResult().getValue(Long.class); // 기존 학습 시간
                            if (existingTime == null) {
                                existingTime = 0L; // 기존 값이 없으면 0으로 초기화
                            }

                            // 누적 시간 계산
                            long newTotalTime = existingTime + (timeInMillis / 1000); // 초 단위로 저장

                            // Firebase에 누적 시간 저장
                            mDatabase.child("users").child(userId).child("learningTime").setValue(newTotalTime)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Log.d("TimerActivity", "학습 시간이 성공적으로 누적되었습니다.");
                                        } else {
                                            Log.e("TimerActivity", "학습 시간 누적 저장 실패");
                                        }
                                    });
                        } else {
                            Log.e("TimerActivity", "기존 학습 시간 가져오기 실패", task.getException());
                        }
                    });
        } else {
            Log.e("TimerActivity", "사용자가 로그인되지 않았습니다.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
        saveTimerState();  // 액티비티 종료 시 타이머 상태 저장
    }
}