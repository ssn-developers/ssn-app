package in.edu.ssn.ssnapp.models;

public class Subject {

     String code;
     String name;
     float credits;
     int grade;

    public Subject(String code,String name,int credits){

        this.code = code;
        this.name = name;
        this.credits = credits;

    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public float getCredits() {
        return credits;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }
}
