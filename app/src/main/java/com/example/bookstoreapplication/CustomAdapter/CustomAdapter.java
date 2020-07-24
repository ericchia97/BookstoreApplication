package com.example.bookstoreapplication.CustomAdapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookstoreapplication.EditBookDetails;
import com.example.bookstoreapplication.Model.BookID;
import com.example.bookstoreapplication.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<BookDetails> {
    Context c;
    ArrayList<BookID> books;
    View view;

    public CustomAdapter(Context c, ArrayList<BookID> books) {
        this.c = c;
        this.books = books;
    }

    @NonNull
    @Override
    public BookDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(c).inflate(R.layout.book_list_details, parent, false);

        return new BookDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookDetails holder, int position) {

        BookID bookID = books.get(position);
        final int ID_no = bookID.getBookId();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, EditBookDetails.class);
                intent.putExtra("ID_no", ID_no);
                c.startActivity(intent);
            }
        });
        holder.Book_title.setText(bookID.getBookName());
        holder.AuthorList.setText(bookID.getAuthorName());
        holder.itemView.setTag(ID_no);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
