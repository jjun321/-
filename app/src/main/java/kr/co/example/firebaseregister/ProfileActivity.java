package kr.co.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvStudyContent, tvStudyDate, tvStudyHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // XML에 정의된 TextView 연결
        tvUserName = findViewById(R.id.UserName);
        tvStudyContent = findViewById(R.id.tvStudyContent);
        tvStudyDate = findViewById(R.id.tvStudyDate);
        tvStudyHours = findViewById(R.id.tvStudyHours);

        // Firebase Auth를 사용해 현재 사용자 정보 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Firebase Database에서 사용자 이름 가져오기
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(uid);

            // 사용자 이름 가져오기
            userRef.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String userName = task.getResult().getValue(String.class);

                    if (userName != null) {
                        tvUserName.setText(userName);
                        tvStudyContent.setText(userName + "님의 총 누적 학습 시간: ");
                    }
                } else {
                    tvUserName.setText("Guest");
                    tvStudyContent.setText("Guest님의 학습 시간");
                }
            });

            // 학습 날짜 가져오기
            userRef.child("study_date").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String studyDate = task.getResult().getValue(String.class);

                    if (studyDate != null) {
                        tvStudyDate.setText(studyDate);
                    } else {
                        tvStudyDate.setText(getCurrentDate());
                    }
                } else {
                    tvStudyDate.setText(getCurrentDate());
                }
            });

            // 학습 시간 가져오기 (초 단위 데이터를 시:분:초로 변환)
            DatabaseReference learningTimeRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("learningTime");

            learningTimeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    long learningTimeInSeconds = task.getResult().getValue(Long.class);
                    String formattedTime = formatTime(learningTimeInSeconds);
                    tvStudyHours.setText(formattedTime);
                    Log.d("Firebase", "Data: " + learningTimeInSeconds);
                } else {
                    tvStudyHours.setText("00시간 00분 00초");
                    Log.d("Firebase", "No data found");
                }
            });

        } else {
            tvUserName.setText("로그인 필요");
            tvStudyContent.setText("로그인 필요님의 학습 시간");
            tvStudyDate.setText(getCurrentDate());
        }

        // "회원정보 수정" 버튼 클릭
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // "뒤로가기" 버튼 클릭
        ImageButton btnArrowback = findViewById(R.id.btnArrowback);
        btnArrowback.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // 로그아웃 버튼 클릭
        Button btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("확인", (dialog, which) -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    redirectToLogin();
                })
                .create()
                .show();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String formatTime(long totalSeconds) {
        int hours = (int) (totalSeconds / 3600);
        int minutes = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60);

        return String.format(Locale.getDefault(), "%02d시간 %02d분 %02d초", hours, minutes, seconds);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
