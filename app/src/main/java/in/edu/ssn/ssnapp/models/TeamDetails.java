package in.edu.ssn.ssnapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class TeamDetails implements Serializable {
    String name, position;
    int dp;
    ArrayList<Integer> type;
    ArrayList<String> url;

    public TeamDetails(String name, String position, int dp, ArrayList<Integer> type, ArrayList<String> url) {
        this.name = name;
        this.position = position;
        this.dp = dp;
        this.type = type;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getDp() {
        return dp;
    }

    public void setDp(int dp) {
        this.dp = dp;
    }

    public ArrayList<Integer> getType() {
        return type;
    }

    public void setType(ArrayList<Integer> type) {
        this.type = type;
    }

    public ArrayList<String> getUrl() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }
}
