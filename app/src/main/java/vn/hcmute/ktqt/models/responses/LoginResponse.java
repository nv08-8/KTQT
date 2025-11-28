package vn.hcmute.ktqt.models.responses;

import vn.hcmute.ktqt.models.User;

public class LoginResponse {
    private boolean success;
    private String token;
    private User user;

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
