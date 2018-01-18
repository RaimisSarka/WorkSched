package com.raimissarka.worksched.data;

import android.provider.BaseColumns;

/**
 * Created by Raimis on 1/16/2018.
 */

public class EmploymentsContract {

    private EmploymentsContract() {}


    public static final class EmploymentsEntry implements BaseColumns {
        public static final String TABLE_NAME = "employments";
        public static final String COLUMN_EMPLOYMENT_NAME = "employment_name";
    }
}
