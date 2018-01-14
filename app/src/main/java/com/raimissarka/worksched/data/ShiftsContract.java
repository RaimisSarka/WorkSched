package com.raimissarka.worksched.data;

import android.provider.BaseColumns;

/**
 * Created by Raimis on 1/14/2018.
 */

public class ShiftsContract {

    private ShiftsContract() {}



    public static final class ShiftsEntry implements BaseColumns {
        public static final String TABLE_NAME = "shifts";
        public static final String COLUMN_SHIFT_NAME = "shift_name";
        public static final String COLUMN_SHIFT_NUMBER = "shift_number";
        public static final String COLUMN_SHIFT_TYPE = "shift_type";
        public static final String COLUMN_SHIFT_START_DATE = "shift_start_date";
    }
}
