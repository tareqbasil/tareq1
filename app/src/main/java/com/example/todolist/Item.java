package com.example.todolist;

public class Item {
    private int id;
    private String MylistName;

    public Item(){

    }
    public Item(int id, String MylistName) {
        this.id = id;
        this.MylistName = MylistName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMyListName() {
        return MylistName;
    }

    public void setMyListName(String MYlistName) {
        this.MylistName = MylistName;
    }
}
