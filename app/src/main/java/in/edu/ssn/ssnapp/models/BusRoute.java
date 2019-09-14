package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusRoute implements Parcelable, Comparable {
    int id;
    String name;
    List<String> stop;
    List<String> time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeList(this.stop);
        dest.writeList(this.time);
    }

    public BusRoute() {
    }

    protected BusRoute(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
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

    @Override
    public int compareTo(Object o) {
        if(this.getId() > ((BusRoute)o).getId())
            return 1;
        return -1;
    }
}