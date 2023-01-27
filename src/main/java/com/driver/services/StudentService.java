package com.driver.services;

import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.models.Student;
import com.driver.repositories.CardRepository;
import com.driver.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {


    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;
    @Autowired
    private CardRepository cardRepository;

    public Student getDetailsByEmail(String email){
        Student student = studentRepository4.findByEmailId(email);
        return student;
    }

    public Student getDetailsById(int id){
        Student student = studentRepository4.findById(id).get();
        return student;
    }

    public void createStudent(Student student){

        Card newCard =  cardService4.createAndReturn(student);
        student.setCard(newCard);
        cardRepository.save(newCard);

    }

    public void updateStudent(Student student){

        studentRepository4.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
     Card card = studentRepository4.findById(id).get().getCard();
     studentRepository4.deleteCustom(id);
     card.setCardStatus(CardStatus.DEACTIVATED);
     cardRepository.save(card);
    }
}
