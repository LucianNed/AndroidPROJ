package com.example.lucian.reversi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import static com.example.lucian.reversi.DetailRegActivity.EXTRA_TEXTO;

/**
 * Created by lucian on 12/06/17.
 */
public class AccessBDActivity extends AppCompatActivity implements QueryFragment.RegisterListener {

    private SQLiteDatabase db;
    private QueryFragment frgListado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_activity);

        frgListado = (QueryFragment) getFragmentManager()
                .findFragmentById(R.id.FrgListQuery);
        frgListado.setRegisterListener(this);
    }

    @Override
    public void onRegisterSelected(String alias, String date) {
        RegFragment fgdet = (RegFragment) getFragmentManager().findFragmentById(R.id.FrgDetalle);
        boolean hayDetalle = (fgdet != null && fgdet.isInLayout());
        String reg = getRegister(alias, date);
        Toast.makeText(this, "fdget: " + hayDetalle, Toast.LENGTH_SHORT).show();
        if (hayDetalle) {
            fgdet.setDetails(reg);
        } else {
            Intent i = new Intent(this, DetailRegActivity.class);
            i.putExtra(EXTRA_TEXTO, reg);
            startActivity(i);
        }
    }

    //go back
    public void goBack(View src) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public String getRegister(String a, String d) {
        db = frgListado.getDb();

        Cursor c2 = db.rawQuery("SELECT * FROM Log WHERE alias ='" + a + "'AND date='" + d + "'", null);

        String details = "Detalles de la partida " + "\n";
        if (c2.moveToFirst()) {
            String alias = c2.getString(1);
            String date = c2.getString(3);
            int size = c2.getInt(2);
            boolean timer = c2.getInt(4) > 0;
            int black_p = c2.getInt(5);
            int white_p = c2.getInt(6);
            int used = c2.getInt(7);
            String result = c2.getString(8);

            details = details + " Alias: " + alias + "\n";
            details = details + " Date: " + date + "\n";
            details = details + " Size: " + size + "\n";
            details = details + " Timer: " + timer + "\n";
            details = details + " Player: " + black_p + "\n";
            details = details + " NPC: " + white_p + "\n";
            details = details + " Time: " + used + "\n";
            details = details + " Result: " + result + "\n";
        }
        if (!c2.isClosed())
            c2.close();
        return details;
    }


}