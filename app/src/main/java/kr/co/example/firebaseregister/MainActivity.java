package kr.co.example.firebaseregister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CircularProgressIndicator circularProgressBar; // CircularProgressIndicator로 변경
    private TextView progressText;
    private DatabaseReference mDatabase;

    // 목표 학습 시간 (초 단위, 10시간 = 36000초)
    private final long totalLearningGoal = 36000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CircularProgressIndicator와 TextView 연결
        circularProgressBar = findViewById(R.id.circular_progress_bar);
        progressText = findViewById(R.id.progress_text);

        // Firebase Auth 초기화
        mAuth = FirebaseAuth.getInstance();

        // FirebaseUser로 로그인 상태 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그아웃 상태라면 로그인 화면으로 이동
            redirectToLogin();
        } else {
            // 사용자 UID를 이용해 이름 가져오기
            String userId = currentUser.getUid();

            // Firebase Database 경로 설정
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("learningTime");

            // Firebase에서 학습 시간 가져오기
            loadLearningTime();
        }

        // UI 설정
        setupUI();
    }

    private void loadLearningTime() {
        mDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Long totalTime = task.getResult().getValue(Long.class);
                if (totalTime == null) totalTime = 0L;

                // CircularProgressIndicator 업데이트
                updateProgressBar(totalTime);
            } else {
                Log.e("MainActivity", "Firebase 데이터 로드 실패", task.getException());
                Toast.makeText(this, "데이터 로드 실패!", Toast.LENGTH_SHORT).show();
                updateProgressBar(0);
            }
        });
    }

    private void updateProgressBar(long totalTimeInSeconds) {
        // 목표 대비 진행률 계산
        int progress = (int) ((totalTimeInSeconds * 100) / totalLearningGoal);
        if (progress > 100) progress = 100; // 최대값 제한

        // CircularProgressIndicator와 TextView 업데이트
        circularProgressBar.setProgress(progress);
        progressText.setText("진행률: " + progress + "%");
    }

    private void setupUI() {
        // NavigationView 초기화
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0); // 첫 번째 헤더 뷰 가져오기
        TextView headerTitle = headerView.findViewById(R.id.nav_header_title); // 헤더 텍스트뷰 가져오기

        // Firebase Authentication에서 사용자 정보 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount") // RegisterActivity에서 저장된 경로
                    .child(currentUser.getUid());

            userRef.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String userName = task.getResult().getValue(String.class);
                    if (userName != null) {
                        headerTitle.setText(userName + "님 환영합니다!");
                        updateProfileMenu(navigationView, userName);
                    } else {
                        headerTitle.setText("Guest님 환영합니다!");
                        updateProfileMenu(navigationView, "Guest");
                    }
                } else {
                    headerTitle.setText("환영합니다, Guest님!");
                    updateProfileMenu(navigationView, "Guest");
                }
            });
        } else {
            headerTitle.setText("환영합니다, Guest님!");
            updateProfileMenu(navigationView, "로그인 필요");
        }

        // DrawerLayout 초기화
        drawerLayout = findViewById(R.id.drawer_layout);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ActionBarDrawerToggle 설정
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // 기본 네비게이션 아이콘 비활성화
        toggle.setDrawerIndicatorEnabled(false);

        // 네비게이션 아이콘 커스텀
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_custom_menu);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 110, false);
        Drawable customIcon = new BitmapDrawable(getResources(), scaledBitmap);
        toolbar.setNavigationIcon(customIcon);

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            } else {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Toolbar의 제목을 중앙으로 정렬
        toolbar.post(() -> {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                View view = toolbar.getChildAt(i);
                if (view instanceof TextView) {
                    TextView titleTextView = (TextView) view;
                    if (titleTextView.getText().equals(toolbar.getTitle())) {
                        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleTextView.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        titleTextView.setLayoutParams(layoutParams);
                        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        Drawable navIcon = toolbar.getNavigationIcon();
                        if (navIcon != null) {
                            int iconWidth = navIcon.getIntrinsicWidth() + toolbar.getContentInsetStartWithNavigation();
                            int adjustedPadding = iconWidth - 150;
                            titleTextView.setPadding(adjustedPadding, 0, 0, 0);
                        }
                        break;
                    }
                }
            }
        });

        // 카테고리 버튼 설정
        ImageButton collegeEntranceExamButton = findViewById(R.id.college_entrance_exam);
        collegeEntranceExamButton.setOnClickListener(v -> openCategoryActivity("college_entrance_exam"));

        ImageButton foreignLanguageButton = findViewById(R.id.high_school_student);
        foreignLanguageButton.setOnClickListener(v -> openCategoryActivity("foreign_language"));

        ImageButton programmingButton = findViewById(R.id.programming);
        programmingButton.setOnClickListener(v -> openCategoryActivity("programming"));

        ImageButton essentialLicenseButton = findViewById(R.id.license);
        essentialLicenseButton.setOnClickListener(v -> openCategoryActivity("essential_license"));

        ImageButton transferButton = findViewById(R.id.transfer);
        transferButton.setOnClickListener(v -> openCategoryActivity("transfer"));

        ImageButton publicOfficialButton = findViewById(R.id.public_official);
        publicOfficialButton.setOnClickListener(v -> openCategoryActivity("public_official"));
    }

    private void updateProfileMenu(NavigationView navigationView, String title) {
        Menu menu = navigationView.getMenu();
        MenuItem profileItem = menu.findItem(R.id.profile);
        if (profileItem != null) {
            profileItem.setTitle(title);
        }
    }

    private void showWelcomeMessage(String userName) {
        Toast.makeText(this, userName + "님 환영합니다!", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openCategoryActivity(String categoryType) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.putExtra("CATEGORY_TYPE", categoryType);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_planner) {
            Intent intent = new Intent(MainActivity.this, PlannerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_timer) {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
