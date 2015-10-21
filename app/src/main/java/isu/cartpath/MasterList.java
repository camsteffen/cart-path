package isu.cartpath;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MasterList extends AppCompatActivity {

    private ItemListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list);

        CartPath app = (CartPath) getApplication();

        final ListView list = (ListView) findViewById(R.id.list_view);
        listAdapter = new ItemListAdapter(this, app);
        list.setAdapter(listAdapter);

        final EditText editText = (EditText) findViewById(R.id.newItem);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    addItem();
                    handled = true;
                }
                return handled;
            }
        });

        registerForContextMenu(list);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        listAdapter.resetCursor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_master_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteItem(info.id);
        }
        return true;
    }

    private void deleteItem(long id) {
        CartPath app = (CartPath) getApplication();
        app.dbHelper.deleteItem(id);
        app.db.delete(ListReaderContract.ItemEntry.TABLE_NAME, ListReaderContract.ItemEntry._ID + "=" + Long.toString(id), null);
        listAdapter.resetCursor();
    }

    private void addItem() {
        final EditText editText = (EditText) findViewById(R.id.newItem);
        String name = editText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ListReaderContract.ItemEntry.COLUMN_NAME_NAME, name);
        values.put(ListReaderContract.ItemEntry.COLUMN_NAME_IN_CART, 0);
        CartPath app = (CartPath) getApplication();
        app.db.insert(ListReaderContract.ItemEntry.TABLE_NAME, null, values);
        listAdapter.resetCursor();
        editText.setText("");
    }

    public void addButton(View view) {
        addItem();
    }

    public void clearChecked(View view) {
        CartPath app = (CartPath) getApplication();
        app.db.delete(ListReaderContract.ItemEntry.TABLE_NAME, ListReaderContract.ItemEntry.COLUMN_NAME_IN_CART + "=1", null);
        listAdapter.resetCursor();
    }

    public void checkItem(View view) {
        CartPath app = (CartPath) getApplication();
        boolean checked = ((CheckBox)view).isChecked();
        ListView list = (ListView) findViewById(R.id.list_view);
        int position = list.getPositionForView(view);
        long id = list.getAdapter().getItemId(position);
        SQLiteDatabase db = app.dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " +
                ListReaderContract.ItemEntry.TABLE_NAME +
                " SET " + ListReaderContract.ItemEntry.COLUMN_NAME_IN_CART + "=" + (checked ? "1" : "0") +
                " WHERE " + ListReaderContract.ItemEntry._ID + "=" + Long.toString(id));
        listAdapter.resetCursor();
    }

    public void openItem(View view) {
        ListView list = (ListView) findViewById(R.id.list_view);
        int position = list.getPositionForView(view);
        long id = list.getAdapter().getItemId(position);
        Intent intent = new Intent(this, EditItem.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
