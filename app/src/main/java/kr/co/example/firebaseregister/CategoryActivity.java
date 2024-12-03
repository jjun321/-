// CategoryActivity.java
package kr.co.example.firebaseregister;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent로 전달받은 카테고리 유형에 따라 다른 리스트 설정
        String categoryType = getIntent().getStringExtra("CATEGORY_TYPE");
        Log.d("CategoryActivity", "Category Type: " + categoryType); // 로그 추가
        List<CategoryElement> categoryList = getCategoryList(categoryType);


        // RecyclerView에 어댑터 설정
        CategoryAdapter adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);
    }

    private List<CategoryElement> getCategoryList(String categoryType) {
        List<CategoryElement> elements = new ArrayList<>();

        // 수능 카테고리
        if ("college_entrance_exam".equals(categoryType)) {
            elements.add(new CategoryElement("메가스터디", "https://www.megastudy.net/", R.drawable.ic_megastudy));
            elements.add(new CategoryElement("대성 마이맥", "https://www.mimacstudy.com/", R.drawable.ic_daesung));
            elements.add(new CategoryElement("EBS", "https://www.ebsi.co.kr/", R.drawable.ic_ebs ));
            elements.add(new CategoryElement("이투스", "https://www.etoos.com/", R.drawable.ic_etoos));
        }
        // 외국어 카테고리
        else if ("foreign_language".equals(categoryType)) {
            elements.add(new CategoryElement("해커스", "https://www.hackers.com/", R.drawable.ic_foreign_language));
            elements.add(new CategoryElement("YBM 어학원", "https://www.ybmedu.com/",R.drawable.ic_foreign_language));
            elements.add(new CategoryElement("파고다", "https://www.pagoda21.com/",R.drawable.ic_foreign_language));
            elements.add(new CategoryElement("월스트리트 잉글리쉬", "https://www.wallstreetenglish.co.kr/",R.drawable.ic_foreign_language));
        }
        // 프로그래밍 카테고리
        else if ("programming".equals(categoryType)) {
            elements.add(new CategoryElement("인프런", "https://www.inflearn.com/", R.drawable.ic_programming));
            elements.add(new CategoryElement("생활코딩", "https://opentutorials.org/", R.drawable.ic_programming));
            elements.add(new CategoryElement("패스트캠퍼스", "https://www.fastcampus.co.kr/", R.drawable.ic_programming));
            elements.add(new CategoryElement("코드스테이츠", "https://www.codestates.com/", R.drawable.ic_programming));
        }
        // 필수 자격증 카테고리
        else if ("essential_license".equals(categoryType)) {
            elements.add(new CategoryElement("한능검", "https://www.historyexam.go.kr/", R.drawable.ic_license));
            elements.add(new CategoryElement("컴활", "https://www.q-net.or.kr/", R.drawable.ic_license));
            elements.add(new CategoryElement("MOS", "https://www.microsoft.com/en-us/learning/mos-certification.aspx", R.drawable.ic_license));
        }
        // 편입 카테고리
        else if ("transfer".equals(categoryType)) {
            elements.add(new CategoryElement("해커스 편입", "https://www.hackersut.com/?_C_=373827&utm_source=naver_ad_pc&utm_medium=brandsearch&utm_campaign=ut_ac_main&utm_content=hometext_a&utm_term=%ED%95%B4%EC%BB%A4%EC%8A%A4%ED%8E%B8%EC%9E%85&NaPm=ct%3Dm3cy48n4%7Cci%3D0zm0002SUN5ByRcNkKXk%7Ctr%3Dbrnd%7Chk%3D260da04781b2e5645437daebf3b07cef190532fa%7Cnacn%3Dr42dBMwHoZq4", R.drawable.ic_transfer));
            elements.add(new CategoryElement("김영 편입", "https://www.kimyoung.co.kr/offmegaky.asp", R.drawable.ic_transfer));
            elements.add(new CategoryElement("에듀윌 편입", "https://event.eduwill.net/Event/2024/_116/AllAcademyFreeTrial?etm_edwcd=_116&etm_mktcd=0000000100500100100100020002000275&utm_source=naver_ad_pc&utm_medium=univ_bs&utm_campaign=_1160000000100500100100100020002000275&utm_content=nad-a001-04-000000330140485&n_media=27758&n_query=%EC%97%90%EB%93%80%EC%9C%8C%ED%8E%B8%EC%9E%85&n_rank=1&n_ad_group=grp-a001-04-000000041613234&n_ad=nad-a001-04-000000330140485&n_keyword_id=nkw-a001-04-000006154504338&n_keyword=%EC%97%90%EB%93%80%EC%9C%8C%ED%8E%B8%EC%9E%85&n_campaign_type=4&n_contract=tct-a001-04-000000000962674&n_ad_group_type=5&NaPm=ct%3Dm3cy5bzs%7Cci%3D0zW0003FUN5B2JwZE13Y%7Ctr%3Dbrnd%7Chk%3D694f091578fc0fde36037bb4ffb61c40808fc8cf%7Cnacn%3Dr42dBMwHoZq4", R.drawable.ic_transfer));
        }
        // 공무원 카테고리
        else if ("public_official".equals(categoryType)) {
            elements.add(new CategoryElement("공무원 저널", "https://www.psnews.co.kr/", R.drawable.ic_public));
            elements.add(new CategoryElement("공무원 합격의 길", "https://www.gonggantech.com/", R.drawable.ic_public));
            elements.add(new CategoryElement("에듀윌 공무원", "https://www.eduwill.net/", R.drawable.ic_public));
        }

        return elements;
    }

}
