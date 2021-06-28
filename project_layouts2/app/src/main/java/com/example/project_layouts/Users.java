package com.example.project_layouts;

import java.util.ArrayList;

public class Users {
    String name,id;
    ArrayList<String> favorites ;

    public Users(){
        favorites =new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void addFaivorite(String fav){
        favorites.add(fav);

    }
    public String printGeneres()
    {
        String ganeres ="";
        for (String genere : this.favorites){

            ganeres += genere+" ";
        }
        return ganeres;
    }
}
