package com.raimissarka.worksched;

/**
 * Created by Raimis on 1/22/2018.
 */

public class DayData {

    private String date;
    private String shiftNo;
    private String workerName;
    private Boolean weekend;
    private Boolean today;

    public DayData(){

    }


    public Boolean getToday() {
        return today;
    }

    public DayData(String date, String shiftNo, String workerName, Boolean weekend, Boolean today) {
        this.date = date;
        this.shiftNo = shiftNo;
        this.workerName = workerName;
        this.weekend = weekend;
        this.today = today;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Boolean getWeekend() {
        return weekend;
    }

}
