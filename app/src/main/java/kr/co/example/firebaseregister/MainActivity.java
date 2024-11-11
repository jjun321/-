package kr.co.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DrawerLayout 초기화
        drawerLayout = findViewById(R.id.drawer_layout);

        // 왼쪽 드로어 메뉴 버튼
        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

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

}
