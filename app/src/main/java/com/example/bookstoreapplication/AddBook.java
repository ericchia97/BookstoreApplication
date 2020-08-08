package com.example.bookstoreapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.bookstoreapplication.Model.BookID;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

public class AddBook extends AppCompatActivity {

    ImageButton CoverButton;
    Realm realm;
    Button BackButton;
    Button SubmitButton;
    EditText BookTitle;
    EditText Author;
    EditText Description_text;

    OutputStream outputStream;


    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

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
        CoverButton = findViewById(R.id.BookCoverButton);

        CoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    pickImageFromGallery();
                }
            }
            else{
                pickImageFromGallery();
            }
            }
        });

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
/*
                BitmapDrawable drawable = (BitmapDrawable) CoverButton.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/Demo/");
                dir.mkdir();

                File file = new File(dir, System.currentTimeMillis()+ ".jpg");
                try {
                    outputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(getApplicationContext(),"Image Saved", Toast.LENGTH_SHORT).show();
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

*/
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

    private byte[] imageViewToByte(ImageButton CoverButton) {
        Bitmap bitmap = ((BitmapDrawable)CoverButton.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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
                    book.setBookCover(imageViewToByte(CoverButton));
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

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else{
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri uri = data.getData();
            //CoverButton.setImageURI(data.getData());

            try{
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                CoverButton.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
