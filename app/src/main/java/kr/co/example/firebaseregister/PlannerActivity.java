package kr.co.example.firebaseregister;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PlannerActivity extends AppCompatActivity {

//


    private DatabaseReference databaseReference; // Firebase Realtime Database 참조
    private EditText dateInput;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        // Firebase Database 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("Timetable");

        dateInput = findViewById(R.id.date_input);
        ImageButton datePickerButton = findViewById(R.id.date_picker_button);

        calendar = Calendar.getInstance();

        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        Button editButton = findViewById(R.id.btn_edit_timetable);
        Button addButton = findViewById(R.id.btn_add_timetable);

        editButton.setOnClickListener(v ->
                Toast.makeText(this, "시간표 수정 클릭됨", Toast.LENGTH_SHORT).show());

        addButton.setOnClickListener(v -> showAddSubjectDialog());

        // 초기 시간표 로드
        updateDateInput();
    }


    // DatePickerDialog를 표시하는 메서드
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateInput();
        };

        new DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // 선택된 날짜를 EditText에 표시하는 메서드
    private void updateDateInput() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        dateInput.setText(sdf.format(calendar.getTime()));
        updateTimetableForWeek();
    }

    private void updateTimetableForWeek() {
        TableLayout timetable = findViewById(R.id.timetable);

        // 선택된 날짜를 기준으로 날짜 문자열 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(calendar.getTime());

        // Firebase에서 데이터 가져오기
        databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 기존 시간표 초기화
                for (int i = 1; i < timetable.getChildCount(); i++) {
                    TableRow row = (TableRow) timetable.getChildAt(i);
                    if (row != null) { // Null 체크
                        for (int j = 1; j <= 7; j++) { // 요일 열
                            TextView cell = (TextView) row.getChildAt(j);
                            if (cell != null) {
                                cell.setText("");
                                cell.setBackgroundColor(ContextCompat.getColor(PlannerActivity.this, R.color.cell_background));
                            }
                        }
                    }
                }

                // Firebase 데이터로 시간표 업데이트
                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                    int dayOfWeek = Integer.parseInt(daySnapshot.getKey()); // 요일 (1~7)
                    for (DataSnapshot timeSnapshot : daySnapshot.getChildren()) {
                        String timeKey = timeSnapshot.getKey(); // 시간
                        String subject = timeSnapshot.getValue(String.class); // 과목명

                        try {
                            int hour = Integer.parseInt(timeKey.replace("시", ""));
                            if (hour >= 6 && hour <= 24) { // 시간 범위 확인
                                TableRow row = (TableRow) timetable.getChildAt(hour - 6 + 1); // 헤더 제외
                                if (row != null) { // Null 체크
                                    TextView cell = (TextView) row.getChildAt(dayOfWeek); // 해당 요일 열
                                    if (cell != null) {
                                        cell.setText(subject);
                                        cell.setBackgroundColor(ContextCompat.getColor(PlannerActivity.this, R.color.highlight_color));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PlannerActivity.this, "시간표 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showAddSubjectDialog() {
        // 다이얼로그 레이아웃 inflate
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_subject_dialog, null);

        // 다이얼로그 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("시간표 추가")
                .setPositiveButton("추가", (dialog, which) -> {
                    // 사용자 입력값 처리
                    EditText subjectInput = dialogView.findViewById(R.id.subject_input);
                    TimePicker startTimePicker = dialogView.findViewById(R.id.start_time_picker);
                    TimePicker endTimePicker = dialogView.findViewById(R.id.end_time_picker);

                    String subject = subjectInput.getText().toString();
                    int startHour = startTimePicker.getHour();
                    int endHour = endTimePicker.getHour();

                    if (subject.isEmpty()) {
                        Toast.makeText(this, "과목명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (startHour >= endHour) {
                        Toast.makeText(this, "시작 시간은 종료 시간보다 빨라야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 시간표에 데이터 추가 로직
                    updateTimetable(subject, startHour, endHour);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateTimetable(String subject, int startHour, int endHour) {
        TableLayout timetable = findViewById(R.id.timetable);

        // 현재 선택된 날짜
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(calendar.getTime());

        // 시간표 데이터 저장 및 UI 업데이트
        for (int i = startHour; i < endHour; i++) {
            int selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1: 일요일, ..., 7: 토요일
            String timeKey = i + "시"; // 시간대 키
            String dayKey = String.valueOf(selectedDayOfWeek); // 요일 키 (1~7)

            // Firebase에 저장
            databaseReference.child(selectedDate).child(dayKey).child(timeKey).setValue(subject);

            // UI 업데이트
            TableRow row = (TableRow) timetable.getChildAt(i - 6 + 1); // 헤더 제외
            if (row != null) {
                TextView cell = (TextView) row.getChildAt(selectedDayOfWeek); // 요일 열
                if (cell != null) {
                    cell.setText(subject);
                    cell.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight_color));
                }
            }
        }
    }
}
//
//
