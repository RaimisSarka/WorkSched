package com.raimissarka.worksched;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raimissarka.worksched.data.EmploymentsContract;
import com.raimissarka.worksched.data.EmploymentsDbHelper;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private static ArrayList<String> mEmploymentNames = new ArrayList<String>();
    private static ArrayList<Integer> mEmployment_IDs = new ArrayList<Integer>();
    private EmploymentTable mEmploymentTable;
    private static TextView mEmploymentToShowTextView;
    private FrameLayout mEmploymentNameSelectorButton;
    static SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        FrameLayout mEditShiftsButton = (FrameLayout) findViewById(R.id.fl_edit_shifts);
        FrameLayout mEditWorkersButton = (FrameLayout) findViewById(R.id.fl_edit_workers);
        FrameLayout mEditEmploymentsButton = (FrameLayout) findViewById(R.id.fl_edit_employments);

        sharedPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
        String mEmploymentToShow = sharedPreferences.getString(MainActivity.EMPLOYMENT_TO_SHOW_KEY, "");
        if(!mEmploymentToShow.equalsIgnoreCase(""))
        {
            mEmploymentToShowTextView = (TextView) findViewById(R.id.tv_show_employment_text_value);
            mEmploymentToShowTextView.setText(mEmploymentToShow);
        }

        //Employment selector
        mEmploymentNameSelectorButton = (FrameLayout) findViewById(R.id.fl_show_employment_selector);

        mEditShiftsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditShiftsActivity.class);
                startActivity(intent);
            }
        });

        mEditWorkersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditWorkersActivity.class);
                startActivity(intent);
            }
        });

        mEditEmploymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditEmploymentsActivity.class);
                startActivity(intent);
            }
        });

        mEmploymentNameSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
                EmploymentNameSelectorDialogFragment mEmploymentShowSelector = new EmploymentNameSelectorDialogFragment();
                mEmploymentShowSelector.show(getSupportFragmentManager(), "employmentShowPicker");
            }
        });

        loadEmploymentData(this);
    }



    public void loadEmploymentData (Context contex){
        mEmploymentTable = new EmploymentTable(contex);
        mEmploymentTable.execute(contex);
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

            int i = 0;
            while (cursor.moveToPosition(i)){
                mEmploymentNames.add (cursor.getString(cursor.getColumnIndex(EmploymentsContract.EmploymentsEntry.COLUMN_EMPLOYMENT_NAME)));
                mEmployment_IDs.add (cursor.getInt(cursor.getColumnIndex((EmploymentsContract.EmploymentsEntry._ID))));
                i++;
            }

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
                            TextView mShowEmploymentText = (TextView) getActivity().findViewById(R.id.tv_show_employment_text_value);
                            mShowEmploymentText.setText(mInnerStrings[which]);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(MainActivity.EMPLOYMENT_TO_SHOW_KEY, mInnerStrings[which]);
                            editor.putInt(MainActivity.EMPLOYMENT_TO_SHOW_ID_KEY, mInnerInts[which]);
                            editor.commit();
                            mEmploymentToShowTextView.setText(mInnerStrings[which]);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }

}
