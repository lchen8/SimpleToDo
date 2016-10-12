package com.example.lily_chen.simpletodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    ArrayList<String> lists;
    ArrayAdapter<String> listsAdapter;
    ListView lvItems;
    Spinner titleSpinner; //spinner to allow user to switch between lists
    String currentList;
    int mostRecentIndex; //index of the most recently edited list, to be loaded on start-up

    private final int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<File> allLists = getAllLists();
        lists = getFileNames(allLists);
        mostRecentIndex = getMostRecent(allLists);

        lvItems = (ListView)findViewById(R.id.lvItems);
        currentList = lists.get(mostRecentIndex);
        readItems();
        itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        titleSpinner = (Spinner)findViewById(R.id.titleSpinner);
        listsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, lists);
        titleSpinner.setAdapter(listsAdapter);
        titleSpinner.setSelection(mostRecentIndex);
        setupSpinnerListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return true;
    }

    /*****************************/
    /* Launch Edit Item Activity */

    public void launchEditItem(int pos, String text) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("pos", pos);
        i.putExtra("text", text);
        startActivityForResult(i, REQUEST_CODE); // brings up the second activity
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String text = data.getExtras().getString("text");
            int pos = data.getExtras().getInt("pos");
            items.set(pos, text);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    /*********************************************/
    /* Methods called from Main Activity buttons */

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        items.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    public void onClearList(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Clear List");
        alert.setMessage("Are you sure you want to delete all items?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemsAdapter.clear();
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /******************************/
    /* Editing and deleting items */

    private void setupListViewListener() {
        // single click launches new activity to edit the item
        lvItems.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View item, final int pos, long id) {
                    //launchEditItem(pos, items.get(pos));
                    toggleStyle(item);
                }
            }
        );

        lvItems.setOnItemLongClickListener(
            new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View item, final int pos, long id) {
                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setView(promptsView);
                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editItemText);
                    userInput.setText(items.get(pos));

                    alert.setCancelable(false).setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                items.set(pos, userInput.getText().toString());
                                itemsAdapter.notifyDataSetChanged();
                                writeItems();
                            }
                        })
                        .setNegativeButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text
                                    items.remove(pos);
                                    itemsAdapter.notifyDataSetChanged();
                                    writeItems();
                                }
                            });
                    alert.show();
                    return true;
                }
            }
        );
    }

    /****************************************/
    /* Handles actions from Action Bar menu */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View settingsView = li.inflate(R.layout.settings, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setView(settingsView);
                alert.setTitle("Settings");

                final Spinner colorSpinner = (Spinner) settingsView.findViewById(R.id.colorSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.colors_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                colorSpinner.setAdapter(adapter);

                alert.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String colorChoice = colorSpinner.getSelectedItem().toString();
                        int barColor, bgColor;
                        switch (colorChoice) {
                            case "Forest":
                                barColor = R.color.forest1;
                                bgColor = R.color.forest2;
                                break;
                            case "Candy":
                                barColor = R.color.candy1;
                                bgColor = R.color.candy2;
                                break;
                            case "Galaxy":
                                barColor = R.color.galaxy1;
                                bgColor = R.color.galaxy2;
                                break;
                            default:
                                barColor = R.color.colorPrimary;
                                bgColor = R.color.white;
                                break;
                        }
                        setBackgroundColor(bgColor, barColor);
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;

            case R.id.action_create_list:
                LayoutInflater li2 = LayoutInflater.from(MainActivity.this);
                View promptsView = li2.inflate(R.layout.prompts, null);
                AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
                alert2.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editItemText);

                alert2.setCancelable(false).setPositiveButton("Create New List",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                        // create new (empty) list and set up the screen accordingly
                        String newListTitle = userInput.getText().toString();
                        createNewList(newListTitle);
                        listsAdapter.notifyDataSetChanged();
                        titleSpinner.setSelection(lists.size()-1);
                        currentList = newListTitle;
                        readItems();
                        itemsAdapter.notifyDataSetChanged();
                        }
                    })
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();
                        }
                    });
                alert2.show();
                return true;

            case R.id.action_delete_list:
                LayoutInflater li3 = LayoutInflater.from(MainActivity.this);
                View promptsView3 = li3.inflate(R.layout.prompts, null);
                AlertDialog.Builder alert3 = new AlertDialog.Builder(MainActivity.this);
                alert3.setMessage("Are you sure you want to delete this list?");

                alert3.setCancelable(false).setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                deleteList(titleSpinner.getSelectedItem().toString());
                                lists.remove(titleSpinner.getSelectedItemPosition());
                                listsAdapter.notifyDataSetChanged();
                                int newListIndex = getMostRecent(getAllLists());
                                titleSpinner.setSelection(newListIndex);
                                currentList = lists.get(newListIndex);
                                readItems();
                                itemsAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                    }
                                });
                alert3.show();
                return true;

            default:
                return true;
        }
    }

    /***************************************/
    /* Spinner for switching between lists */

    private void setupSpinnerListener() {
        titleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.i("onitemselected ", "did it happen");
                currentList = titleSpinner.getSelectedItem().toString();
                readItems();
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing
            }

        });
    }

    /*******************************/
    /* Helper and set-up functions */

    private void readItems() {
        File todoFile = new File(getListsDir(), currentList);
        try {
            items.clear();
            items.addAll(FileUtils.readLines(todoFile));
            Log.i("reading from " + currentList, items.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeItems() {
        File todoFile = new File(getListsDir(), currentList);
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Log.i("writing to " + currentList, FileUtils.readLines(todoFile).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Toggles the style of a view. None -> Bold -> Strike-through.
    // Currently broken, the style stays with the view when we switch between different lists
    private void toggleStyle(View view) {
        TextView item = (TextView) view;
        Log.i("st", item.getPaintFlags() + "");
        Log.i("st", Paint.STRIKE_THRU_TEXT_FLAG + "");
        if ((item.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) == 0 &&
            (item.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == 0 ) {
            item.setPaintFlags(item.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        }
        else if ((item.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) != 0 &&
                (item.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == 0 ) {
            item.setPaintFlags(item.getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG);
            item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            item.setPaintFlags(item.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void setBackgroundColor(int bgColor, int barColor) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundResource(bgColor);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getInteger(barColor)));
    }

    private void createNewList(String listName) {
        File todoFile = new File(getListsDir(), listName);
        try {
            todoFile.createNewFile();
            lists.add(listName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // I decided to store my lists inside of a separate direction to avoid
    // extraneous files, but it seems like there's still an automatically
    // generated 'data' file...
    private String getListsDir() {
        //check if directory exists, if not, make it
        File file = new File(getFilesDir().toString() + "/lists");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.toString();
    }

    // returns in the index of the most recently modified file.
    // the index is used to set the spinner correctly on start-up
    // or after deleting a file
    private int getMostRecent(ArrayList<File> files) {
        if (files == null || files.size() == 0) {
            return -1;
        }
        int lastModifiedIndex = 0;
        for (int i = 1; i < files.size(); i++) {
            Log.i("index", ""+i+", "+lastModifiedIndex);
            if (files.get(lastModifiedIndex).lastModified() < files.get(i).lastModified()) {
                lastModifiedIndex = i;
            }
        }
        return lastModifiedIndex;
    }

    // get all files that exists inside the list directory
    private ArrayList<File> getAllLists() {
        File filesDir = new File (getListsDir());
        File[] files = filesDir.listFiles();
        ArrayList<File> filesArrayList = new ArrayList<File>();
        if (files == null || files.length == 0) {
            filesArrayList.add(new File(filesDir, "My To-Do List"));
        } else {
            for (File f : files) {
                filesArrayList.add(f);
            }
        }
        Log.i("files?? ", filesArrayList.toString());
        return filesArrayList;
    }

    // return list of strings to use in titleSpinner
    private ArrayList<String> getFileNames(ArrayList<File> files) {
        ArrayList<String> names = new ArrayList<String>();
        for (File f : files) {
            names.add(f.getName());
        }
        return names;
    }

    private void deleteList(String listName) {
        File file = new File(getListsDir(), listName);
        file.delete();
    }
}
