package com.example.rv720.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase kelimeDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().hide();
        createoropendatabase();
    }

    public void save (View view){
    Intent myintent = new Intent(this,kaydet.class);
    startActivity(myintent);
}

    public void list (View view){
        Intent myintent = new Intent(this,liste.class);
        startActivity(myintent);
    }

    public void vocabularycard(View view){

        Intent myintent = new Intent(this,kelimekart.class);
        startActivity(myintent);
    }


    public void createoropendatabase() {
        Cursor mycursor;

        kelimeDB = this.openOrCreateDatabase("Vocabulary", MODE_PRIVATE, null);
        kelimeDB.execSQL("CREATE TABLE IF NOT EXISTS vocabulary " + "(id integer primary key, vocabulary VARCHAR, pronunciation VARCHAR, meaning VARCHAR, memorycode VARCHAR, ismemorized integer default 0, tarih DATE default CURRENT_DATE);");

        File database = getApplicationContext().getDatabasePath("Vocabulary.db");
        // Check if the database exists
        try {

            if (database.exists()) {
                Toast.makeText(this, "Database Exist", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.e("CONTACTS ERROR", "Error Creating Database");
        }
    }

}
