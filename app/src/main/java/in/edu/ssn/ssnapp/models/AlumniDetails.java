package in.edu.ssn.ssnapp.models;

public class AlumniDetails {
    String name, email;

    public AlumniDetails() { }

    public AlumniDetails(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}