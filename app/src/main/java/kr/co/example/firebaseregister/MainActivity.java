package kr.co.example.firebaseregister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NavigationView 초기화
        NavigationView navigationView = findViewById(R.id.navigation_view);

// 헤더 뷰 가져오기
        View headerView = navigationView.getHeaderView(0); // 첫 번째 헤더 뷰 가져오기
        TextView headerTitle = headerView.findViewById(R.id.nav_header_title); // 헤더 텍스트뷰 가져오기

// Firebase Authentication에서 사용자 정보 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Firebase Realtime Database에서 사용자 이름 가져오기
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount") // RegisterActivity에서 저장된 경로
                    .child(currentUser.getUid()); // 현재 사용자의 UID

            userRef.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String userName = task.getResult().getValue(String.class); // 사용자 이름 가져오기
                    if (userName != null) {
                        // 헤더 텍스트 업데이트
                        headerTitle.setText(userName + "님" + " 환영합니다! ");

                        // MenuItem(profile) 업데이트
                        Menu menu = navigationView.getMenu();
                        MenuItem profileItem = menu.findItem(R.id.profile);
                        if (profileItem != null) {
                            profileItem.setTitle(userName); // 사용자 이름만 표시
                        }
                    } else {
                        // 이름이 없을 경우 기본값 설정
                        headerTitle.setText( "Guest님 환영합니다!");
                        Menu menu = navigationView.getMenu();
                        MenuItem profileItem = menu.findItem(R.id.profile);
                        if (profileItem != null) {
                            profileItem.setTitle("Guest");
                        }
                    }
                } else {
                    // 데이터 가져오기 실패 시 기본값 설정
                    headerTitle.setText("환영합니다, Guest님!");
                    Menu menu = navigationView.getMenu();
                    MenuItem profileItem = menu.findItem(R.id.profile);
                    if (profileItem != null) {
                        profileItem.setTitle("Guest");
                    }
                }
            });
        } else {
            // 로그인하지 않은 경우 기본값 설정
            headerTitle.setText("환영합니다, Guest님!");
            Menu menu = navigationView.getMenu();
            MenuItem profileItem = menu.findItem(R.id.profile);
            if (profileItem != null) {
                profileItem.setTitle("로그인 필요");
            }
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

        // BitmapDrawable을 사용하여 PNG 이미지 크기 조정
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_custom_menu);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 110, false); // 원하는 크기로 조정
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
                            int adjustedPadding = iconWidth - 150; // 왼쪽으로 20px 이동
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

    // 사용자 정보 수정 페이지로 이동
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // 메뉴 파일 적용
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            //profileActivity로 이동
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
