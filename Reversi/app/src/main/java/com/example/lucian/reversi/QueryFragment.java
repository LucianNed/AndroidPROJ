package com.example.lucian.reversi;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.lucian.reversi.ReversiLogic.Bl;
import static com.example.lucian.reversi.ReversiLogic.Em;
import static com.example.lucian.reversi.ReversiLogic.Ti;
import static com.example.lucian.reversi.ReversiLogic.Vi;

/**
 * Created by lucian on 15/06/17.
 */

public class QueryFragment extends ListFragment {

    private static final String QUERY = "q";
    protected SQLiteDatabase db;
    private RegisterListener lstnr;
    private Cursor c;
    private Cursor new_c;
    private TodoCursorAdapter tca;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomSQLHelper usdbh =
                new CustomSQLHelper(getActivity(), "DBReversi", null, 2);
        db = usdbh.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.query_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        c = db.rawQuery("SELECT _id,alias, date, result FROM Log", null);

        int count = c.getCount();

        if (count == 0) {
            //Insertamos 1 juego de ejemplo si la tabla esta vacia
            //Generamos los datos

            String alias = "Dummy";
            SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
            String date = dt.format(new Date());
            String result = Vi;

            //Insertamos los datos en la tabla Usuarios
            db.execSQL("INSERT INTO Log (alias, date, result) " +
                    "VALUES ('" + alias + "', '" + date + "', '" + result + "')");
        }
        tca = new TodoCursorAdapter(getActivity(), c);

        getListView().setAdapter(tca);

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, int pos, long id) {

                if (lstnr != null) {
                    Cursor c = ((TodoCursorAdapter) list.getAdapter()).getCursor();
                    c.moveToPosition(pos);
                    String a = c.getString(c.getColumnIndexOrThrow("alias"));
                    String d = c.getString(c.getColumnIndexOrThrow("date"));
                    lstnr.onRegisterSelected(a, d);
                }
            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.get_all) {
            getAllRegistersFor(item);
        } else if (id == R.id.send_mail) {
            sendRegMail(item);
        } else if (id == R.id.delete_this) {
            removeItemFromList(item);
        }
        return super.onContextItemSelected(item);
    }

    //send the field info as mail
    public void sendRegMail(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemPosition = info.position;
        c.moveToPosition(itemPosition);
        String body = c.getString(1) + "\n" + c.getString(2) + "\n" + c.getString(3);

        //send mail
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/html");
        i.setData(Uri.parse("mailto:" + getActivity().getResources().getString(R.string.results_fakemail)));
        i.putExtra(Intent.EXTRA_SUBJECT, "PracticaAndroid1");
        i.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(i);
    }

    //delete field
    public void removeItemFromList(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemPosition = info.position;

        c.moveToPosition(itemPosition);
        String[] args = {c.getString(1), c.getString(2)};
        db.delete("LOG", "alias=? AND date =?", args);
        new_c = db.rawQuery("SELECT _id,alias, date, result FROM Log", null);
        tca.changeCursor(new_c);
        //new_c.close();
    }

    //get all fields
    public void getAllRegistersFor(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemPosition = info.position;

        c.moveToPosition(itemPosition);
        String[] alias = {c.getString(1)};
        new_c = db.rawQuery("SELECT _id,alias, date, result FROM LOG WHERE alias =?", alias);
        tca.changeCursor(new_c);
        //new_c.close();
    }

    //color the result
    public int findTextColor(String message) {
        if (message != null) {
            if (message.equals(Ti) || message.equals(Bl))
                return ContextCompat.getColor(getActivity(), R.color.colorAlert);
            else if (message.equals(Em))
                return ContextCompat.getColor(getActivity(), R.color.colorText);
            else if (message.equals(Vi))
                return ContextCompat.getColor(getActivity(), R.color.colorPLAYER);
            else
                return ContextCompat.getColor(getActivity(), R.color.colorNPC);
        }
        return ContextCompat.getColor(getActivity(), R.color.colorNPC);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setRegisterListener(RegisterListener listener) {

        lstnr = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public interface RegisterListener {
        void onRegisterSelected(String alias, String date);
    }

    public class TodoCursorAdapter extends CursorAdapter {
        public TodoCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.listitem_query, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            TextView listID = (TextView) view.findViewById(R.id.listID);
            TextView listResult = (TextView) view.findViewById(R.id.listResult);
            // Extract properties from cursor
            String id_t = cursor.getString(cursor.getColumnIndexOrThrow("alias")) + " "
                    + cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String res_t = cursor.getString(cursor.getColumnIndexOrThrow("result"));
            // Populate fields with extracted properties
            listID.setText(id_t);
            listResult.setText(res_t);
            listResult.setTextColor(findTextColor(res_t));
        }
    }

}
