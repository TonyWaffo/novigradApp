package com.example.novigrad;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private String _id;
    private String _name;

    private List<String> _infos;
    private List<String> _documents;

    public Service() {
        // Default constructor with no arguments.
    }
    public Service(String id, String name,List<String> informations, List<String> documents){
        this._id=id;
        this._name=name;
        this._infos=informations;
        this._documents=documents;
    }

    public Service(String name,List<String> informations, List<String> documents){
        this._name=name;
        this._infos=informations;
        this._documents=documents;
    }

    //Getters implementation

    public String get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public List get_infos() {
        return _infos;
    }

    public List get_documents() {
        return _documents;
    }


    //Setters implementation

    public void set_id(String _id) {
        this._id = _id;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void add_infos(String text) {
        this._infos.add(text);
    }

    public void add_documents(String text) {
        this._documents.add(text);
    }
}
