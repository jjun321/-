package kr.co.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvStudyContent, tvStudyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // XML 파일 이름

        // XML에 정의된 TextView를 가져옴
        tvUserName = findViewById(R.id.UserName);
        tvStudyContent = findViewById(R.id.tvStudyContent); // 학습 시간 TextView 가져오기
        tvStudyDate = findViewById(R.id.tvStudyDate); // 학습 날짜 TextView 가져오기

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

                    // TextView에 사용자 이름 설정
                    if (userName != null) {
                        tvUserName.setText(userName); // 사용자 이름 설정
                        tvStudyContent.setText(userName + "님의 학습 시간"); // 학습 시간 텍스트 설정
                    }
                } else {
                    tvUserName.setText("Guest"); // 기본값 설정
                    tvStudyContent.setText("Guest님의 학습 시간"); // 기본값 설정
                }
            });

            // 학습 날짜 가져오기
            userRef.child("study_date").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String studyDate = task.getResult().getValue(String.class);

                    // Firebase에서 가져온 날짜 설정
                    if (studyDate != null) {
                        tvStudyDate.setText(studyDate); // Firebase 날짜 설정
                    } else {
                        tvStudyDate.setText(getCurrentDate()); // 현재 날짜로 설정
                    }
                } else {
                    tvStudyDate.setText(getCurrentDate()); // 기본값: 현재 날짜
                }
            });

        } else {
            tvUserName.setText("로그인 필요"); // 로그아웃 상태일 경우
            tvStudyContent.setText("로그인 필요님의 학습 시간"); // 로그아웃 상태일 경우
            tvStudyDate.setText(getCurrentDate()); // 로그아웃 상태일 경우 현재 날짜 표시
        }


        // "회원정보 수정" 버튼 클릭 시 EditProfileActivity로 이동
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditProfileActivity로 이동
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });



    }

    // 현재 날짜를 가져오는 메서드
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }




}