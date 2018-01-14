package com.raimissarka.worksched;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.raimissarka.worksched.data.WorkersContract;
import com.raimissarka.worksched.data.WorkersDbHelper;

public class AddWorkerActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private EditText mWorkerName;
    private EditText mWorkersPhoneNumber;
    private WorkersDbHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new WorkersDbHelper(this);

        FrameLayout mAddWorkerButton = (FrameLayout) findViewById(R.id.fl_worker_add_button);



        mAddWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();


                try {
                    mSuccesfullAdd = addToWorkersDb();
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(AddWorkerActivity.this, R.string.toast_succesfull_add,
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


        mWorkerName = (EditText) findViewById(R.id.et_worker_name_text);
        mWorkersPhoneNumber = (EditText) findViewById(R.id.et_worker_phone_number);
    }



    public Boolean addToWorkersDb() {


        if (mWorkerName.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(AddWorkerActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }


        if (mWorkersPhoneNumber.getText().length() == 0) {
            Toast.makeText(AddWorkerActivity.this, R.string.toast_empty_phone_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }


        if (mWorkerName.getText().length() != 0 &&
                mWorkersPhoneNumber.getText().length() != 0) {
            addNewWorker(mWorkerName.getText().toString(), mWorkersPhoneNumber.getText().toString());
            return true;
        } else {
            return false;
        }
    }



    public long addNewWorker (String name, String phone) {
        ContentValues cv = new ContentValues();
        cv.put(WorkersContract.WorkersEntry.COLUMN_WORKER_NAME, name);
        cv.put(WorkersContract.WorkersEntry.COLUMN_SHIFT_DEPENDENCY, 1);
        cv.put(WorkersContract.WorkersEntry.COLUMN_PHONE_NUMBER, phone);
        return mDb.insert(WorkersContract.WorkersEntry.TABLE_NAME, null, cv);
    }



    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
