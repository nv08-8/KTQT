const sgMail = require("@sendgrid/mail");

// Don't set API key here unconditionally; validate before sending so we can throw a helpful error.
if (process.env.SENDGRID_API_KEY) {
  sgMail.setApiKey(process.env.SENDGRID_API_KEY);
}

/**
 * sendOtpEmail(toEmail, otp)
 * sendOtpEmail(toEmail, subject, text)
 */
async function sendOtpEmail(toEmail, subjectOrOtp, text) {
  if (!toEmail) {
    throw new Error('sendOtpEmail: missing toEmail');
  }

  if (!process.env.SENDGRID_API_KEY) {
    throw new Error('SENDGRID_API_KEY is not configured. Set it in your environment or .env file.');
  }

  // Determine subject and text body
  let subject = 'Your UTEBook verification code';
  let textBody = '';

  if (typeof text === 'string' && typeof subjectOrOtp === 'string') {
    // Called as (toEmail, subject, text)
    subject = subjectOrOtp;
    textBody = text;
  } else {
    // Called as (toEmail, otp)
    const otp = subjectOrOtp;
    subject = 'Your UTEBook verification code';
    textBody = `Your verification code is ${otp}. It will expire in 10 minutes.`;
  }

  const fromEmail = process.env.SENDGRID_FROM || 'no-reply@utebook.example';

  const msg = {
    to: toEmail,
    from: {
      email: fromEmail,
      name: 'UTEBook'
    },
    subject,
    text: textBody,
    html: `<p>${textBody}</p>`
  };

  try {
    const response = await sgMail.send(msg);
    console.log('📧 Email sent!', response[0]?.statusCode || 'no-status');
    return response;
  } catch (error) {
    // Provide the SendGrid response body when available for easier debugging
    if (error.response && error.response.body) {
      console.error('❌ SendGrid Error:', error.response.body);
    } else {
      console.error('❌ SendGrid Error:', error);
    }
    throw error;
  }
}

module.exports = sendOtpEmail;
