package in.edu.ssn.ssnapp.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.FontChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamCellFragment extends Fragment {

    public ExamCellFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_cell, container, false);
        CommonUtils.initFonts(getContext(),view);
        initUI(view);
        return view;
    }

    void initUI(View view){

    }
}