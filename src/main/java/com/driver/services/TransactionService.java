package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;


import javax.xml.crypto.Data;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available

        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id4
        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();

        if(book == null || !book.isAvailable()){
            throw  new Exception("Book is either unavailable or not present");
        }
        if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED)){
            throw  new Exception("Card is invalid");
        }
        if(card.getBooks().size() >= max_allowed_books ){
            throw  new Exception("Book limit has reached for this card");
        }
        Transaction transaction = new Transaction();
      transaction.setTransactionId(UUID.randomUUID().toString());
      transaction.setBook(book);
      transaction.setCard(card);
      transaction.setIssueOperation(true);
      transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);

     book.setAvailable(false);
     card.getBooks().add(book);

     cardRepository5.save(card);
     bookRepository5.save(book);

     //Note that the error message should match exactly in all cases

       return transaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);
        Card card = cardRepository5.findById(cardId).get();
        Book book = bookRepository5.findById(bookId).get();
        int fine = 0;
        Date presentDate = new Date();
        long time = presentDate.getTime() - transaction.getTransactionDate().getTime();
        long days = TimeUnit.MILLISECONDS.toDays(time);
        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        if(days > getMax_allowed_days){
            fine = (int)days * fine_per_day;
        }
        //make the book available for other users
        book.setAvailable(true);
        bookRepository5.save(book);
        //make a new transaction for return book which contains the fine amount as well
        Transaction returnBookTransaction  = new Transaction();
        returnBookTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        returnBookTransaction.setFineAmount(fine);
        returnBookTransaction.setTransactionId(UUID.randomUUID().toString());
        returnBookTransaction.setIssueOperation(true);
        returnBookTransaction.setCard(card);
        returnBookTransaction.setBook(book);

        transactionRepository5.save(returnBookTransaction);
        return returnBookTransaction; //return the transaction after updating all details
    }
}
