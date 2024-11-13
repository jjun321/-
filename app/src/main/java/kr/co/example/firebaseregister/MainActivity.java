package kr.co.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DrawerLayout 초기화
        drawerLayout = findViewById(R.id.drawer_layout);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ActionBarDrawerToggle을 사용해 툴바와 드로어 연동
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
        intent.putExtra("CATEGORY_TYPE", categoryType); // 카테고리 유형 전달
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
