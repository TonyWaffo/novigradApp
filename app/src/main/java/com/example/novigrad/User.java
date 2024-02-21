// SEG 2505 - groupe 21
// Projet Service Novigrade


package com.example.novigrad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    /*
     * The following section represents the User class, which holds user information.
     */


    // Fields representing user information
    private String id;
    private String first_name;
    private String last_name;
    private String email_address;
    private String password;
    private String postal_code;
    private String role;
    private long phone_number;
    Map<String, Object> myServices;
    Map<String, Object> schedule;

    Map<String, Float> rating;


    //Default constructor which is helpful to be able to communicate with firebase
    public User() {
        myServices = new HashMap<>();
        //myServices = new ArrayList<Service>();
        schedule = new HashMap<>();

        rating = new HashMap<>();
    }

    public User(String id, String first_name, String last_name,
                String email_address, String postal_code, String password, String role, long phone_number) {

        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_address = email_address;
        this.postal_code = postal_code;
        this.password = password;
        this.phone_number = phone_number;
        this.role = role;
        this.myServices = new HashMap<>();
        this.schedule = new HashMap<>();
        this.rating = new HashMap<>();
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setServices(Map<String, Object> services) {
        this.myServices = services;
    }

    // Getter method for retrieving the id of the user
    public String getId() {
        return id;
    }

    // Getter method for retrieving the first name of the user
    public String getFirst_name() {
        return first_name;
    }


    // Retrieving the last name of the user
    public String getLast_name() {
        return last_name;
    }


    // Retrieving the email address
    public String getEmail_address() {
        return email_address;
    }


    // Retrieving the postal code
    public String getPostal_code() {
        return postal_code;
    }


    // Retrieving the password
    public String getPassword() {
        return password;
    }

    //Retrieving the role
    public String getRole() {
        return role;
    }


    // Retrieving the phone number
    public long getPhone_number() {
        return phone_number;
    }


    public Map<String, Object> getServices() {
        return myServices;
    }

    // Retrieving the schedule
    public Map<String, Object> getSchedule() {
        return schedule;
    }
    public Map<String, Float> getRating()
    {
        return this.rating;
    }


    public void addRate(Float num){
        this.rating.put(String.valueOf(num),num);
    }

    // Calculating the note given by the customer
    public double getRate()
    {
        double sum = 0.0;
        for(Float note : rating.values()) {
            sum += note;
        }
        return Math.round((sum/ rating.size()) * 100.0) / 100.0;
    }
}
