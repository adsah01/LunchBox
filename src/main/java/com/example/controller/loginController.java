package com.example.controller;

import com.example.domain.LunchBox;
import com.example.domain.Person;
import com.example.domain.User;
import com.example.repository.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;


@Controller
public class loginController {
    ArrayList<User> users;
    ArrayList<Person> persons;
    ArrayList<LunchBox> lunchBoxes;

    @Autowired
    Repository repository;

    @PostConstruct
    public void RefreshUsers() {
        users = (ArrayList<User>) repository.getUsers();
    }
    @PostConstruct
    public void RefreshPersons() {
        persons = (ArrayList<Person>) repository.getPersons();
    }
    @PostConstruct
    public void RefreshLunchBoxes() {
        lunchBoxes = (ArrayList<LunchBox>) repository.getLunchBoxes();
    }

    @PostMapping("/login")
    public ModelAndView getUserLogin(@RequestParam String userName, HttpSession session, @RequestParam String password) throws Exception {
        for (User index : users) {
            if((userName.equals(index.getUserName()) && (password.equals(index.getPassword())))) {
                return new ModelAndView("Adam");

            }

        }
        System.out.println("Fel login");
        return null;
    }

    @GetMapping("/newUser")
    public ModelAndView form() {

        User user = new User("", "", "");
        Person person = new Person("", "", "");
        ModelAndView mv = new ModelAndView("signUp");
        mv.addObject("user",user);
        mv.addObject("person",person);

        return mv;
}

    @PostMapping("/newUser")
    public ModelAndView newUser(@ModelAttribute User user ,@ModelAttribute Person person) throws Exception {
        System.out.println(person.getFirstName());

        int key = Integer.parseInt(repository.addUser(user, person));
        users.add(new User(key, user.getUserName(), user.getPassword(), user.getMail()));
        persons.add(new Person(key, person.getFirstName(), person.getLastName(), person.getPhoneNumber()));


        return new ModelAndView("Adam");
    }


}
