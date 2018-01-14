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

public class EditOneWorkerActivity extends AppCompatActivity {

    private WorkersDbHelper dbHelper;
    private SQLiteDatabase mDb;
    private EditText mWorkerName;
    private EditText mWorkersPhoneNumber;
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

        //TODO get dependency

        //get phone
        String phoneNumber = getIntent().getStringExtra("WORKER_PHONE");
        mWorkersPhoneNumber = (EditText) findViewById(R.id.et_edit_worker_phone_number);
        mWorkersPhoneNumber.setText(phoneNumber);

        //get id
        String stringWorkersID = getIntent().getStringExtra("WORKERS_ID");
        workersID = Long.parseLong(stringWorkersID);
        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new WorkersDbHelper(this);

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

    private Boolean saveWorkerToDb(long id) {
        if (mWorkerName.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(EditOneWorkerActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mWorkersPhoneNumber.getText().length() == 0) {
            Toast.makeText(EditOneWorkerActivity.this, R.string.toast_empty_phone_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mWorkerName.getText().length() != 0 &&
                mWorkersPhoneNumber.getText().length() != 0) {
            updateWorker(id, mWorkerName.getText().toString(), mWorkersPhoneNumber.getText().toString());
            return true;
        } else {
            return false;
        }
    }



    public long updateWorker(long id, String name, String phone) {
        ContentValues cv = new ContentValues();
        cv.put(WorkersContract.WorkersEntry.COLUMN_WORKER_NAME, name);
        cv.put(WorkersContract.WorkersEntry.COLUMN_SHIFT_DEPENDENCY, 1);
        cv.put(WorkersContract.WorkersEntry.COLUMN_PHONE_NUMBER, phone);
        return mDb.update(WorkersContract.WorkersEntry.TABLE_NAME, cv,  "_id="+id, null);
    }
}
