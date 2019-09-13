package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusRoute implements Parcelable {
    String routeName;
    List<String> stop;
    List<String> time;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.routeName);
        dest.writeList(this.stop);
        dest.writeList(this.time);
    }

    public BusRoute() {
    }

    protected BusRoute(Parcel in) {
        this.routeName = in.readString();
        this.stop = new ArrayList<String>();
        this.time = new ArrayList<String>();
        in.readList(this.stop, String.class.getClassLoader());
        in.readList(this.time, String.class.getClassLoader());
    }

    public static final Parcelable.Creator<BusRoute> CREATOR = new Parcelable.Creator<BusRoute>() {
        @Override
        public BusRoute createFromParcel(Parcel source) {
            return new BusRoute(source);
        }

        @Override
        public BusRoute[] newArray(int size) {
            return new BusRoute[size];
        }
    };
}