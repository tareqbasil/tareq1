package com.example.todolist;

public class Item {
    private String id;
    private String MylistName;

    public Item(){

    }
    public Item(String id, String MylistName) {
        this.id = id;
        this.MylistName = MylistName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMyListName() {
        return MylistName;
    }

    public void setMyListName(String MYlistName) {
        this.MylistName = MylistName;
    }
}
