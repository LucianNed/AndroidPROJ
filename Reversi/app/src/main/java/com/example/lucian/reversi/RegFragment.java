package com.example.lucian.reversi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lucian on 15/06/17.
 */

public class RegFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.detail_reg_fragment, container, false);
    }

    public void setDetails(String texto) {
        TextView txtDetalle = (TextView) getView().findViewById(R.id.TxtRegDetail);
        txtDetalle.setText(texto);
    }
}
