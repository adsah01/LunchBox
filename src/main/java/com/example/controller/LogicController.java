package com.example.controller;

import com.example.domain.LunchBox;
import com.example.domain.Person;
import com.example.domain.User;
import com.example.repository.Repository;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.org.xml.sax.SAXException;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class LogicController {
    ArrayList<User> users;
    ArrayList<Person> persons;
    ArrayList<LunchBox> lunchBoxes;
    String lunchBoxesJson;

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
    public void RefresshLunchBoxes() {
        lunchBoxes = (ArrayList<LunchBox>) repository.getLunchBoxes();
        lunchBoxesJson = objectToJSON(lunchBoxes);

    }


    @PostMapping("/login")
    public ModelAndView getUserLogin(@RequestParam String userName, HttpSession session, @RequestParam String password, User user, Person person) throws Exception {
        for (User index : users) {
            if((userName.equals(index.getUserName()) && (password.equals(index.getPassword())))) {
                session.setAttribute("user", index);
                session.setAttribute("person", persons.get(index.getUserID()) );

                return new ModelAndView("Adam").addObject("session", session);

            }

        }
        String error = "Wrong username or password";
        boolean showLogin = true;
        return new ModelAndView("index")
                .addObject("showLogin", showLogin)
                .addObject("error", error)
                .addObject("user", user)
                .addObject("person", person)
                .addObject("lunchBoxes", lunchBoxesJson);
    }

    @GetMapping("/")
    public ModelAndView form() {

        User user = new User("", "", "");
        Person person = new Person("", "", "");
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("user",user);
        mv.addObject("person",person);
        mv.addObject("lunchBoxes", lunchBoxesJson);

        return mv;
}

    @PostMapping("/")
    public ModelAndView newUser(@Valid User user, BindingResult bru, @Valid Person person, BindingResult brp, RedirectAttributes attr) throws Exception {

        if (bru.hasErrors() || brp.hasErrors() ||   userNameDuplicate(user)) {
            attr.addFlashAttribute("errors", bru);
            boolean showNewUser = true;
            String error = bru.getFieldError().getField() + " " + bru.getFieldError().getDefaultMessage();

            return new ModelAndView("index")
                    .addObject("showNewUser", showNewUser)
                    .addObject("error", error)
                    .addObject("lunchBoxes", lunchBoxesJson);

        }

        int key = Integer.parseInt(repository.addUser(user, person));
        users.add(new User(key, user.getUserName(), user.getPassword(), user.getMail()));
        persons.add(new Person(key, person.getFirstName(), person.getLastName(), person.getPhoneNumber()));
        return new ModelAndView("Adam");
    }

    @GetMapping("/test")
    public ModelAndView foodApi() {


        ModelAndView mv = new ModelAndView("test");


        return mv;
    }

    @PostMapping("/test")
    public ModelAndView getApi(@RequestParam String ingredient) throws Exception{
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=10+Tulegatan,+Stockholm,+Sweden&key=AIzaSyBTZQRmcgBi0Fw0rNCsKoUBZohWk7UW0dw";
        String ingredientInfo = readUrl(url);
        System.out.println(ingredientInfo);
        boolean searchedForIngredient = true;

        return new ModelAndView("test")
                .addObject("ingridient", ingredientInfo)
                .addObject("searchedForIngredient", searchedForIngredient);

    }


    public boolean userNameDuplicate(User user) {
        boolean duplicate = false;

        for(User index : users) {
            if(index.getUserName().equals(user.getUserName())){
                duplicate = true;
                return duplicate;
            }
        }return duplicate;
    }


    public String objectToJSON(ArrayList<LunchBox> array) {
        ObjectMapper mapper  = new ObjectMapper();
        String jsonInString = "[";
            for(int i = 0; i<array.size(); i++) {
                try {
                    jsonInString += mapper.writeValueAsString(array.get(i));
                    if(i<array.size()-1) {
                        jsonInString += ",";
                    }

                }
                catch(JsonGenerationException e) {
                    e.printStackTrace();
                }
                catch(JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            jsonInString += "]";
        return jsonInString;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


}
