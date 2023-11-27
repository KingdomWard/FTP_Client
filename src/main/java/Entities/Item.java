package Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface Item {
    StringProperty fname = new SimpleStringProperty("");
    StringProperty fsize = new SimpleStringProperty("");
    StringProperty dadded = new SimpleStringProperty("");
    StringProperty fowner = new SimpleStringProperty("");
    StringProperty fid = new SimpleStringProperty("");

    String getFname();
    String getFsize();
    String getDadded();
    String getFowner();
    String getFid();

}
