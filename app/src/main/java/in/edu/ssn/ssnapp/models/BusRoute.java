package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusRoute implements Parcelable {

    boolean avail;
    String routeName;
    String dname;
    String dphone;
    //TODO add lat lng
    String via;
    List<stop> stop;

    public static class stop implements Parcelable{
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.place);
            dest.writeString(this.time);
        }

        public stop() {
        }

        protected stop(Parcel in) {
            this.place = in.readString();
            this.time = in.readString();
        }

        public static final Creator<stop> CREATOR = new Creator<stop>() {
            @Override
            public stop createFromParcel(Parcel source) {
                return new stop(source);
            }

            @Override
            public stop[] newArray(int size) {
                return new stop[size];
            }
        };
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.avail ? (byte) 1 : (byte) 0);
        dest.writeString(this.routeName);
        dest.writeString(this.dname);
        dest.writeString(this.dphone);
        dest.writeString(this.via);
        dest.writeList(this.stop);
    }

    public BusRoute() {
    }

    protected BusRoute(Parcel in) {
        this.avail = in.readByte() != 0;
        this.routeName = in.readString();
        this.dname = in.readString();
        this.dphone = in.readString();
        this.via = in.readString();
        this.stop = new ArrayList<BusRoute.stop>();
        in.readList(this.stop, BusRoute.stop.class.getClassLoader());
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
