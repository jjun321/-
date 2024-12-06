package kr.co.example.firebaseregister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

    private CircularProgressIndicator circularProgressBar;
    private TextView progressText;
    private DatabaseReference mDatabase;

    // 목표 학습 시간 (초 단위, 10시간 = 36000초)
    private final long totalLearningGoal = 3600;

    private long totalTimeInSeconds = 0; // 누적 학습 시간
    private boolean isRunning = false; // 실시간 업데이트 상태 플래그
    private Handler handler = new Handler();

    private Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                totalTimeInSeconds++;
                updateProgressBar(totalTimeInSeconds);
                handler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularProgressBar = findViewById(R.id.circular_progress_bar);
        progressText = findViewById(R.id.progress_text);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        } else {
            String userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("learningTime");

            loadLearningTime(); // Firebase에서 학습 시간 가져오기
        }

        setupUI();
    }

    private void loadLearningTime() {
        mDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Long loadedTime = task.getResult().getValue(Long.class);
                if (loadedTime != null) {
                    totalTimeInSeconds = loadedTime;
                }
                updateProgressBar(totalTimeInSeconds);

                // 실시간 업데이트 시작
                startRealtimeProgressUpdater();
            } else {
                Log.e("MainActivity", "Firebase 데이터 로드 실패", task.getException());
                Toast.makeText(this, "데이터 로드 실패!", Toast.LENGTH_SHORT).show();
                updateProgressBar(0);
            }
        });
    }

    private void updateProgressBar(long totalTimeInSeconds) {
        int progress = (int) ((totalTimeInSeconds * 100) / totalLearningGoal);
        if (progress > 100) progress = 100;

        circularProgressBar.setProgress(progress);
        progressText.setText("진행률: " + progress + "%");
    }

    private void startRealtimeProgressUpdater() {
        isRunning = true;
        handler.post(progressUpdater);
    }

    private void stopRealtimeProgressUpdater() {
        isRunning = false;
        handler.removeCallbacks(progressUpdater);
    }

    private void setupUI() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView headerTitle = headerView.findViewById(R.id.nav_header_title);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
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

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        toggle.setDrawerIndicatorEnabled(false);

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
    protected void onPause() {
        super.onPause();
        stopRealtimeProgressUpdater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRealtimeProgressUpdater();
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
