package in.edu.ssn.ssnapp.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.FontChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamCellFragment extends Fragment {


    public ExamCellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exam_cell, container, false);
        initFonts(view);
        initUI(view);
        return view;
    }

    void initUI(View view){

    }

    //Fonts
    public Typeface regular, bold, italic, bold_italic;
    private void initFonts(View view){
        regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_italic.ttf");
        bold_italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold_italic.ttf");
        FontChanger fontChanger = new FontChanger(bold);
        fontChanger.replaceFonts((ViewGroup) view);
    }

}
