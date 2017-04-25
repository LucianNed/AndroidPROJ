package com.example.lucian.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by lucian on 24/03/17.
 */

public class ConfigActivity extends AppCompatActivity {

    protected static final java.lang.String CFG_SELECTED_SIZE = "101";
    protected static final java.lang.String CFG_PLAYER_NAME = "010";
    protected static final java.lang.String CFG_TIMER = "000";
    protected String player_name;
    protected Boolean timer_on;
    protected Integer size;
    protected RadioGroup SizeGroup;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        SizeGroup = (RadioGroup) findViewById(R.id.radio_group);
        SizeGroup.setOnCheckedChangeListener(new RadioGroupInfo());

        if (savedInstanceState != null) {
            //load player name
            EditText e = (EditText) findViewById(R.id.config_alias_edit);
            e.setText(savedInstanceState.getString(CFG_PLAYER_NAME));
            //load timer
            CheckBox c = (CheckBox) findViewById(R.id.config_time_check);
            c.setChecked(savedInstanceState.getBoolean(CFG_TIMER));
            //load size
            int s = savedInstanceState.getInt(CFG_SELECTED_SIZE);
            for (int i = 0; i < SizeGroup.getChildCount(); i++) {
                RadioButton r = (RadioButton) SizeGroup.getChildAt(i);
                if (Integer.valueOf(r.getText().toString()) == s)
                    r.toggle();
            }
        }
        //initialize configuration
        this.size = getSize();
        this.timer_on = getTimer();
        this.player_name = getPlayer();
    }

    //start button
    public void clickStartGame(View src) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(CFG_SELECTED_SIZE, this.getSize());
        i.putExtra(CFG_TIMER, this.getTimer());
        i.putExtra(CFG_PLAYER_NAME, this.getPlayer());
        startActivity(i);
        finish();
    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putInt(CFG_SELECTED_SIZE, this.getSize());
        savedInstanceState.putString(CFG_PLAYER_NAME, this.getPlayer());
        savedInstanceState.putBoolean(CFG_TIMER, this.getTimer());
    }

    public Integer getSize() {
        Integer s = 0;                                       //initialized jsut in case
        int count = SizeGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton r = (RadioButton) SizeGroup.getChildAt(i);
            if (r.isChecked()) {
                s = Integer.valueOf(r.getText().toString());
            }
        }

        return s;
    }

    public Boolean getTimer() {
        Boolean b;
        CheckBox c = (CheckBox) findViewById(R.id.config_time_check);
        b = c.isChecked();

        return b;
    }

    public String getPlayer() {
        String p;
        EditText e = (EditText) findViewById(R.id.config_alias_edit);
        p = e.getText().toString();

        return p;
    }

    private class RadioGroupInfo implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton other = (RadioButton) group.getChildAt(i);
                if (other.getId() != checkedId) {
                    if (other.isChecked())
                        other.toggle();
                }
            }
        }

    }

}
