package com.jordanjudt.databaseproject;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase database;
    String databaseName = "", tableName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.createDatabase).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createDatabase(((EditText) findViewById(R.id.query)).getText().toString());
            }
        });

        findViewById(R.id.createTable).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createTable(((EditText) findViewById(R.id.query)).getText().toString());
            }
        });

        findViewById(R.id.executeQuery).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                executeQuery();
            }
        });
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    private void createDatabase(String name) {
        if (name.equals("") || name.matches("\\w"))
            Toast.makeText(this, "Please enter a valid database name", Toast.LENGTH_SHORT).show();
        else {
            try {
                if (!name.endsWith(".db"))
                    name += ".db";
                databaseName = name;
                database = openOrCreateDatabase(name, Activity.MODE_PRIVATE, null);
                Toast.makeText(this, "Created database \"" + name + "\"!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Something's wrong :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createTable(String name) {
        if (database == null)
            Toast.makeText(this, "Please create a database first", Toast.LENGTH_SHORT).show();
        else if (name.equals("") || name.matches("\\w"))
            Toast.makeText(this, "Please enter a valid table name", Toast.LENGTH_SHORT).show();
        else {
            try {
                tableName = name;
                database.execSQL("CREATE TABLE IF NOT EXISTS " + name + "(" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "age INTEGER," +
                        "phone TEXT);");

                database.execSQL("INSERT INTO " + name + "(name, age, phone) VALUES ('Jordan', 10, '111-222-3333')");
                database.execSQL("INSERT INTO " + name + "(name, age, phone) VALUES ('Sam', 20, '444-555-6666')");
                database.execSQL("INSERT INTO " + name + "(name, age, phone) VALUES ('Alex', 30, '777-888-9999')");

                Toast.makeText(this, "Created table \"" + name + "\" with 3 entries", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Something's wrong :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void executeQuery() {
        if (tableName.equals("") || tableName.matches("\\w"))
            Toast.makeText(this, "Please create a table first", Toast.LENGTH_SHORT).show();
        else {
            try {
                Cursor cursor = database.rawQuery("SELECT name, age, phone FROM " + tableName, new String[]{});

                int count = cursor.getCount();
                String output = String.format("**Results: %d**\n", count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    output += String.format("Record #%d\n", i);
                    output += String.format("Name: %s\n", cursor.getString(0));
                    output += String.format("Age: %d\n", cursor.getInt(1));
                    output += String.format("Phone: %s\n\n", cursor.getString(2));
                }
                cursor.close();

                ((TextView) findViewById(R.id.queryView)).setText(output);
                Toast.makeText(this, "Here you go!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Something's wrong :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
