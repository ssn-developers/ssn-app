package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Subject;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {
    boolean darkMode = false;
    private ArrayList<Subject> subjects;

    public SubjectsAdapter(Context context, ArrayList<Subject> subjects) {
        this.subjects = subjects;
        darkMode = SharedPref.getBoolean(context, "dark_mode");

    }

    @Override
    public SubjectsAdapter.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View subjectItem;
        if (darkMode) {
            subjectItem = layoutInflater.inflate(R.layout.subject_item_dark, parent, false);
        } else {
            subjectItem = layoutInflater.inflate(R.layout.subject_item, parent, false);
        }

        return new SubjectsAdapter.SubjectViewHolder(subjectItem, viewType);
    }

    @Override
    public void onBindViewHolder(final SubjectsAdapter.SubjectViewHolder holder, final int position) {
        holder.codeTV.setText(subjects.get(position).getCode());
        holder.nameTV.setText(subjects.get(position).getName());

        holder.leftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (holder.gradeTV.getText().toString()) {
                    case "O":
                        holder.gradeTV.setText("A+");
                        subjects.get(position).setGrade(9);
                        return;
                    case "A+":
                        holder.gradeTV.setText("A");
                        subjects.get(position).setGrade(8);
                        return;
                    case "A":
                        holder.gradeTV.setText("B+");
                        subjects.get(position).setGrade(7);
                        return;
                    case "B+":
                        holder.gradeTV.setText("B");
                        subjects.get(position).setGrade(6);
                        return;
                    case "B":
                        holder.gradeTV.setText("RA");
                        subjects.get(position).setGrade(0);
                        return;
                }
            }
        });

        holder.rightIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (holder.gradeTV.getText().toString()) {
                    case "A+":
                        holder.gradeTV.setText("O");
                        subjects.get(position).setGrade(10);
                        return;
                    case "A":
                        holder.gradeTV.setText("A+");
                        subjects.get(position).setGrade(9);
                        return;
                    case "B+":
                        holder.gradeTV.setText("A");
                        subjects.get(position).setGrade(8);
                        return;
                    case "B":
                        holder.gradeTV.setText("B+");
                        subjects.get(position).setGrade(7);
                        return;
                    case "RA":
                        holder.gradeTV.setText("B");
                        subjects.get(position).setGrade(6);
                        return;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        public TextView codeTV, nameTV, gradeTV;
        ImageView leftIV, rightIV;

        public SubjectViewHolder(View itemView, int viewType) {
            super(itemView);

            codeTV = itemView.findViewById(R.id.codeTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            gradeTV = itemView.findViewById(R.id.gradeTV);
            leftIV = itemView.findViewById(R.id.leftIV);
            rightIV = itemView.findViewById(R.id.rightIV);

            nameTV.setSelected(true);
        }
    }
}
