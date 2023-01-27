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
        if(student != null){
            Card newCard =  cardService4.createAndReturn(student);
            if(newCard != null){
                student.setCard(newCard);
                cardRepository.save(newCard);
            }else{
                // handle the case where newCard is null
            }
        }else{
            // handle the case where student is null
        }
    }

    public void updateStudent(Student student){

        studentRepository4.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
        Student student = studentRepository4.findById(id).get();
        if (student!=null) {
            Card card = student.getCard();
            studentRepository4.deleteCustom(id);
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.save(card);
        } else {
           
        }
    }
}
