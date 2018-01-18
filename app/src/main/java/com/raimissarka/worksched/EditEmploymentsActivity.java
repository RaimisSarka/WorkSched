package com.raimissarka.worksched;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.raimissarka.worksched.data.EmploymentsContract;
import com.raimissarka.worksched.data.EmploymentsDbHelper;

public class EditEmploymentsActivity extends AppCompatActivity {

    private EmploymentsAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EmploymentsTable mTable;
    private RecyclerView employmentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employments);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton mAddEmploymentButton = (FloatingActionButton) findViewById(R.id.fab_add_employment);

        mAddEmploymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEmploymentsActivity.this, AddEmploymentActivity.class);
                startActivity(intent);
            }
        });

        loadData(this);
    }



    public void loadData (Context contex){
        mTable = new EmploymentsTable(contex);
        mTable.execute(contex);
    }



    @Override
    protected void onResume() {
        if (mTable.getCursor() != null) {
            loadData(this);
        }
        super.onResume();
    }



    /**
     * Query the mDb and get all shifts names from the shifts table
     *
     * @return Cursor containing the list of shifts
     */
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



    private boolean removeEmployments(long id) {
        return mDb.delete(EmploymentsContract.EmploymentsEntry.TABLE_NAME,
                EmploymentsContract.EmploymentsEntry._ID + "=" + id, null) > 0;
    }



    public class EmploymentsTable extends AsyncTask<Context, Void,Void> {

        private Context mContext;
        private Cursor cursor = null;
        private Boolean mExecuting = null;
        private ProgressBar mLoadingIndicator;



        public Cursor getCursor() {
            return cursor;
        }



        public EmploymentsTable(Context context) {
            mContext = context;
        }



        @Override
        protected void onPreExecute() {
            ProgressBar mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_employment_loading_indicator);
            mExecuting = true;
            if (cursor == null){
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
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
            ProgressBar mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_employment_loading_indicator);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Create an adapter for that cursor to display the data
            mAdapter = new EmploymentsAdapter(mContext, cursor);

            employmentRecyclerView = (RecyclerView) EditEmploymentsActivity.this.findViewById(R.id.rv_edit_employments);

            // Set layout for the RecyclerView, because it's a list we are using the linear layout
            employmentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            // Link the adapter to the RecyclerView
            employmentRecyclerView.setAdapter(mAdapter);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {



                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }



                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    if (viewHolder.itemView.getTag() == null){
                        Toast.makeText(EditEmploymentsActivity.this, R.string.toast_failed,
                                Toast.LENGTH_LONG).show();
                    } else {
                        final long id = (long) viewHolder.itemView.getTag();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditEmploymentsActivity.this);
                        alertDialog.setTitle(R.string.dialog_delete_item_title).
                                setMessage(R.string.dialog_delete_item_text);

                        alertDialog.setPositiveButton(R.string.dialog_positive_button_label,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeEmployments(id);
                                        loadData(mContext);
                                    }
                                });

                        alertDialog.setNegativeButton(R.string.dialog_negative_button_label,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadData(mContext);
                                    }
                                });
                        alertDialog.show();

                    }
                }
            }).attachToRecyclerView(employmentRecyclerView);
        }
    }
}
