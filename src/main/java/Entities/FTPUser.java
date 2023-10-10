package Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FTPUser {
    private int id;
    private String userid;
    private String password;

    public FTPUser(int id, String userid, String password) {
        this.id = id;
        this.userid = userid;
        this.password = password;
    }

    @Override
    public String toString(){
        return String.format("FTPUser: { username: %s, password: %s, id: %s}", userid, password, id);
    }
}
