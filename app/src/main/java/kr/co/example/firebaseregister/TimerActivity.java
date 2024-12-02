package kr.co.example.firebaseregister;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TimerActivity extends AppCompatActivity {
    private Spinner subjectSpinner;
    private TextView timerText;
    private Button startButton, pauseButton;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private boolean isRunning = false;
    private long elapsedTime = 0L;

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

        // SharedPreferences에서 타이머 데이터 불러오기
        elapsedTime = loadElapsedTime();

        // 타이머 시작
        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis();
                handler.post(timerRunnable);
                isRunning = true;
            }
        });

        // 타이머 일시정지
        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                handler.removeCallbacks(timerRunnable);
                elapsedTime += System.currentTimeMillis() - startTime;
                saveElapsedTime(elapsedTime);  // 데이터 저장
                isRunning = false;
            }
        });

        // 매일 00시에 초기화
        scheduleDailyReset();
    }

    private void scheduleDailyReset() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 00시 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // 다음 00시로 설정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // AlarmManager로 매일 00시 실행
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    // SharedPreferences에 데이터 저장
    private void saveElapsedTime(long elapsedTime) {
        SharedPreferences sharedPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("elapsedTime", elapsedTime);
        editor.apply();
    }

    // SharedPreferences에서 데이터 로드
    private long loadElapsedTime() {
        SharedPreferences sharedPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        return sharedPreferences.getLong("elapsedTime", 0);
    }
}
