package com.example.bookstoreapplication.Model;

import androidx.core.content.PermissionChecker;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BookID extends RealmObject {


    @PrimaryKey
    private int BookId;

    private String BookCover;
    @Required
    private String BookName;
    @Required
    private String AuthorName;
    @Required
    private String Description;
    public BookID() {

    }

    public BookID(int bookId, String bookName, String authorName, String description)
    {
        this.BookId = bookId;
        this.BookName = bookName;
        this.AuthorName = authorName;
        this.Description = description;
    }

    public int getBookId() {
        return BookId;
    }

    public void setBookId(int bookId) {
        BookId = bookId;
    }

    public String getBookCover() {
        return BookCover;
    }

    public void setBookCover(String bookCover) {
        BookCover = bookCover;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}