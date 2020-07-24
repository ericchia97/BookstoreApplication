package com.example.bookstoreapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstoreapplication.Model.BookID;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

public class AddBook extends AppCompatActivity {

    Realm realm;
    Button BackButton;
    Button SubmitButton;
    EditText BookTitle;
    EditText Author;
    EditText Description_text;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book_details);
        realm = Realm.getDefaultInstance();
        initUI();
    }

    private void initUI(){
        BackButton = findViewById(R.id.Back_Button);
        SubmitButton = findViewById(R.id.Submit_Button);
        BookTitle = findViewById(R.id.BookName);
        Author = findViewById(R.id.AuthorName);
        Description_text = findViewById(R.id.Description);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddBook.this, Booklist.class);
                startActivity(i);
            }
        });
        SubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Simple checking
                String title = BookTitle.getText().toString();
                String book_author = Author.getText().toString();
                if(TextUtils.isEmpty(title)){
                    BookTitle.setError("This field cannot be empty.");
                    return;
                }
                else if(TextUtils.isEmpty(book_author)){
                    Author.setError("This field cannot be empty.");
                    return;
                }
                else {
                    addBook();
                }
                Intent i = new Intent(AddBook.this, Booklist.class);
                startActivity(i);
            }
        });
    }

    private void addBook(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Number ID_No = bgRealm.where(BookID.class).max("BookId");
                int Book_no = (ID_No == null) ? 1 : ID_No.intValue()+1;

                //String Cover_Test = Integer.toString(Book_no);


                BookID book = bgRealm.createObject(BookID.class, Book_no);
                //book.setBookCover(Cover_Test);

                    book.setBookName(BookTitle.getText().toString());
                    book.setAuthorName(Author.getText().toString());
                    book.setDescription(Description_text.getText().toString());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Toast.makeText(AddBook.this,"Add Book Succeed.", Toast.LENGTH_LONG).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Toast.makeText(AddBook.this,"Action Failed.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
