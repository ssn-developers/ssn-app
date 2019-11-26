package in.edu.ssn.ssnapp.models;

public class Subject {

    private String code;
    private String name;
    private int credits;
    private int grade;

    public Subject(String code,String name,int credits){

        this.code = code;
        this.name = name;
        this.credits = credits;
        this.grade = 6;

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

    public int getCredits() {
        return credits;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }
}
