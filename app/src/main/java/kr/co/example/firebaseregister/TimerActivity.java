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
    private Button toggleButton, resetButton;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private boolean isRunning = false;
    private long elapsedTime = 0L;

    private DatabaseReference mDatabase;
    private Runnable saveToFirebaseRunnable;

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
        toggleButton = findViewById(R.id.toggleButton);
        resetButton = findViewById(R.id.resetButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // SharedPreferences에서 저장된 타이머 상태 복원
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("startTime", 0L);
        elapsedTime = prefs.getLong("elapsedTime", 0L);
        isRunning = prefs.getBoolean("isRunning", false);

        // 초기화 버튼 비활성화 및 색상 설정
        resetButton.setEnabled(false);
        resetButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // 비활성화 시 회색


        // 토글 버튼 클릭 리스너 설정
        toggleButton.setOnClickListener(v -> {
                    if (isRunning) {
                        handler.post(timerRunnable);
                        startFirebaseSaving();
                        toggleButton.setText("일시정지");
                        toggleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        updateTimerText(elapsedTime);
                        toggleButton.setText("시작");
                        toggleButton.setBackgroundColor(getResources().getColor(android.R.color.black));
                    }
                });

        //초기화 버튼 클릭 리스너 설정

        // 초기화 버튼 클릭 리스너 설정 (여기에 코드 삽입)
        resetButton.setOnClickListener(v -> {
            // 타이머 정지
            handler.removeCallbacks(timerRunnable);
            stopFirebaseSaving(); // Firebase 저장 중단

            // 타이머 초기화
            elapsedTime = 0L; // 경과 시간 초기화
            startTime = System.currentTimeMillis(); // 새 시작 시간 설정
            updateTimerText(elapsedTime); // 타이머 텍스트 업데이트

            // 타이머 상태 갱신
            isRunning = false;
            toggleButton.setText("시작");
            toggleButton.setBackgroundColor(getResources().getColor(android.R.color.black));

            // 초기화 버튼 비활성화
            resetButton.setEnabled(false);
            resetButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

            // 타이머 상태 저장
            saveTimerState(); // SharedPreferences에 상태 저장
        });

        toggleButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis();
                handler.post(timerRunnable);
                startFirebaseSaving(); // Firebase 저장 시작
                isRunning = true;
                toggleButton.setText("일시정지");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                resetButton.setEnabled(true);
                resetButton.setBackgroundColor(getResources().getColor(android.R.color.black));
                saveTimerState();
            } else {
                handler.removeCallbacks(timerRunnable);
                stopFirebaseSaving(); // Firebase 저장 중단
                elapsedTime += System.currentTimeMillis() - startTime;
                saveElapsedTime(elapsedTime); // 누적 시간 저장
                isRunning = false;
                toggleButton.setText("시작");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.black));
                saveTimerState();
            }
        });

        resetButton.setOnClickListener(v -> {
            handler.removeCallbacks(timerRunnable);
            stopFirebaseSaving(); // Firebase 저장 중단
            elapsedTime = 0L;
            startTime = System.currentTimeMillis();
            updateTimerText(elapsedTime);
            isRunning = false;
            toggleButton.setText("시작");
            toggleButton.setBackgroundColor(getResources().getColor(android.R.color.black));
            resetButton.setEnabled(false);
            resetButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            saveTimerState();
        });
    }

    private void startFirebaseSaving() {
        saveToFirebaseRunnable = new Runnable() {
            @Override
            public void run() {
                long currentElapsedTime = System.currentTimeMillis() - startTime + elapsedTime;
                saveElapsedTime(currentElapsedTime);
                handler.postDelayed(this, 1000); // 1초마다 저장
            }
        };
        handler.post(saveToFirebaseRunnable);
    }

    private void stopFirebaseSaving() {
        if (saveToFirebaseRunnable != null) {
            handler.removeCallbacks(saveToFirebaseRunnable);
        }
    }

    private void updateTimerText(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hours = minutes / 60;
        minutes = minutes % 60;

        timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
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

            mDatabase.child("users").child(userId).child("currentTimer").setValue(timeInMillis / 1000)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TimerActivity", "Firebase에 실시간 시간 저장 성공: " + (timeInMillis / 1000) + "초");
                        } else {
                            Log.e("TimerActivity", "Firebase에 실시간 시간 저장 실패", task.getException());
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
        stopFirebaseSaving(); // Firebase 저장 중단
        saveTimerState();
    }
}
