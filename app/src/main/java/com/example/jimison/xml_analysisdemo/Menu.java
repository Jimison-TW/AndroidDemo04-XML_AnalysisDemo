package com.example.jimison.xml_analysisdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void airClick(View view){
        Intent it = new Intent(this,MainActivity.class);
        startActivity(it);
    }

    public void busClick(View view){
        Intent it = new Intent(this,BusActivity.class);
        startActivity(it);
    }
}
