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

public class AddEmploymentActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private EmploymentsDbHelper dbHelper;
    private EditText mEmploymentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employment);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new EmploymentsDbHelper(this);

        FrameLayout mAddEmploymentButton = (FrameLayout) findViewById(R.id.fl_employment_add_button);



        mAddEmploymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();


                try {
                    mSuccesfullAdd = addToEmploymentsDb();
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(AddEmploymentActivity.this, R.string.toast_succesfull_add,
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


        mEmploymentName = (EditText) findViewById(R.id.et_employment_name_text);
    }



    public Boolean addToEmploymentsDb() {


        if (mEmploymentName.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(AddEmploymentActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (mEmploymentName.getText().length() != 0) {
            addNewEmployment(mEmploymentName.getText().toString());
            return true;
        } else {
            return false;
        }
    }



    public long addNewEmployment (String name) {
        ContentValues cv = new ContentValues();
        cv.put(EmploymentsContract.EmploymentsEntry.COLUMN_EMPLOYMENT_NAME, name);
        return mDb.insert(EmploymentsContract.EmploymentsEntry.TABLE_NAME, null, cv);
    }
}
