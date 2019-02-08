package com.raimissarka.worksched;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raimissarka.worksched.data.EmploymentsContract;
import com.raimissarka.worksched.data.EmploymentsDbHelper;
import com.raimissarka.worksched.data.ShiftsContract;
import com.raimissarka.worksched.data.ShiftsDbHelper;
import com.raimissarka.worksched.data.WorkersContract;
import com.raimissarka.worksched.data.WorkersDbHelper;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String EMPLOYMENT_TO_SHOW_KEY = "employment_to_show";
    public static String EMPLOYMENT_TO_SHOW_ID_KEY = "employment_to_show_id";

    private List<DayData> mDayData= new ArrayList<>();
    private SQLiteDatabase mDb;
    private Cursor mWorkersCursor;
    private Cursor mShiftsCursor;
    private Cursor mEmploymentsCursor;
    private int mMainYear;
    private int mMainDay;
    private int mEmploymentToShowInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setToday();
        setupSharedPrefereces();
        //loadData(this);
    }

    public void setToday(){
        TextView mDateTextView = (TextView) findViewById(R.id.tvDate);

        Calendar calendarToday = Calendar.getInstance();
        Date mTodayDate = calendarToday.getTime();
        int mDayOfWeekToday = calendarToday.get(Calendar.DAY_OF_WEEK);
        mMainYear = calendarToday.get(Calendar.YEAR);
        mMainDay = calendarToday.get(Calendar.DAY_OF_YEAR);
        String[] mDayNamesArray = getResources().getStringArray(R.array.weekdays_name);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String mTodayDateString =  formatter.format(mTodayDate).toString();

        mDateTextView.setText(mTodayDateString + " " + mDayNamesArray[mDayOfWeekToday - 1]);
    }

    public void loadData (Context contex){
        WorkersTable mWorkersTable = new WorkersTable(contex);
        mWorkersTable.execute(contex);
    }

    private Cursor getAllWorkers() {
        return mDb.query(
                WorkersContract.WorkersEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WorkersContract.WorkersEntry._ID
        );
    }

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

    private void setupSharedPrefereces(){
        SharedPreferences sharedPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);;
        String mEmploymentToShowText = sharedPreferences.getString(EMPLOYMENT_TO_SHOW_KEY, "");
        mEmploymentToShowInt = sharedPreferences.getInt(EMPLOYMENT_TO_SHOW_ID_KEY, 0);
        if(!mEmploymentToShowText.equalsIgnoreCase(""))
        {
            //TODO do something..
        }
    }

    public String whitchShiftThatDay (int day, int year) throws ParseException {

        //TODO remove static text
        String mShiftNoToReturn = "No such shift";

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, day);
        Date mCheckDay = calendar.getTime();


        if (mShiftsCursor.moveToFirst()){
            do{
                String mShiftStartDate =
                        mShiftsCursor.getString(mShiftsCursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_START_DATE));
                int mShiftNumber =
                        mShiftsCursor.getInt(mShiftsCursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NUMBER));

                Date mShiftStartDay = new SimpleDateFormat("yyyy/MM/dd").parse(mShiftStartDate);

                if (mCheckDay.after(mShiftStartDay)){
                    Calendar mSearchCalendar = Calendar.getInstance();
                    mSearchCalendar.set(Calendar.YEAR, year);
                    mSearchCalendar.set(Calendar.DAY_OF_YEAR, day);

                    Date mSearchDate;
                    mSearchDate = mShiftStartDay;

                    int mDecrDay = day;
                    int mDecrYear = year;
                    Date mDecCheckDate = mCheckDay;

                    while (mSearchDate.before(mDecCheckDate)){
                        mDecrDay = mDecrDay - 8;
                        mSearchCalendar.set(Calendar.YEAR, year);

                        if (mDecrDay <= 0){
                            mDecrYear--;
                            mSearchCalendar.set(Calendar.YEAR, mDecrYear);
                            mDecrDay = mSearchCalendar.getActualMaximum(Calendar.YEAR);
                            mSearchCalendar.set(Calendar.DAY_OF_YEAR, mDecrDay);
                        }

                        mSearchCalendar.set(Calendar.DAY_OF_YEAR, mDecrDay);
                        mDecCheckDate = mSearchCalendar.getTime();
                    }


                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

                    String[] mDateFromSearchDate =  formatter.format(mSearchDate).toString().split("-");
                    int mDayFromSearchDate = Integer.valueOf(mDateFromSearchDate[0]);

                    if ((mDayFromSearchDate - mDecrDay) == 8 || (mDayFromSearchDate - mDecrDay) == 7) {
                        mShiftNoToReturn = String.valueOf(mShiftNumber);
                    } else {
                        mShiftNoToReturn = "0";
                    }

                } else {
                    Calendar mSearchCalendar = Calendar.getInstance();
                    mSearchCalendar.set(Calendar.YEAR, year);
                    mSearchCalendar.set(Calendar.DAY_OF_YEAR, day);

                    Date mSearchDate;
                    mSearchDate = mShiftStartDay;


                    int mIncrDay = day;
                    int mIncrYear = year;

                    Date mIncCheckDate = mCheckDay;

                    while (mSearchDate.after(mIncCheckDate)){
                        mIncrDay = mIncrDay + 8;
                        mSearchCalendar.set(Calendar.YEAR, year);

                        if (mIncrDay >= mSearchCalendar.getActualMaximum(Calendar.YEAR)){
                            mIncrYear++;
                            mSearchCalendar.set(Calendar.YEAR, mIncrYear);
                            mIncrDay = 1;
                            mSearchCalendar.set(Calendar.DAY_OF_YEAR, mIncrDay);
                        }

                        mSearchCalendar.set(Calendar.DAY_OF_YEAR, mIncrDay);
                        mIncCheckDate = mSearchCalendar.getTime();

                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

                    String[] mDateFromSearchDate =  formatter.format(mSearchDate).toString().split("-");
                    int mDayFromSearchDate = Integer.valueOf(mDateFromSearchDate[0]);

                    if ((mIncrDay - mDayFromSearchDate) == 1 || (mIncrDay == mDayFromSearchDate)){
                        mShiftNoToReturn = String.valueOf(mShiftNumber);;
                    } else {
                        mShiftNoToReturn = "0";
                    }
                }

            }while(mShiftsCursor.moveToNext() && mShiftNoToReturn.equals("0") );
        }

        //TODO calculate shift
        return mShiftNoToReturn;
    }

    public String getWorkersName (String mShNo){
        String mWorkersNameByShift = "No such worker";

        if (mWorkersCursor.moveToFirst()){
            do{
                int mShiftDependency =
                        mWorkersCursor.getInt(mWorkersCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_SHIFT_DEPENDENCY));
                if (mShiftDependency == Integer.valueOf(mShNo)){
                    int mEmploymentDependency =
                            mWorkersCursor.getInt(mWorkersCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_EMPLOYMENT_DEPENDENCY));
                            if (mEmploymentDependency == mEmploymentToShowInt){
                                mWorkersNameByShift =
                                        mWorkersCursor.getString(mWorkersCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_WORKER_NAME));
                            }
                }
            }while(mWorkersCursor.moveToNext());
        }

        return mWorkersNameByShift;
    }

    public void createYearList(int year){
        Calendar calendar = Calendar.getInstance();
        Boolean weekEnd = false;
        Boolean today = false;
        calendar.set(Calendar.YEAR, year);
        int numOfDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        int dayNo;
         for (dayNo = 1; dayNo <= numOfDays + 2; dayNo++){

             String mShiftNo = "0";
             try {
                 mShiftNo = whitchShiftThatDay(dayNo, year);
             } catch (ParseException e){

                 Log.e("e","Exception...");

             }

             calendar.set(Calendar.DAY_OF_YEAR, dayNo);
             int month = calendar.get(Calendar.MONTH) + 1;
             int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
             int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

             if (dayOfWeek == 1) {
                 dayOfWeek = 7;
             } else {
                 dayOfWeek --;
             }

             if (dayOfWeek == 6 || dayOfWeek == 7){
                 weekEnd = true;
             } else {
                 weekEnd = false;
             }

             if (calendar.get(Calendar.DAY_OF_YEAR) == mMainDay) {
                 today = true;
             } else {
                 today = false;
             }


             DayData dataToAdd = new DayData(String.valueOf(month)+"/"+dayOfMonth+"",
                     mShiftNo,
                     getWorkersName(mShiftNo), weekEnd, today);
             mDayData.add(dataToAdd);
         }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class WorkersTable extends AsyncTask<Context, Void,Void> {

        private Context mContext;
        private Cursor cursor = null;
        private Boolean mExecuting = null;
        private ProgressBar mLoadingIndicator;



        public WorkersTable(Context context) {
            mContext = context;
        }



        @Override
        protected void onPreExecute() {
            mExecuting = true;
            mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_main_loading_indicator);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }



        @Override
        protected Void doInBackground(Context... params) {

            WorkersDbHelper dbHelperWorkers = new WorkersDbHelper(mContext);
            EmploymentsDbHelper dbHelperEmployments = new EmploymentsDbHelper(mContext);
            ShiftsDbHelper dbHelperShifts = new ShiftsDbHelper(mContext);

            mDb = dbHelperWorkers.getWritableDatabase();
            mDb.beginTransaction();
            try {
                mWorkersCursor = getAllWorkers();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }

            mDb = dbHelperEmployments.getWritableDatabase();
            mDb.beginTransaction();
            try {
                mEmploymentsCursor = getAllEmployments();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }

            mDb = dbHelperShifts.getWritableDatabase();
            mDb.beginTransaction();
            try {
                mShiftsCursor = getAllShifts();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }

            createYearList(mMainYear);
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            mExecuting = false;
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Create an adapter for that cursor to display the data
            ScheduleAdapter mAdapter = new ScheduleAdapter(mDayData);

            RecyclerView scheduleRecyclerView = (RecyclerView) MainActivity.this.findViewById(R.id.rv_workers);

            // Set layout for the RecyclerView, because it's a list we are using the linear layout
            scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            // Link the adapter to the RecyclerView
            scheduleRecyclerView.setAdapter(mAdapter);

            TextView mDateTextView = (TextView) findViewById(R.id.tvDate);
            mDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerView scheduleRecyclerView = (RecyclerView) MainActivity.this.findViewById(R.id.rv_workers);
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) scheduleRecyclerView.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(mMainDay - 1, 0);
                }
            });

            LinearLayoutManager mLayoutManager = (LinearLayoutManager) scheduleRecyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(mMainDay - 1, 0);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShiftsCursor.close();
        mWorkersCursor.close();
        mEmploymentsCursor.close();
    }

}
