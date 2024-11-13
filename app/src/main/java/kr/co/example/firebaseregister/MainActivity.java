package kr.co.example.firebaseregister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // BitmapDrawable을 사용하여 PNG 이미지 크기 조정
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_custom_menu);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false); // 원하는 크기로 조정
        Drawable customIcon = new BitmapDrawable(getResources(), scaledBitmap);
        toolbar.setNavigationIcon(customIcon);

        // 네비게이션 아이콘 클릭 시 드로어 열기/닫기 설정
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            } else {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        // 드로어 리스너 추가
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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

                        // 아이콘의 너비만큼 왼쪽 패딩 추가
                        Drawable navIcon = toolbar.getNavigationIcon();
                        if (navIcon != null) {
                            int iconWidth = navIcon.getIntrinsicWidth() + toolbar.getContentInsetStartWithNavigation();
                            int adjustedPadding = iconWidth - 250; // 왼쪽으로 20px 이동
                            titleTextView.setPadding(adjustedPadding, 0, 0, 0);
                        }
                        break;
                    }
                }
            }
        });

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

    private void openCategoryActivity(String categoryType) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.putExtra("CATEGORY_TYPE", categoryType);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // 드로어가 열려 있을 때 뒤로 가기 버튼을 누르면 드로어를 닫음
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }
}
