//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models.requests;

public class LoginRequest {
    // send 'email' key instead of 'username' to match backend
    public String email;
    public String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
