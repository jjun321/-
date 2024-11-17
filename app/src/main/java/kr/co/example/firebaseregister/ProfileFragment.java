package kr.co.example.firebaseregister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 사용자 정보 설정 (데모 데이터)
        /*TextView userName = view.findViewById(R.id.user_name);
        TextView userRole = view.findViewById(R.id.user_role);
        TextView studyTime = view.findViewById(R.id.study_time);

        userName.setText("김OO");
        userRole.setText("학생");
        studyTime.setText("10시간");*/

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
