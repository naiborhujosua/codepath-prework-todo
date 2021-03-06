package com.aishablake.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    ArrayList<String> toDoItems;
    ArrayAdapter<String> toDoAdapter;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(toDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                toDoItems.remove(i);
                toDoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("item_text", toDoItems.get(position));
                intent.putExtra("item_index", position);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String updatedText = data.getExtras().getString("updated_text");
            int updateIndex = data.getExtras().getInt("update_index");
            toDoItems.set(updateIndex, updatedText);
            toDoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    public void populateArrayItems() {
        readItems();
        toDoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toDoItems);
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.txt");
        try {
            toDoItems = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e) {
            toDoItems = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(file, toDoItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View view) {
        toDoAdapter.add(etEditText.getText().toString());
        etEditText.setText("");
        writeItems();
    }
}
