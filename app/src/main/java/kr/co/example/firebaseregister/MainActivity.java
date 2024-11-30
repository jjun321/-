package kr.co.example.firebaseregister;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//////
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    public Fragment fragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DrawerLayout 초기화
        drawerLayout = findViewById(R.id.drawer_layout);

        // Toolbar설정 //Toolbar오류
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/////////////////////////////////////////////
        // "시간표 추가" 버튼 클릭 리스너 추가
        Button addTimetableButton = findViewById(R.id.btn_add_timetable);
        if (addTimetableButton != null) {
            addTimetableButton.setOnClickListener(v -> showAddSubjectDialog());
        } else {
            Log.e("MainActivity", "Add Timetable button not found!");
        }


        // 기본 액션바의 타이틀 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // ActionBarDrawerToggle 설정
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // 기본 네비게이션 아이콘 비활성화
        toggle.setDrawerIndicatorEnabled(false);

        // 네비게이션 아이콘 설정(클릭 시 드로어 열기/닫기 설정)
        ImageView navIcon = findViewById(R.id.navigation_icon);
        navIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Toolbar의 제목을 중앙으로 정렬 및 아이콘 크기만큼 보정
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
                        titleTextView.setPadding(0, 0, toggle.getDrawerArrowDrawable().getIntrinsicWidth(), 0);
                        break;
                    }
                }
            }
        });

        // 프로필 아이콘 클릭 시 사용자 정보 수정 페이지로 이동
        ImageView profileIcon = findViewById(R.id.profile_icon);
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> openProfileFragment());
        } else {
            Log.e("MainActivity", "Profile icon not found!");
        }

//////////
        // 네비게이션 뷰 설정
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);



        // 카테고리 버튼 설정
        Button collegeEntranceExamButton = findViewById(R.id.college_entrance_exam);
        collegeEntranceExamButton.setOnClickListener(v -> openCategoryActivity("college_entrance_exam"));

        Button foreignLanguageButton = findViewById(R.id.high_school_student);
        foreignLanguageButton.setOnClickListener(v -> openCategoryActivity("foreign_language"));

        Button programmingButton = findViewById(R.id.programming);
        programmingButton.setOnClickListener(v -> openCategoryActivity("programming"));

        Button essentialLicenseButton = findViewById(R.id.license);
        essentialLicenseButton.setOnClickListener(v -> openCategoryActivity("essential_license"));

        Button transferButton = findViewById(R.id.transfer);
        transferButton.setOnClickListener(v -> openCategoryActivity("transfer"));

        Button publicOfficialButton = findViewById(R.id.public_official);
        publicOfficialButton.setOnClickListener(v -> openCategoryActivity("public_official"));






    }

    private void showAddSubjectDialog() {
        new AlertDialog.Builder(this)
                .setTitle("시간표 추가")
                .setMessage("새로운 과목을 추가하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    // 확인 버튼 동작
                    Log.d("MainActivity", "과목 추가 확인 클릭");
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    // 취소 버튼 동작
                    Log.d("MainActivity", "과목 추가 취소 클릭");
                    dialog.dismiss();
                })
                .create()
                .show();
    }




    //////////
    // 네비게이션 메뉴 선택 동작
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_planer) { // 학습 계획표 선택 시 PlannerActivity로 이동
            Intent intent = new Intent(MainActivity.this, PlannerActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

            // 다른 메뉴 항목 처리
        } else if (itemId == R.id.profile) {
            openProfileFragment();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }



    // ProfileFragment 열기
    private void openProfileFragment() {
        ProfileFragment profileFragment  = new ProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openCategoryActivity(String categoryType) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.putExtra("CATEGORY_TYPE", categoryType);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // 드로어가 열려 있을 때 뒤로 가기 버튼을 누르면 드로어를 닫음
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
