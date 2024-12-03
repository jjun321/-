package kr.co.example.firebaseregister;

public class CategoryElement {
    private String name;
    private String url;
    private int iconResId;

    // 기본 생성자 (Firebase에서 데이터 파싱 시 필요)
    public CategoryElement() {}

    // 세 개의 매개변수를 받는 생성자 추가
    public CategoryElement(String name, String url, int iconResId) {
        this.name = name;
        this.url = url;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
    public int getIconResId() {
        return iconResId;
    }
}
