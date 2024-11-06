// CategoryActivity.java
package kr.co.example.firebaseregister;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);  // activity_category.xml 파일을 설정

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecyclerView에 필요한 어댑터 설정
        CategoryAdapter adapter = new CategoryAdapter(getCategoryList(), this);
        recyclerView.setAdapter(adapter);
    }

    private List<CategoryElement> getCategoryList() {
        List<CategoryElement> elements = new ArrayList<>();
        elements.add(new CategoryElement("메가스터디", "https://www.megastudy.net/"));
        elements.add(new CategoryElement("대성 마이맥", "https://www.mimacstudy.com/"));
        elements.add(new CategoryElement("EBS", "https://www.ebsi.co.kr/"));
        elements.add(new CategoryElement("이투스", "https://www.etoos.com/"));
        return elements;
    }
}
