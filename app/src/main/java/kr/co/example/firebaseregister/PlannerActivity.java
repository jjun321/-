package kr.co.example.firebaseregister;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PlannerActivity extends AppCompatActivity {

    private EditText dateInput;
    private Calendar calendar;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        // 날짜 입력 필드와 달력 버튼 초기화
        dateInput = findViewById(R.id.date_input);
        ImageButton datePickerButton = findViewById(R.id.date_picker_button);

        calendar = Calendar.getInstance();

        // 달력 버튼 클릭 시 DatePickerDialog 표시
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // 시간표 수정 및 추가 버튼 클릭 시 동작
        Button editButton = findViewById(R.id.btn_edit_timetable);
        Button addButton = findViewById(R.id.btn_add_timetable);

        editButton.setOnClickListener(v ->
                Toast.makeText(this, "시간표 수정 클릭됨", Toast.LENGTH_SHORT).show());

        addButton.setOnClickListener(v ->
                Toast.makeText(this, "시간표 추가 클릭됨", Toast.LENGTH_SHORT).show());
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

        for (int i = startHour; i < endHour; i++) {
            // 6시 기준으로 행을 가져옴 (6시가 첫 번째 행)
            TableRow row = (TableRow) timetable.getChildAt(i - 6); // 6시부터 시작
            // 월요일 열을 기준으로 수정 (1은 월요일, 필요 시 변경)
            TextView cell = (TextView) row.getChildAt(1); // 월요일 열
            cell.setText(subject);
            cell.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight_color));
        }
    }
}
