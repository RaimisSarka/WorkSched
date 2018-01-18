package com.raimissarka.worksched.data;

import android.provider.BaseColumns;

/**
 * Created by Raimis on 1/9/2018.
 */

public class WorkersContract {

    private WorkersContract() {}

    public static final class WorkersEntry implements BaseColumns {
        public static final String TABLE_NAME = "workers";
        public static final String COLUMN_WORKER_POSITION = "worker_position";
        public static final String COLUMN_WORKER_NAME = "worker_name";
        public static final String COLUMN_SHIFT_DEPENDENCY = "shift_dependency";
        public static final String COLUMN_EMPLOYMENT_DEPENDENCY = "employment_dependency";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
    }
}
