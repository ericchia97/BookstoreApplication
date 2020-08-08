package com.example.bookstoreapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bookstoreapplication.Model.BookID;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

public class EditBookDetails extends AppCompatActivity {
    Realm realm;
    BookID bookID;
    ImageButton editBookCover;
    EditText editBookName;
    EditText editAuthor;
    EditText editDescription;
    Button UpdateButton;
    Button BackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_book_details);

        realm = Realm.getDefaultInstance();
        editBookCover = findViewById(R.id.BookCoverButton);
        editBookName = findViewById(R.id.Edit_BookName);
        editAuthor = findViewById(R.id.Edit_AuthorName);
        editDescription = findViewById(R.id.Edit_Description);
        UpdateButton = findViewById(R.id.Update_Button);
        BackButton = findViewById(R.id.Back_Button);

        Intent getIntent = getIntent();
        final int position = getIntent.getIntExtra("ID_no", 0);
        bookID = realm.where(BookID.class).equalTo("BookId", position).findFirst();

        byte[] bookCover = bookID.getBookCover();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bookCover, 0, bookCover.length);
        editBookCover.setImageBitmap(bitmap);
        editBookName.setText(bookID.getBookName());
        editAuthor.setText(bookID.getAuthorName());
        editDescription.setText(bookID.getDescription());

        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editBookName.getText().toString();
                String book_author = editAuthor.getText().toString();
                if(TextUtils.isEmpty(title)){
                    editBookName.setError("This field cannot be empty.");
                    return;
                }
                else if(TextUtils.isEmpty(book_author)){
                    editAuthor.setError("This field cannot be empty.");
                    return;
                }
                else {
                    Update_Details(position);
                }

                Intent i = new Intent(EditBookDetails.this, Booklist.class);
                startActivity(i);
            }
        });
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditBookDetails.this, Booklist.class);
                startActivity(i);
            }
        });
    }

    private void Update_Details(final int position){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bookID = bgRealm.where(BookID.class).equalTo("BookId", position).findFirst();
                bookID.setBookName(editBookName.getText().toString());
                bookID.setAuthorName(editAuthor.getText().toString());
                bookID.setDescription(editDescription.getText().toString());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Toast.makeText(EditBookDetails.this,"Update details Succeed.", Toast.LENGTH_LONG).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Toast.makeText(EditBookDetails.this,"Action Failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Delete_Books(int position){
        realm.beginTransaction();
        realm.where(BookID.class).equalTo("BookId", position).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }
}
