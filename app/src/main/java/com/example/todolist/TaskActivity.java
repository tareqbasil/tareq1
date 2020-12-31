package com.example.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private RecyclerViewAdapterTask recyclerViewAdapterTask;
    private RecyclerView recyclerViewTask;

    private EditText itemTaskName;
    private List<Task> tasklist;
    private Button btn_saveTask;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String itemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);

        TextView detsListName = findViewById(R.id.dets_listName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String listName = bundle.getString("ListName");
            itemId = bundle.getString("ItemId");

            detsListName.setText(listName + " List");
        }

        recyclerViewTask = findViewById(R.id.recyclerViewTask);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));

        tasklist = new ArrayList<>();

        recyclerViewAdapterTask = new RecyclerViewAdapterTask(this, tasklist);
        recyclerViewTask.setAdapter(recyclerViewAdapterTask);


        /*Data SnapShot*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tasklist.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Task task = snapshot.getValue(Task.class);
                            tasklist.add(task);
                        }
                        recyclerViewAdapterTask.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
        /*Data SnapShot*/


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTask);

        Button btn_createTask = findViewById(R.id.btn_createTask);
        btn_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createlistDialog();
            }
        });

        TextView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    private void createlistDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.list_name, null);
        itemTaskName = view.findViewById(R.id.edit_text);
        btn_saveTask = view.findViewById(R.id.submit_list);

        TextView title = view.findViewById(R.id.typing_name);
        title.setText("Enter name : ");
        itemTaskName.setHint(" name");

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemTaskName.getText().toString().isEmpty()) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();

                    Task task = new Task();
                    task.setTaskName(itemTaskName.getText().toString());
                    task.setIsChecked(false);
                    task.setTaskName(itemId);

                    String tasksId = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).push().getKey();
                    tasklist.add(task);
                    task.setTaskName(tasksId);
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").child(tasksId).setValue(task);
                    Snackbar.make(v, "Added Successfully *__^", Snackbar.LENGTH_SHORT).show();

                    recyclerViewAdapterTask.notifyDataSetChanged();
                    alertDialog.dismiss();
//                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void saveItem(View v) {
//
//        String newItemNOT = itemTaskName.getText().toString().trim();
//
//        Task task = new Task(newItemNOT, false);
//        taskList.add(0, task);
//        recyclerViewAdapterTask.notifyItemInserted(0);
//        recyclerViewTask.smoothScrollToPosition(0);
//
//        alertDialog.dismiss();
//    }

    public void onCheckboxClicked(View view) {
    }

    Task deletedItem = null;
    Task newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(tasklist, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedItem = tasklist.get(position);
                    tasklist.remove(deletedItem);
                    recyclerViewAdapterTask.notifyItemRemoved(position);
                    String nameTask = deletedItem.getTaskName();
                    Snackbar.make(recyclerViewTask, nameTask + " DELETED", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tasklist.add(position, deletedItem);
                                    recyclerViewAdapterTask.notifyItemInserted(position);
                                }
                            }).show();
                    break;

                case ItemTouchHelper.RIGHT:
                    newItem = tasklist.get(position);

                    builder = new AlertDialog.Builder(TaskActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.list_name, null);

                    TextView title = view.findViewById(R.id.typing_name);

                    itemTaskName = view.findViewById(R.id.edit_text);
                    btn_saveTask = view.findViewById(R.id.submit_list);

                    title.setText("Edit Item : ");
                    itemTaskName.setText(newItem.getTaskName());
                    btn_saveTask.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();


                    btn_saveTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setTaskName(itemTaskName.getText().toString());

                            if (!itemTaskName.getText().toString().isEmpty()) {
//                                recyclerViewTaskAdapter.notifyItemChanged(position,newItem);

                                tasklist.remove(newItem);
                                recyclerViewAdapterTask.notifyItemRemoved(position);

                                tasklist.add(position, newItem);
                                recyclerViewAdapterTask.notifyItemInserted(position);
                                alertDialog.dismiss();

                                Snackbar.make(view, " done", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, " Empty", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };
}

