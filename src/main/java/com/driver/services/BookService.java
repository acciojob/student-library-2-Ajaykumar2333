package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository authorRepository;

    public void createBook(Book book){
//   int authorid = book.getAuthor().getId();
   Author author = book.getAuthor();
   book.setAuthor(author);
//
//   List<Book> bookWritten = author.getBooksWritten();
//   bookWritten.add(book);
//   author.setBooksWritten(bookWritten);

        author.getBooksWritten().add(book);

   authorRepository.save(author);


    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = new ArrayList<>();

        if(genre!=null && available && author == null){
            books = bookRepository2.findBooksByGenre(genre,available);
        }
        else  if(genre!=null && !available && author != null){
            books = bookRepository2.findBooksByGenreAuthor(genre,author,available);
        }
        else  if(genre==null && available && author != null){
            books = bookRepository2.findBooksByAuthor(author,available);
        }
        else  if(genre==null && available && author == null){
            books = bookRepository2.findByAvailability(available);
        }
        return books;


    }
}
