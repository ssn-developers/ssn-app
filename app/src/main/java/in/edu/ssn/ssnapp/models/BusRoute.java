package in.edu.ssn.ssnapp.models;

import java.io.Serializable;
import java.util.List;

public class BusRoute implements Serializable {

    boolean avail;
    String routeName;
    String dname;
    String dphone;
    //TODO add lat lng
    String via;
    List<stop> stop;

    public static class stop implements Serializable{
        String place;
        String time;
        //TODO add lat lng

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public boolean isAvail() {
        return avail;
    }

    public void setAvail(boolean avail) {
        this.avail = avail;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDphone() {
        return dphone;
    }

    public void setDphone(String dphone) {
        this.dphone = dphone;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public List<BusRoute.stop> getStop() {
        return stop;
    }

    public void setStop(List<BusRoute.stop> stop) {
        this.stop = stop;
    }
}
