package com.elephant.multiple.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Elephant
 * @class CalendarDates
 * @description
 * @time 16/9/18 下午4:24
 */
public class CalendarDates implements Parcelable {
    public String start;
    public String end;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.start);
        dest.writeString(this.end);
    }

    public CalendarDates() {
    }

    protected CalendarDates(Parcel in) {
        this.start = in.readString();
        this.end = in.readString();
    }

    public static final Parcelable.Creator<CalendarDates> CREATOR = new Parcelable.Creator<CalendarDates>() {
        public CalendarDates createFromParcel(Parcel source) {
            return new CalendarDates(source);
        }

        public CalendarDates[] newArray(int size) {
            return new CalendarDates[size];
        }
    };
}
