package com.example.sid2019.APP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.sid2019.APP.Database.DataBaseConfig;
import com.example.sid2019.R;

public class DatePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
    }

    public void confirmChoice(View v){
        DatePicker datePicker = findViewById(R.id.datePicker);
        int[] yearMonthDay= new int[3];
        int global = getIntent().getIntExtra("global",0);
        yearMonthDay[0] = datePicker.getYear();
        yearMonthDay[1]= datePicker.getMonth()+1;
        yearMonthDay[2] = datePicker.getDayOfMonth();
        if(global==1){
        Intent intent = new Intent(this, AlertasGlobaisActivity.class);
        intent.putExtra("date",yearMonthDay);
        startActivity(intent);
        finish();}
        else{
            Intent intent = new Intent(this, AlertasCulturaActivity.class);
            intent.putExtra("date",yearMonthDay);
            startActivity(intent);
            finish();
        }

    }
}
