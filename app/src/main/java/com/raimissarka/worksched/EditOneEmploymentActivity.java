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

import com.raimissarka.worksched.data.EmploymentsContract;
import com.raimissarka.worksched.data.EmploymentsDbHelper;

public class EditOneEmploymentActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private EmploymentsDbHelper dbHelper;
    private EditText mEmploymentNameEditText;
    private long mEmploymentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_one_employment);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        dbHelper = new EmploymentsDbHelper(this);

        //get name
        String name = getIntent().getStringExtra("EMPLOYMENT_NAME");
        mEmploymentNameEditText = (EditText) findViewById(R.id.et_edit_employment_name_text);
        mEmploymentNameEditText.setText(name);

        //get _ID
        mEmploymentID = Long.parseLong(getIntent().getStringExtra("EMPLOYMENT_ID"));

        //save button
        FrameLayout mEditEmploymentSaveButton = (FrameLayout) findViewById(R.id.fl_edit_employment_save_button);


        mEditEmploymentSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();
                try {
                    mSuccesfullAdd = saveEmploymentToDb(mEmploymentID);
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(EditOneEmploymentActivity.this, R.string.toast_succesfull_save,
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



    private Boolean saveEmploymentToDb(long id) {
        if (mEmploymentNameEditText.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(EditOneEmploymentActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mEmploymentNameEditText.getText().length() != 0) {

            updateEmployment(mEmploymentID,
                    mEmploymentNameEditText.getText().toString());

            return true;
        } else {
            return false;
        }
    }



    public long updateEmployment(long id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(EmploymentsContract.EmploymentsEntry.COLUMN_EMPLOYMENT_NAME, name);
        return mDb.update(EmploymentsContract.EmploymentsEntry.TABLE_NAME, cv,  "_id="+id, null);
    }
}
