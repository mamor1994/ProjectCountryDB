package com.example.projectandroid3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Countries extends AppCompatActivity {

    EditText nameText, nameText1, capitalText, populationText;
    SQLiteDatabase database;
    String createTableSQL = "CREATE TABLE if not exists Country (\n" +
            "    Name    TEXT    NOT NULL,\n" +
            "    Capital TEXT    NOT NULL,\n" +
            "    Population INTEGER,\n" +
            "    Country_ID INTEGER PRIMARY KEY AUTOINCREMENT\n" +
            ");";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);
        nameText = findViewById(R.id.editTextName);
        nameText1 = findViewById(R.id.editTextName1);
        capitalText = findViewById(R.id.editTextCapital);
        populationText = findViewById(R.id.editTextPopulation);
        database = openOrCreateDatabase("CountryDB.db", MODE_PRIVATE, null);
        database.execSQL(createTableSQL);

        Button btnDelete = findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        Button btnUpdate = findViewById(R.id.buttonUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        Button btnSearch = findViewById(R.id.buttonSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    public void add(View view){
        String countryName = nameText.getText().toString();
        String countryCapital = capitalText.getText().toString();
        String countryPopulation = populationText.getText().toString();

        boolean fields = !countryName.isEmpty() && !countryCapital.isEmpty() && !countryPopulation.isEmpty();
        if (fields) {
            String insertSQL = "Insert into Country(Name,Capital,Population) " +
                    "values(?,?,?)";
            String[] parameters = new String[3];
            parameters[0] = nameText.getText().toString();
            parameters[1] = capitalText.getText().toString();
            parameters[2] = populationText.getText().toString();
            database.execSQL(insertSQL,parameters);
            showMessage("Info","Country added!");
            nameText.setText("");
            capitalText.setText("");
            populationText.setText("");
        } else if (fields){
            showMessage("Error", "Please enter a country name to add.");
        } else {
        showMessage("Error", "Please fill in all fields.");
        }
    }

    public void show(View view){
        String selectSQL = "Select * from Country";
        Cursor cursor = database.rawQuery(selectSQL,null);
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()){
            builder.append("Name: ").append(cursor.getString(0)).append("\n");
            builder.append("Capital: ").append(cursor.getString(1)).append("\n");
            builder.append("Population: ").append(cursor.getString(2)).append("\n");
            builder.append("-------------------------------------\n");
        }
        showMessage("Countries in DB",builder.toString());
    }

    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    public void delete() {
        String countryName = nameText.getText().toString();
        if (!countryName.isEmpty()) {
            String deleteSQL = "DELETE FROM Country WHERE Name = ?";
            String[] parameters = new String[]{countryName};
            database.execSQL(deleteSQL, parameters);
            showMessage("Info", "Country deleted!");
            nameText.setText("");
        } else {
            showMessage("Error", "Please enter a country name to delete.");
        }
    }

    public void update() {
        String countryName = nameText.getText().toString();
        String countryCapital = capitalText.getText().toString();
        String countryPopulation = populationText.getText().toString();

        boolean fields = !countryName.isEmpty() && !countryCapital.isEmpty() && !countryPopulation.isEmpty();
        if (fields) {
            ContentValues values = new ContentValues();
            values.put("Capital", countryCapital);
            values.put("Population", countryPopulation);

            String[] whereArgs = new String[]{countryName};
            int rowsAffected = database.update("Country", values, "Name=?", whereArgs);

            if (rowsAffected > 0) {
                showMessage("Info", "Country updated!");
            } else {
                showMessage("Error", "Failed to update country. Please check the input.");
            }
        } else {
            showMessage("Error", "Please fill in all fields.");
        }
    }

    public void search() {
        String searchKeyword = nameText1.getText().toString().trim();

        if (!searchKeyword.isEmpty()) {
            String selectSQL = "SELECT * FROM Country WHERE Name LIKE ?";
            String[] selectionArgs = new String[]{"%" + searchKeyword + "%"};

            Cursor cursor = database.rawQuery(selectSQL, selectionArgs);
            StringBuilder builder = new StringBuilder();

            while (cursor.moveToNext()) {
                builder.append("Name: ").append(cursor.getString(0)).append("\n");
                builder.append("Capital: ").append(cursor.getString(1)).append("\n");
                builder.append("Population: ").append(cursor.getString(2)).append("\n");
                builder.append("-------------------------------------\n");
                nameText1.setText("");
            }

            if (builder.length() > 0) {
                showMessage("Search Results", builder.toString());
            } else {
                showMessage("Search Results", "No matching countries found.");
                nameText1.setText("");
            }
        } else {
            showMessage("Error", "Please enter a search keyword.");
        }
    }
    
}