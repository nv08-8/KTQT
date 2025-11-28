//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models.responses;

import vn.hcmute.ktqt.models.User;

public class AuthResponse {
    public String accessToken;
    public String refreshToken;
    public User profile;
}

