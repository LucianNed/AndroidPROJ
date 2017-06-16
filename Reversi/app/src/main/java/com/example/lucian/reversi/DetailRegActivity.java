package com.example.lucian.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by lucian on 15/06/17.
 */

public class DetailRegActivity extends FragmentActivity {

    public static final String EXTRA_TEXTO = "log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_reg_activity);

        Intent i = getIntent();

        RegFragment frag = (RegFragment) getFragmentManager().findFragmentById(R.id.FrgDetalle);
        if (frag != null && frag.isInLayout()) {
            frag.setDetails(i.getStringExtra(EXTRA_TEXTO));
        }
    }

    //back
    public void clicBack(View src) {
        finish();
    }
}
