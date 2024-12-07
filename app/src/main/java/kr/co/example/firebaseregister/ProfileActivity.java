package kr.co.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageView tierImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // XML에 정의된 TextView 및 ImageView 연결
        tvUserName = findViewById(R.id.UserName);
        tvStudyContent = findViewById(R.id.tvStudyContent);
        tvStudyDate = findViewById(R.id.tvStudyDate);
        tvStudyHours = findViewById(R.id.tvStudyHours);
        tierImage = findViewById(R.id.tierImage); // 티어 이미지

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 사용자 정보 가져오기
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(uid);

            userRef.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String userName = task.getResult().getValue(String.class);
                    tvUserName.setText(userName != null ? userName : "Guest");
                    tvStudyContent.setText((userName != null ? userName : "Guest") + "님의 총 누적 학습 시간: ");
                } else {
                    tvUserName.setText("Guest");
                    tvStudyContent.setText("Guest님의 학습 시간");
                }
            });

            userRef.child("study_date").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String studyDate = task.getResult().getValue(String.class);
                    tvStudyDate.setText(studyDate != null ? studyDate : getCurrentDate());
                } else {
                    tvStudyDate.setText(getCurrentDate());
                }
            });

            DatabaseReference learningTimeRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("learningTime");

            learningTimeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    long learningTimeInSeconds = task.getResult().getValue(Long.class);
                    String formattedTime = formatTime(learningTimeInSeconds);
                    tvStudyHours.setText(formattedTime);

                    // 티어 업데이트
                    updateTier(learningTimeInSeconds);
                } else {
                    tvStudyHours.setText("00시간 00분 00초");
                    tierImage.setImageResource(R.drawable.ic_bronze); // 기본값으로 브론즈 설정
                }
            });

        } else {
            tvUserName.setText("로그인 필요");
            tvStudyContent.setText("로그인 필요님의 학습 시간");
            tvStudyDate.setText(getCurrentDate());
            tierImage.setImageResource(R.drawable.ic_bronze); // 기본값으로 브론즈 설정
        }

        //프로필 수정 넘어가는 부분삭제
//        Button btnEditProfile = findViewById(R.id.btnEditProfile);
//        btnEditProfile.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//        });

        ImageButton btnArrowback = findViewById(R.id.btnArrowback);
        btnArrowback.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Button btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void updateTier(long totalSeconds) {

//        //시간 단위
//        long totalHours = totalSeconds / 3600;
//
//        if (totalHours < 3) {
//            tierImage.setImageResource(R.drawable.ic_bronze);
//        } else if (totalHours < 6) {
//            tierImage.setImageResource(R.drawable.ic_silver);
//        } else if (totalHours < 9) {
//            tierImage.setImageResource(R.drawable.ic_gold);
//        } else {
//            tierImage.setImageResource(R.drawable.ic_king);

        // 초 단위 기준으로 티어 조건 변경 (시연 영상및 발표때 잠깐 사용할 용도)
            if (totalSeconds < 5) {
                tierImage.setImageResource(R.drawable.ic_bronze);
            } else if (totalSeconds < 10) {
                tierImage.setImageResource(R.drawable.ic_silver);
            } else if (totalSeconds < 15) {
                tierImage.setImageResource(R.drawable.ic_gold);
            } else {
                tierImage.setImageResource(R.drawable.ic_king);
            }

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
