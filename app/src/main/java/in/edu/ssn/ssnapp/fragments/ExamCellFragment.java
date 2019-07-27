package in.edu.ssn.ssnapp.fragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.CommonUtils;

public class ExamCellFragment extends Fragment implements View.OnClickListener {

    CardView cv_internal, cv_result, cv_seat, cv_exam;

    public ExamCellFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_cell, container, false);
        CommonUtils.initFonts(getContext(),view);
        initUI(view);

        return view;
    }

    void initUI(View view){
        cv_internal = view.findViewById(R.id.cv_internal);  cv_internal.setOnClickListener(this);
        cv_result = view.findViewById(R.id.cv_result);      cv_result.setOnClickListener(this);
        cv_seat = view.findViewById(R.id.cv_seat);          cv_seat.setOnClickListener(this);
        cv_exam = view.findViewById(R.id.cv_exam);          cv_exam.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String text = v.getTag().toString();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_alert_dialog2);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
        TextView tv_message = dialog.findViewById(R.id.tv_message);

        tv_message.setText(text + " will be updated soon and will be notified you shortly.");
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}