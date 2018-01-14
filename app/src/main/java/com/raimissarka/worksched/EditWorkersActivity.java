package com.raimissarka.worksched;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.raimissarka.worksched.data.WorkersContract;
import com.raimissarka.worksched.data.WorkersDbHelper;

public class EditWorkersActivity extends AppCompatActivity{

    private WorkersAdapter mAdapter;
    private SQLiteDatabase mDb;
    private WorkersTable mTable;
    private RecyclerView workersRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workers);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        EditText mSearchText = (EditText) findViewById(R.id.et_workers_filter);

        FloatingActionButton mAddWorkerButton = (FloatingActionButton) findViewById(R.id.fab_add_worker);

        mAddWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditWorkersActivity.this, AddWorkerActivity.class);
                startActivity(intent);
            }
        });

        loadData(this);
    }



    public void loadData (Context contex){
        mTable = new WorkersTable(contex);
        mTable.execute(contex);
    }



    @Override
    protected void onResume() {
        if (mTable.getCursor() != null) {
            loadData(this);
        }
        super.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }



    /**
     * Query the mDb and get all workers from the workers table
     *
     * @return Cursor containing the list of workers
     */
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



    @Override
    protected void onDestroy() {
        //mDb.close();
        super.onDestroy();
    }



    private boolean removeWorker(long id) {
        return mDb.delete(WorkersContract.WorkersEntry.TABLE_NAME, WorkersContract.WorkersEntry._ID + "=" + id, null) > 0;
    }



    public class WorkersTable extends AsyncTask<Context, Void,Void> {

        private Context mContext;
        private Cursor cursor = null;
        private Boolean mExecuting = null;
        private ProgressBar mLoadingIndicator;



        public Boolean getmExecuting() {
            return mExecuting;
        }



        public Cursor getCursor() {
            return cursor;
        }



        public WorkersTable(Context context) {
            mContext = context;
        }



        public Context getmContext() {
            return mContext;
        }



        @Override
        protected void onPreExecute() {
            ProgressBar mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
            mExecuting = true;
            if (cursor == null){
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
        }



        @Override
        protected Void doInBackground(Context... params) {

            // Create a DB helper (this will create the DB if run for the first time)
            WorkersDbHelper dbHelper = new WorkersDbHelper(mContext);

            // Keep a reference to the mDb until paused or killed
            mDb = dbHelper.getWritableDatabase();

            // Get all workers info from the database and save in a cursor
            mDb.beginTransaction();
            try {
                cursor = getAllWorkers();
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            mExecuting = false;
            ProgressBar mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Create an adapter for that cursor to display the data
            mAdapter = new WorkersAdapter(mContext, cursor);

            workersRecyclerView = (RecyclerView) EditWorkersActivity.this.findViewById(R.id.rv_edit_workers);

            // Set layout for the RecyclerView, because it's a list we are using the linear layout
            workersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            // Link the adapter to the RecyclerView
            workersRecyclerView.setAdapter(mAdapter);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {



                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }



                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    if (viewHolder.itemView.getTag() == null){
                        Toast.makeText(EditWorkersActivity.this, R.string.toast_failed,
                                Toast.LENGTH_LONG).show();
                    } else {
                        final long id = (long) viewHolder.itemView.getTag();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditWorkersActivity.this);
                        alertDialog.setTitle(R.string.dialog_delete_item_title).
                                setMessage(R.string.dialog_delete_item_text);

                        alertDialog.setPositiveButton(R.string.dialog_positive_button_label,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeWorker(id);
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
            }).attachToRecyclerView(workersRecyclerView);
        }
    }
}
