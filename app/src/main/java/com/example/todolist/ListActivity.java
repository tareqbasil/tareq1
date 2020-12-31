package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private List<Item> itemlist;
    private RecyclerViewAdapter recyclerviewadapter;
    private RecyclerView recyclerview;
    private AlertDialog alertdialog;
    private EditText edittext;
    private Button submit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        recyclerview = findViewById(R.id.recyclerView);
        Button createList = findViewById(R.id.btn_add);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        itemlist = new ArrayList<>();
        recyclerviewadapter = new RecyclerViewAdapter(this, itemlist);
        recyclerview.setAdapter(recyclerviewadapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View s) {
                popUpDialog();
            }
        });
    }

    private void popUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.list_name, null);
        edittext = view.findViewById(R.id.edit_text);
        submit = view.findViewById(R.id.submit_list);
        builder.setView(view);
        alertdialog = builder.create();
        alertdialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View s) {

                if (!edittext.getText().toString().isEmpty()){
                    Item item = new Item();
                    item.setMyListName(edittext.getText().toString());
                    itemlist.add(item);

                    recyclerviewadapter.notifyDataSetChanged();
                    alertdialog.dismiss();

                }
                else{
                    Snackbar.make(s, "Pleas Enter List name ...", Snackbar.LENGTH_SHORT);
                }



            }
        });
    }

    Item deletedItem = null;
    Item newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(itemlist, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedItem = itemlist.get(position);
                    itemlist.remove(deletedItem);
                    recyclerviewadapter.notifyItemRemoved(position);
                    String nameList = deletedItem.getMyListName();
                    Snackbar.make(recyclerview, nameList + " DELETED", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    itemlist.add(position, deletedItem);
                                    recyclerviewadapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;

                case ItemTouchHelper.RIGHT:
                    newItem = itemlist.get(position);

                    builder = new AlertDialog.Builder(ListActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.list_name, null);

                    TextView title = view.findViewById(R.id.typing_name);

                    edittext = view.findViewById(R.id.edit_text);
                    submit = view.findViewById(R.id.submit_list);

                    title.setText("Edit Item : ");
                    edittext.setText(newItem.getMyListName());
                    submit.setText("Update");

                    builder.setView(view);
                    alertdialog = builder.create();
                    alertdialog.show();


                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setMyListName(edittext.getText().toString());

                            if (!edittext.getText().toString().isEmpty()){

                                itemlist.remove(newItem);
                                recyclerviewadapter.notifyItemRemoved(position);

                                itemlist.add(position, newItem);
                                recyclerviewadapter.notifyItemInserted(position);
                                alertdialog.dismiss();

                                Snackbar.make(view," done",Snackbar.LENGTH_SHORT).show();
                            }else {
                                Snackbar.make(view,"field",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };
}