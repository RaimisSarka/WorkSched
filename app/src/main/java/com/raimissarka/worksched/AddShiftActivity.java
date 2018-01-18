package com.raimissarka.worksched;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raimissarka.worksched.data.ShiftsContract;
import com.raimissarka.worksched.data.ShiftsDbHelper;

import java.util.Calendar;
import java.util.Date;

public class AddShiftActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private EditText mShiftName;
    private EditText mShiftNumber;
    private EditText mShiftStartDate;
    private int mShiftType = 1;
    private int mStartDate;
    private ShiftsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ImageView mSetDateButton = (ImageView) findViewById(R.id.iw_date_pick_button);



        mSetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });



        //Shift type selector
        FrameLayout mShiftTypeSelectorButton = (FrameLayout) findViewById(R.id.fl_shift_type_selector);

        mShiftTypeSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeSelectorDialogFragment mTypeSelector = new TypeSelectorDialogFragment();
                mTypeSelector.show(getSupportFragmentManager(), "typePicker");
            }
        });

        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new ShiftsDbHelper(this);

        FrameLayout mAddShiftButton = (FrameLayout) findViewById(R.id.fl_shift_add_button);



        mAddShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();


                try {
                    mSuccesfullAdd = addToShiftsDb();
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(AddShiftActivity.this, R.string.toast_succesfull_add,
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


        mShiftName = (EditText) findViewById(R.id.et_shift_name_text);
        mShiftNumber = (EditText) findViewById(R.id.et_shift_number_text_value);
        mShiftStartDate = (EditText) findViewById(R.id.et_shift_start_date_selector_value);
        TextView mShiftTypeText = (TextView) findViewById(R.id.tv_shift_type_text_value);

        Resources res = getResources();
        String[] mTypeValues = res.getStringArray(R.array.shift_types);
        if (mShiftTypeText.getText().toString() == mTypeValues[0]){
            mShiftType = 1;
        } else{
            mShiftType = 2;
        }
    }



    public Boolean addToShiftsDb() {


        if (mShiftName.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(AddShiftActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }


        if (mShiftNumber.getText().length() == 0) {
            Toast.makeText(AddShiftActivity.this, R.string.toast_empty_phone_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (mShiftStartDate.getText().length() == 0) {
            Toast.makeText(AddShiftActivity.this, R.string.toast_empty_date_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }


        if (mShiftName.getText().length() != 0 &&
                mShiftNumber.getText().length() != 0 &&
                mShiftStartDate.getText().length() != 0) {
            addNewShift(mShiftName.getText().toString(),
                    mShiftNumber.getText().toString(),
                    mShiftStartDate.getText().toString(),
                    String.valueOf(mShiftType));
            return true;
        } else {
            return false;
        }
    }



    public long addNewShift (String name, String number, String date, String shiftType) {
        ContentValues cv = new ContentValues();
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NAME, name);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NUMBER, number);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE, shiftType);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_START_DATE, date);
        return mDb.insert(ShiftsContract.ShiftsEntry.TABLE_NAME, null, cv);
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }



        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditText mShiftStartDate = getActivity().findViewById(R.id.et_shift_start_date_selector_value);

            mShiftStartDate.setText(
                    String.valueOf(year) + '/' +
                    String.valueOf(month + 1) + '/' +
                            String.valueOf(day));
        }
    }



    public static class TypeSelectorDialogFragment extends DialogFragment {


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.dialog_pick_type_message)
                    .setItems(R.array.shift_types, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView mTypeText = (TextView) getActivity().findViewById(R.id.tv_shift_type_text_value);
                            switch (which) {
                                case 0:{
                                    mTypeText.setText("1");
                                    break;
                                }
                                case 1:{
                                    mTypeText.setText("2");
                                    break;
                                }
                                default:{
                                    //allso do something
                                    break;
                                }
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }
}
