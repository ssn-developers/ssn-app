package in.edu.ssn.ssnapp.models;

import com.sahurjt.objectcsv.annotations.CsvModel;
import com.sahurjt.objectcsv.annotations.CsvParameter;

@CsvModel(headerPresent = true)
public class Faculty {
    @CsvParameter(value = "access")
    public String access;

    @CsvParameter(value = "name")
    public String name;

    @CsvParameter(value = "email")
    public String email;

    @CsvParameter(value = "position")
    public String position;

    @CsvParameter(value = "dept")
    public String dept;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
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

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}