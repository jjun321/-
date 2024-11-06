package kr.co.example.firebaseregister;

public class CategoryElement {
    private String name;
    private String url;

    // 기본 생성자 (Firebase에서 데이터 파싱 시 필요)
    public CategoryElement() {}

    public CategoryElement(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
