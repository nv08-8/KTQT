const sgMail = require("@sendgrid/mail");

sgMail.setApiKey(process.env.SENDGRID_API_KEY);

async function sendOtpEmail(toEmail, otp) {
  try {
          const msg = {
              to,
              from: {
                  email: process.env.SENDGRID_FROM,
                  name: "UTEBook"  // thêm tên sẽ tăng uy tín email
              },
              subject,
              text,
              html: `<p>${text}</p>`,     // có HTML sẽ ít bị đánh spam hơn
          };

          const response = await sgMail.send(msg);
          console.log("📧 Email sent!", response[0].statusCode);
      } catch (error) {
          console.error("❌ SendGrid Error:", error.response?.body || error);
      }
  }

  module.exports = sendOtpEmail;
