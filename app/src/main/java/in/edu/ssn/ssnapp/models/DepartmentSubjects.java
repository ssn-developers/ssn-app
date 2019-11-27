package in.edu.ssn.ssnapp.models;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSubjects {

    String dept;
    ArrayList<SemesterSubjects> sem;

    public ArrayList<SemesterSubjects> getSem() {
        return sem;
    }

    public DepartmentSubjects(String dept,ArrayList<SemesterSubjects> sem)
    {
        this.dept = dept;
        this.sem = sem;
    }

    public class SemesterSubjects{

        int no;
        ArrayList<Subject> subjects;


        public ArrayList<Subject> getSubjects() {
            return subjects;
        }
    }
}
