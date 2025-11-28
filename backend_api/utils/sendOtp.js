const sgMail = require("@sendgrid/mail");

sgMail.setApiKey(process.env.SENDGRID_API_KEY);

async function sendOtpEmail(toEmail, otp) {
  const msg = {
    to: toEmail,
    from: process.env.SENDGRID_FROM, // email gửi đi
    subject: "Mã xác thực OTP của bạn",
    html: `
      <div style="font-family: Arial; padding: 10px;">
        <h2>Mã OTP xác thực</h2>
        <p>Xin chào,</p>
        <p>Mã OTP của bạn là:</p>
        <h1 style="color: blue;">${otp}</h1>
        <p>Mã này có hiệu lực trong 5 phút.</p>
      </div>
    `
  };

  try {
    await sgMail.send(msg);
    console.log("Email OTP đã gửi thành công!");
    return true;
  } catch (error) {
    console.error("Lỗi gửi OTP:", error);
    return false;
  }
}

module.exports = sendOtpEmail;
