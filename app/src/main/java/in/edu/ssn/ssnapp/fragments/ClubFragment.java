package in.edu.ssn.ssnapp.fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.CommonUtils;

public class ClubFragment extends Fragment {

    public ClubFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);
        return view;

    }

}