package com.igeak.test.geakapidemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * Created by hasen on 15-12-18.
 */
public class GeakWeatherInfo implements Parcelable {

    public static final Creator<GeakWeatherInfo> CREATOR = new Creator<GeakWeatherInfo>() {

        @Override
        public GeakWeatherInfo[] newArray(int size) {
            return new GeakWeatherInfo[size];
        }

        @Override
        public GeakWeatherInfo createFromParcel(Parcel source) {
            return new GeakWeatherInfo(source);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(regionCode);
        dest.writeString(regionName);
        dest.writeString(regionPinyin);
        dest.writeInt(currentTemp);
        dest.writeString(currentWeaType);
        dest.writeString(sunrise);
        dest.writeString(sunset);
        dest.writeString(currentWeaCN);
        dest.writeString(currentWeaEN);
        dest.writeString(humidity);
        dest.writeInt(aqi);
        dest.writeString(wde);
        dest.writeString(wse);
    }

    public static final class Columns implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.parse("content://com.igeak.weather/geakweatherinfo");

        public static final String REGION_CODE = "regionCode";
        public static final String REGION_NAME = "regionName";
        public static final String REGION_PINYIN = "regionPinyin";
        public static final String UPDATE_TAG = "updateTag";
        public static final String CURRENT_TEMP = "currentTemp";
        public static final String CURRENT_WEA_TYPE = "currentWeaType";
        public static final String SUNRISE = "sunrise";
        public static final String SUNSET = "sunset";
        public static final String CURRENT_WEA_CN = "currentWeaCN";
        public static final String CURRENT_WEA_EN = "currentWeaEN";
        public static final String HUMIDITY = "humidity";
        public static final String AQI = "aqi";
        public static final String WDE = "wde";
        public static final String WSE = "wse";

        public static final int GEAK_WEATHER_INFO_REGION_CODE = 1;
        public static final int GEAK_WEATHER_INFO_REGION_NAME = 2;
        public static final int GEAK_WEATHER_INFO_REGION_PINYIN = 3;
        public static final int GEAK_WEATHER_INFO_UPDATE_TAG = 4;
        public static final int GEAK_WEATHER_INFO_CURRENT_TEMP = 5;
        public static final int GEAK_WEATHER_INFO_CURRENT_WEA_TYPE = 6;
        public static final int GEAK_WEATHER_INFO_SUNRISE = 7;
        public static final int GEAK_WEATHER_INFO_SUNSET = 8;
        public static final int GEAK_WEATHER_INFO_CURRENT_WEA_CN = 9;
        public static final int GEAK_WEATHER_INFO_CURRENT_WEA_EN = 10;
        public static final int GEAK_WEATHER_INFO_HUMIDITY = 11;
        public static final int GEAK_WEATHER_INFO_AQI = 12;
        public static final int GEAK_WEATHER_INFO_WDE = 13;
        public static final int GEAK_WEATHER_INFO_WSE = 14;


        public static final String[] GEAK_WEATHER_INFO_QUERY_COLUMNS = {
                REGION_CODE, REGION_NAME, REGION_PINYIN, UPDATE_TAG, CURRENT_TEMP, CURRENT_WEA_TYPE
                , SUNRISE, SUNSET};

    }

    public String regionCode;
    public String regionName;
    public String regionPinyin;
    public int currentTemp;
    public String currentWeaType;
    public String sunrise;
    public String sunset;
    public String currentWeaCN;
    public String currentWeaEN;
    public String humidity;
    public int aqi;
    public String wde;
    public String wse;

    public GeakWeatherInfo() {
    }

    public GeakWeatherInfo(Parcel parcel) {
        regionCode = parcel.readString();
        regionName = parcel.readString();
        regionPinyin = parcel.readString();
        currentTemp = parcel.readInt();
        currentWeaType = parcel.readString();
        sunrise = parcel.readString();
        sunset = parcel.readString();
        currentWeaCN = parcel.readString();
        currentWeaEN = parcel.readString();
        humidity = parcel.readString();
        aqi = parcel.readInt();
        wde = parcel.readString();
        wse = parcel.readString();
    }

    public GeakWeatherInfo(Cursor cursor) {
        regionCode = cursor.getString(Columns.GEAK_WEATHER_INFO_REGION_CODE);
        regionName = cursor.getString(Columns.GEAK_WEATHER_INFO_REGION_NAME);
        regionPinyin = cursor.getString(Columns.GEAK_WEATHER_INFO_REGION_PINYIN);
        currentTemp = cursor.getInt(Columns.GEAK_WEATHER_INFO_CURRENT_TEMP);
        currentWeaType = cursor.getString(Columns.GEAK_WEATHER_INFO_CURRENT_WEA_TYPE);
        sunrise = cursor.getString(Columns.GEAK_WEATHER_INFO_SUNRISE);
        sunset = cursor.getString(Columns.GEAK_WEATHER_INFO_SUNSET);
        currentWeaCN = cursor.getString(Columns.GEAK_WEATHER_INFO_CURRENT_WEA_CN);
        currentWeaEN = cursor.getString(Columns.GEAK_WEATHER_INFO_CURRENT_WEA_EN);
        humidity = cursor.getString(Columns.GEAK_WEATHER_INFO_HUMIDITY);
        aqi = cursor.getInt(Columns.GEAK_WEATHER_INFO_AQI);
        wde = cursor.getString(Columns.GEAK_WEATHER_INFO_WDE);
        wse = cursor.getString(Columns.GEAK_WEATHER_INFO_WSE);
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();
        values.put(Columns.REGION_CODE,regionCode);
        values.put(Columns.REGION_NAME,regionName);
        values.put(Columns.REGION_PINYIN,regionPinyin);
        values.put(Columns.CURRENT_TEMP,currentTemp);
        values.put(Columns.CURRENT_WEA_TYPE,currentWeaType);
        values.put(Columns.SUNRISE,sunrise);
        values.put(Columns.SUNSET,sunset);
        values.put(Columns.CURRENT_WEA_CN,currentWeaCN);
        values.put(Columns.CURRENT_WEA_EN,currentWeaEN);
        values.put(Columns.HUMIDITY,humidity);
        values.put(Columns.AQI,aqi);
        values.put(Columns.WDE,wde);
        values.put(Columns.WSE,wse);
        return values;
    }

    @Override
    public String toString() {
        return "GeakWeatherInfo{" +
                "regionCode='" + regionCode + '\'' +
                ", regionName='" + regionName + '\'' +
                ", regionPinyin='" + regionPinyin + '\'' +
                ", currentTemp=" + currentTemp +
                ", currentWeaType='" + currentWeaType + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", currentWeaCN='" + currentWeaCN + '\'' +
                ", currentWeaEN='" + currentWeaEN + '\'' +
                ", humidity='" + humidity + '\'' +
                ", aqi=" + aqi +
                ", wde='" + wde + '\'' +
                ", wse='" + wse +"}";
    }
}
