package com.example.bookstoreapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookstoreapplication.CustomAdapter.CustomAdapter;
import com.example.bookstoreapplication.Helper.MyHelper;
import com.example.bookstoreapplication.Helper.SwipeButton;
import com.example.bookstoreapplication.Helper.deleteOnClickListener;
import com.example.bookstoreapplication.Model.BookID;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class Booklist extends AppCompatActivity {

    Realm realm;
    FloatingActionButton AddBookButton;
    Button LogoutButton;
    RecyclerView Booklist_view;

    EditBookDetails editBooks;
    MyHelper helper;
    RealmChangeListener realmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        realm = Realm.getDefaultInstance();


        init();
    }

    private void init(){

        AddBookButton = findViewById(R.id.AddFloatingButton);
        LogoutButton = findViewById(R.id.Logout_Button);
        Booklist_view = findViewById(R.id.Booklist_View);

        helper = new MyHelper(realm);
        helper.selectFromRealm();

        final CustomAdapter adapter = new CustomAdapter(this, helper.Refresh());
        Booklist_view.setLayoutManager(new LinearLayoutManager(this));
        Booklist_view.setAdapter(adapter);

        SwipeButton swipeButton = new SwipeButton(this, Booklist_view, 200) {
            @Override
            public void instantiateDeleteButton(final RecyclerView.ViewHolder viewHolder, List<SwipeButton.DeleteButton> buffer) {
                buffer.add(new DeleteButton(Booklist.this,
                        "Delete",
                        R.drawable.ic_baseline_delete_24,
                        30,
                        Color.parseColor("#FF3C30"),
                        new deleteOnClickListener() {
                            @Override
                            public void onClick(int pos) {
                                realm.beginTransaction();
                                realm.where(BookID.class).equalTo("BookId", (int)viewHolder.itemView.getTag()).findFirst().deleteFromRealm();
                                realm.commitTransaction();
                                Toast.makeText(Booklist.this,"Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }));

            }
        };
        List_Refresh();

        AddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Booklist.this, AddBook.class);
                startActivity(i);
            }
        });

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void List_Refresh(){
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                CustomAdapter adapter = new CustomAdapter(Booklist.this, helper.Refresh());
                Booklist_view.setAdapter(adapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }


}
