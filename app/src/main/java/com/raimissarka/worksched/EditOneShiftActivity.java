package com.raimissarka.worksched;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
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

import com.raimissarka.worksched.R;
import com.raimissarka.worksched.data.ShiftsContract;
import com.raimissarka.worksched.data.ShiftsDbHelper;

import java.util.Calendar;

public class EditOneShiftActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private ShiftsDbHelper dbHelper;
    private EditText mShiftNameEditText;
    private EditText mShiftNumberEditText;
    private TextView mShiftTypeTextView;
    private EditText mStartDateEditText;
    private long mShiftID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_one_shift);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ImageView mSetDateButton = (ImageView) findViewById(R.id.iw_edit_date_pick_button);



        mSetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EditOneShiftActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        dbHelper = new ShiftsDbHelper(this);

        //get name
        String name = getIntent().getStringExtra("SHIFT_NAME");
        mShiftNameEditText = (EditText) findViewById(R.id.et_edit_shift_name_text);
        mShiftNameEditText.setText(name);

        //get number
        String number = getIntent().getStringExtra("SHIFT_NUMBER");
        mShiftNumberEditText = (EditText) findViewById(R.id.et_edit_shift_number_text_value);
        mShiftNumberEditText.setText(number);

        //get type
        String type = getIntent().getStringExtra("SHIFT_TYPE");
        mShiftTypeTextView = (TextView) findViewById(R.id.tv_edit_shift_type_text_value);
        mShiftTypeTextView.setText(type);

        //get date
        String date  = getIntent().getStringExtra("SHIFT_START_DATE");
        mStartDateEditText = (EditText) findViewById(R.id.et_edit_shift_start_date_selector_value);
        mStartDateEditText.setText(date);

        //get _ID
        mShiftID = Long.parseLong(getIntent().getStringExtra("SHIFT_ID"));

        //Shift type selector
        FrameLayout mShiftTypeSelectorButton = (FrameLayout) findViewById(R.id.fl_edit_shift_type_selector);

        mShiftTypeSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeSelectorDialogFragment mTypeSelector = new TypeSelectorDialogFragment();
                mTypeSelector.show(getSupportFragmentManager(), "typePicker");
            }
        });

        //save button
        FrameLayout mEditShiftSaveButton = (FrameLayout) findViewById(R.id.fl_edit_shift_save_button);



        mEditShiftSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean mSuccesfullAdd = false;
                mDb = dbHelper.getWritableDatabase();
                mDb.beginTransaction();
                try {
                    mSuccesfullAdd = saveShiftToDb(mShiftID);
                    if (mSuccesfullAdd) {
                        mDb.setTransactionSuccessful();
                        Toast.makeText(EditOneShiftActivity.this, R.string.toast_succesfull_save,
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



    private Boolean saveShiftToDb(long id) {
        if (mShiftNameEditText.getText().length() == 0) {
            //checking if the name field is not empty
            Toast.makeText(EditOneShiftActivity.this, R.string.toast_empty_name_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (mShiftNumberEditText.getText().length() == 0) {
            Toast.makeText(EditOneShiftActivity.this, R.string.toast_empty_phone_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (mShiftTypeTextView.getText().length() == 0) {
            Toast.makeText(EditOneShiftActivity.this, R.string.toast_no_type,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (mStartDateEditText.getText().length() == 0) {
            Toast.makeText(EditOneShiftActivity.this, R.string.toast_empty_date_field,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(mShiftNameEditText.getText().length() != 0 &&
                mShiftNumberEditText.getText().length() != 0 &&
                mShiftTypeTextView.getText().length() != 0 &&
                mStartDateEditText.getText().length() != 0) {

            updateShift(mShiftID,
                    mShiftNameEditText.getText().toString(),
                    mShiftNumberEditText.getText().toString(),
                    mShiftTypeTextView.getText().toString(),
                    mStartDateEditText.getText().toString());

            return true;
        } else {
            return false;
        }
    }



    public long updateShift(long id, String name, String number, String type, String date) {
        ContentValues cv = new ContentValues();
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NAME, name);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NUMBER, number);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE, type);
        cv.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_START_DATE, date);
        return mDb.update(ShiftsContract.ShiftsEntry.TABLE_NAME, cv,  "_id="+id, null);
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
            EditText mShiftStartDate = getActivity().findViewById(R.id.et_edit_shift_start_date_selector_value);

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
                           TextView mTypeText = (TextView) getActivity().findViewById(R.id.tv_edit_shift_type_text_value);
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
