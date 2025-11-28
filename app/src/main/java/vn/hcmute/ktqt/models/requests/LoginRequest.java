//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models.requests;

public class LoginRequest {
    public String username;
    public String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

