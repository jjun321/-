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

        // 수능 버튼 클릭 이벤트 설정
        Button collegeEntranceExamButton = findViewById(R.id.college_entrance_exam);
        collegeEntranceExamButton.setOnClickListener(v -> openCategoryActivity());
    }

    private void openCategoryActivity() {
        // CategoryActivity로 이동
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        startActivity(intent);
    }
}
