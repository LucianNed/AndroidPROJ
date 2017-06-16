package com.example.lucian.reversi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lucian on 14/06/17.
 */

public class LogFragment extends Fragment {

    protected TextView log;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.log_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        addToLog("LOG... \n");
    }

    public void addToLog(String texto) {

        log = (TextView) getView().findViewById(R.id.TxtLog);
        log.append(" " + texto + "\n");
    }

}
