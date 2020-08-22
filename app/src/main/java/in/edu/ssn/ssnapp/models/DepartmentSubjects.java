package in.edu.ssn.ssnapp.models;

import java.util.ArrayList;

public class DepartmentSubjects {

    String dept;
    ArrayList<SemesterSubjects> sem;

    public DepartmentSubjects(String dept, ArrayList<SemesterSubjects> sem) {
        this.dept = dept;
        this.sem = sem;
    }

    public ArrayList<SemesterSubjects> getSem() {
        return sem;
    }

    public class SemesterSubjects {
        ArrayList<Subject> subjects;


        public ArrayList<Subject> getSubjects() {
            return subjects;
        }
    }
}
