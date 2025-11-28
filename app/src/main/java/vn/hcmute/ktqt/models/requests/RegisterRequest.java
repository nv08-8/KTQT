//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models.requests;

public class RegisterRequest {
    public String name;
    public String email;
    public String password;
    public String phone;

    public RegisterRequest(String name, String password, String email, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}

