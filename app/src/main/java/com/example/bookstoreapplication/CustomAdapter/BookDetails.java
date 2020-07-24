package com.example.bookstoreapplication.CustomAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookstoreapplication.R;

public class BookDetails extends RecyclerView.ViewHolder {
    ImageView Cover_photo;
    TextView Book_title;
    TextView AuthorList;

    public BookDetails(@NonNull View itemView) {
        super(itemView);

        Book_title = itemView.findViewById(R.id.list_BookTitle);
        AuthorList = itemView.findViewById(R.id.list_Author);
    }
}
