package Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Folder implements Item{

    private final StringProperty fname;

    @Getter
    private final List<Item> children = new ArrayList<>();


    public Folder(String name){
        this.fname = new SimpleStringProperty(name);
    }

    public Folder(String name, List<Item> children){
        this.fname = new SimpleStringProperty(name);
        this.children.addAll(children);
    }

    public void addChildren(List<Item> children){
        this.children.addAll(children);
    }
    public void addChildren(Item child){
        children.add(child);
    }

    @Override
    public String getFname() {
        return fname.get();
    }

    @Override
    public String getFid() {
        return null;
    }

    @Override
    public String getDadded() {
        return null;
    }

    @Override
    public String getFowner() {return null;}

    @Override
    public String getFsize() {
        return null;
    }
}
