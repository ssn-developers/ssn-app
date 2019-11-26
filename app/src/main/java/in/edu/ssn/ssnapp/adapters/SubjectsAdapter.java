package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.models.Subject;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {
    private ArrayList<Subject> subjects;
    private Context context;
    boolean darkMode=false;

    public SubjectsAdapter(Context context, ArrayList<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
        darkMode = SharedPref.getBoolean(context,"dark_mode");

    }
    @Override
    public SubjectsAdapter.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View subjectItem;
        if(darkMode) {
            subjectItem = layoutInflater.inflate(R.layout.subject_item_dark, parent, false);
        }
        else {
            subjectItem = layoutInflater.inflate(R.layout.subject_item, parent, false);
        }


        return new SubjectsAdapter.SubjectViewHolder(subjectItem,viewType);
    }

    @Override
    public void onBindViewHolder(final SubjectsAdapter.SubjectViewHolder holder, final int position) {

        holder.subjectCodeTV.setText(subjects.get(position).getCode());
        holder.subjectNameTV.setText(subjects.get(position).getName());

        holder.leftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (holder.gradeTV.getText().toString())
                {
                    case "O" : holder.gradeTV.setText("A+");
                                subjects.get(position).setGrade(9);
                                return;
                    case "A+" : holder.gradeTV.setText("A");
                               subjects.get(position).setGrade(8);
                                return;
                    case "A" : holder.gradeTV.setText("B+");
                                subjects.get(position).setGrade(7);
                                return;
                    case "B+" : holder.gradeTV.setText("B");
                                subjects.get(position).setGrade(6);
                                return;
                    case "B" : holder.gradeTV.setText("RA");
                                subjects.get(position).setGrade(0);
                                return;

                }
            }
        });

        holder.rightIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (holder.gradeTV.getText().toString())
                {
                    case "RA" : holder.gradeTV.setText("B");
                        subjects.get(position).setGrade(6);
                        return;
                    case "A+" : holder.gradeTV.setText("O");
                        subjects.get(position).setGrade(10);
                        return;
                    case "A" : holder.gradeTV.setText("A+");
                        subjects.get(position).setGrade(9);
                        return;
                    case "B+" : holder.gradeTV.setText("A");
                        subjects.get(position).setGrade(8);
                        return;
                    case "B" : holder.gradeTV.setText("B+");
                        subjects.get(position).setGrade(7);
                        return;

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        public TextView subjectCodeTV,subjectNameTV,gradeTV;
        ImageView leftIV,rightIV;

        public SubjectViewHolder(View itemView, int viewType) {
            super(itemView);

            subjectCodeTV = itemView.findViewById(R.id.subject_code);
            subjectNameTV = itemView.findViewById(R.id.subject_name);
            gradeTV = itemView.findViewById(R.id.grade_choose_TV);
            leftIV = itemView.findViewById(R.id.left_button);
            rightIV = itemView.findViewById(R.id.right_button);
        }
    }
}
