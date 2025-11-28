package vn.hcmute.ktqt.models.requests;

public class OtpRequest {
    public String userId;
    public String otp;

    public OtpRequest(String userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }
}

