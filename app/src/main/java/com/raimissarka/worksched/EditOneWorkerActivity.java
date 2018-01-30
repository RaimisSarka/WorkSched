package com.raimissarka.worksched;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raimissarka.worksched.data.EmploymentsContract;
import com.raimissarka.worksched.data.EmploymentsDbHelper;
import com.raimissarka.worksched.data.ShiftsContract;
import com.raimissarka.worksched.data.ShiftsDbHelper;
import com.raimissarka.worksched.data.WorkersContract;
import com.raimissarka.worksched.data.WorkersDbHelper;

import java.util.ArrayList;

public class EditOneWorkerActivity extends AppCompatActivity {

    private WorkersDbHelper dbHelper;
    private SQLiteDatabase mDb;
    private static ArrayList<String> mShiftsNames = new ArrayList<String>();
    private static ArrayList<Integer> mShiftsNumbers = new ArrayList<Integer>();
    private static ArrayList<String> mEmploymentNames = new ArrayList<String>();
    private static ArrayList<Integer> mEmployment_IDs = new ArrayList<Integer>();
    private ShiftsTable mTable;
    private EditText mWorkerName;
    private EditText mWorkersPosition;
    private EditText mWorkersPhoneNumber;
    private TextView mWorkersShiftDependency;
    private TextView mWorkersEmploymentDependency;
    private EmploymentTable mEmploymentTable;
    private TextView mWorkersShiftDpendencyText;
    private TextView mWorkersEmploymentDpendencyText;
    private static int mWorkersShiftDependencyInt = 1;
    private static int mWorkersEmploymentDependencyInt = 1;
    private long workersID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_one_worker);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //get name
        String s = getIntent().getStringExtra("WORKER_NAME");
        mWorkerName = (EditText) findViewById(R.id.et_edit_worker_name_text);
        mWorkerName.setText(s);

        //get position
        String workerPosition = getIntent().getStringExtra("WORKER_POSITION");
        mWorkersPosition = (EditText) findViewById(R.id.et_edit_worker_position_value);
        mWorkersPosition.setText(workerPosition);

        //get phone
        String phoneNumber = getIntent().getStringExtra("WORKER_PHONE");
        mWorkersPhoneNumber = (EditText) findViewById(R.id.et_edit_worker_phone_number);
        mWorkersPhoneNumber.setText(phoneNumber);

        //get id
        String stringWorkersID = getIntent().getStringExtra("WORKERS_ID");
        workersID = Long.parseLong(stringWorkersID);
        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new WorkersDbHelper(this);

        //get shift dependency
        String stringWorkersShiftDependency = getIntent().getStringExtra("WORKER_SHIFT_DEPENDENCY");
        mWorkersShiftDependency = (TextView) findViewById(R.id.tv_edit_worker_shift_dependency_selector_value);
        mWorkersShiftDependency.setText(stringWorkersShiftDependency);

        //get employment dependency
        String stringEmploymentDependency = getIntent().getStringExtra("WORKER_EMPLOYMENT_DEPENDENCY");
        mWorkersEmploymentDependency = (TextView) findViewById(R.id.tv_edit_worker_employment_dependency_selector_value);
        mWorkersEmploymentDependency.setText(stringEmploymentDependency);

        loadShiftsData(this);
        loadEmploymentData(this);


        //Shift dependency selector
        FrameLayout mShiftNameSelectorButton = (FrameLayout) findViewById(R.id.fl_edit_worker_shift_dependency_selector);

        //Employment dependency selector
        FrameLayout mEmploymentNameSelectorButton = (FrameLayout) findViewById(R.id.fl_edit_worker_employment_dependency_selector);

        mShiftNameSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShiftNameSelectorDialogFragment mShiftDependencySelector = new ShiftNameSelectorDialogFragment();
                mShiftDependencySelector.show(getSupportFragmentManager(), "shiftDependencyPicker");
            }
        });

        mEmploymentNameSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmploymentNameSelectorDialogFragment mEmploymentDependencySelector = new EmploymentNameSelectorDialogFragment();
                mEmploymentDependencySelector.show(getSupportFragmentManager(), "employmentDependencyPicker");
            }
        });

        //Save button
        FrameLayout mSaveWorkerButton = (FrameLayout) findViewById(R.id.fl_edit_worker_save_button);

        mSaveWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();
                try {
                    mSuccesfullAdd = saveWorkerToDb(workersID);
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(EditOneWorkerActivity.this, R.string.toast_succesfull_save,
                                Toast.LENGTH_LONG).show();
                    }
                }finally {
                    mDb.endTransaction();
                }
                if (mSuccesfullAdd) {
                    mDb.close();
                    finish();
                }
            }
        });
    }



    public void loadShiftsData (Context contex){
        mTable = new ShiftsTable(contex);
        mTable.execute(contex);
    }



    public void loadEmploymentData (Context contex){
        mEmploymentTable = new EmploymentTable(contex);
        mEmploymentTable.execute(contex);
    }


    private Boolean saveWorkerToDb(long id) {
        if (mWorkerName.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(EditOneWorkerActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (mWorkersPosition.getText().length() == 0) {
            Toast.makeText(EditOneWorkerActivity.this, R.string.toast_no_position,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mWorkersPhoneNumber.getText().length() == 0) {
            Toast.makeText(EditOneWorkerActivity.this, R.string.toast_empty_phone_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mWorkerName.getText().length() != 0 &&
                mWorkersPhoneNumber.getText().length() != 0 &&
                mWorkersPosition.getText().length() != 0) {
            updateWorker(id, mWorkerName.getText().toString(),
                    mWorkersPhoneNumber.getText().toString(),
                    mWorkersPosition.getText().toString());
            return true;
        } else {
            return false;
        }
    }



    public long updateWorker(long id, String name, String phone, String position) {
        ContentValues cv = new ContentValues();
        cv.put(WorkersContract.WorkersEntry.COLUMN_WORKER_NAME, name);
        cv.put(WorkersContract.WorkersEntry.COLUMN_WORKER_POSITION, position);
        cv.put(WorkersContract.WorkersEntry.COLUMN_SHIFT_DEPENDENCY, mWorkersShiftDependencyInt);
        cv.put(WorkersContract.WorkersEntry.COLUMN_EMPLOYMENT_DEPENDENCY, mWorkersEmploymentDependencyInt);
        cv.put(WorkersContract.WorkersEntry.COLUMN_PHONE_NUMBER, phone);
        return mDb.update(WorkersContract.WorkersEntry.TABLE_NAME, cv,  "_id="+id, null);
    }



    /**
     * Query the mDb and get all shifts names from the shifts table
     *
     * @return Cursor containing the list of shifts
     */
    private Cursor getAllShifts() {
        return mDb.query(
                ShiftsContract.ShiftsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ShiftsContract.ShiftsEntry._ID
        );
    }



    private Cursor getAllEmployments() {
        return mDb.query(
                EmploymentsContract.EmploymentsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EmploymentsContract.EmploymentsEntry._ID
        );
    }



    public class ShiftsTable extends AsyncTask<Context, Void,Void> {

        private Context mContext;
        private Cursor cursor = null;
        private Boolean mExecuting = null;



        public Cursor getCursor() {
            return cursor;
        }



        public ShiftsTable(Context context) {
            mContext = context;
        }



        @Override
        protected void onPreExecute() {
            mExecuting = true;
        }



        @Override
        protected Void doInBackground(Context... params) {

            // Create a DB helper (this will create the DB if run for the first time)
            ShiftsDbHelper dbHelper = new ShiftsDbHelper(mContext);

            // Keep a reference to the mDb until paused or killed
            mDb = dbHelper.getWritableDatabase();

            // Get all shifts info from the database and save in a cursor
            mDb.beginTransaction();
            try {
                cursor = getAllShifts();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            mExecuting = false;

            mShiftsNames.clear();
            mShiftsNumbers.clear();
            int mShiftNumberForCompare = Integer.valueOf(mWorkersShiftDependency.getText().toString());

            int i = 0;
            while (cursor.moveToPosition(i)){
                mShiftsNames.add (cursor.getString(cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NAME)));
                mShiftsNumbers.add (cursor.getInt(cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NUMBER)));

                //Set text representation of Shift number
                if (mShiftsNumbers.get(i) == mShiftNumberForCompare){
                    mWorkersShiftDependency.setText(mShiftsNames.get(i));
                    mWorkersShiftDependencyInt = Integer.valueOf(mShiftsNumbers.get(i));
                }
                i++;
            }

        }
    }



    public class EmploymentTable extends AsyncTask<Context, Void,Void> {

        private Context mContext;
        private Cursor cursor = null;
        private Boolean mExecuting = null;
        private ProgressBar mLoadingIndicator;



        public Cursor getCursor() {
            return cursor;
        }



        public EmploymentTable(Context context) {
            mContext = context;
        }



        @Override
        protected void onPreExecute() {
            mExecuting = true;
        }



        @Override
        protected Void doInBackground(Context... params) {

            // Create a DB helper (this will create the DB if run for the first time)
            EmploymentsDbHelper dbHelper = new EmploymentsDbHelper(mContext);

            // Keep a reference to the mDb until paused or killed
            mDb = dbHelper.getWritableDatabase();

            // Get all shifts info from the database and save in a cursor
            mDb.beginTransaction();
            try {
                cursor = getAllEmployments();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            mExecuting = false;

            mEmploymentNames.clear();
            int m_IdForcompare = Integer.valueOf(mWorkersEmploymentDependency.getText().toString());

            int i = 0;
            while (cursor.moveToPosition(i)){
                mEmploymentNames.add (cursor.getString(cursor.getColumnIndex(EmploymentsContract.EmploymentsEntry.COLUMN_EMPLOYMENT_NAME)));
                mEmployment_IDs.add (cursor.getInt(cursor.getColumnIndex((EmploymentsContract.EmploymentsEntry._ID))));

                //Set text representation of Employment _ID
                if (mEmployment_IDs.get(i) == m_IdForcompare) {
                    mWorkersEmploymentDependency.setText(mEmploymentNames.get(i));
                    mWorkersEmploymentDependencyInt = Integer.valueOf(mEmployment_IDs.get(i));
                }
                i++;
            }

        }
    }



    public static class ShiftNameSelectorDialogFragment extends DialogFragment {


        private String[] mInnerStrings = null;
        private Integer[] mInnerInts = null;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            mInnerStrings = mShiftsNames.toArray(new String[0]);
            mInnerInts = mShiftsNumbers.toArray(new Integer[0]);

            builder.setTitle(R.string.dialog_pick_shift_name_message)
                    .setItems(mInnerStrings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView mShiftDependencyText = (TextView) getActivity().findViewById(R.id.tv_edit_worker_shift_dependency_selector_value);
                            mShiftDependencyText.setText(mInnerStrings[which]);
                            mWorkersShiftDependencyInt = mInnerInts[which];
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }



    public static class EmploymentNameSelectorDialogFragment extends DialogFragment {


        private String[] mInnerStrings = null;
        private Integer[] mInnerInts = null;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            mInnerStrings = mEmploymentNames.toArray(new String[0]);
            mInnerInts = mEmployment_IDs.toArray(new Integer[0]);

            builder.setTitle(R.string.dialog_pick_employment_name_message)
                    .setItems(mInnerStrings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView mEmploymentDependencyText = (TextView) getActivity().findViewById(R.id.tv_edit_worker_employment_dependency_selector_value);
                            mEmploymentDependencyText.setText(mInnerStrings[which]);
                            mWorkersEmploymentDependencyInt = mInnerInts[which];
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }

}
