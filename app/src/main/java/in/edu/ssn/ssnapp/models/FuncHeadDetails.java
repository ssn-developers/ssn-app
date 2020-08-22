package in.edu.ssn.ssnapp.models;

public class FuncHeadDetails {
    String name, email, position, extn;

    public FuncHeadDetails() {
    }

    public FuncHeadDetails(String name, String email, String position, String extn) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.extn = extn;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getExtn() {
        return extn;
    }

    public void setExtn(String extn) {
        this.extn = extn;
    }
}