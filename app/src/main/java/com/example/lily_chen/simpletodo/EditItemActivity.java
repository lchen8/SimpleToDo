package com.example.lily_chen.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by lily_chen on 10/12/16.
 */
public class EditItemActivity extends AppCompatActivity {
    String text;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        pos = getIntent().getIntExtra("pos", 0);
        text = getIntent().getStringExtra("text");
        EditText etText = (EditText) findViewById(R.id.editText);
        etText.setText(text);
    }

    public void onSave(View v){
        EditText etText = (EditText) findViewById(R.id.editText);
        Intent data = new Intent();
        data.putExtra("text", etText.getText().toString());
        data.putExtra("pos", pos);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
