// Simple SendGrid test script for local debugging
// Usage (PowerShell):
//  node .\test_sendgrid.js

// Load .env so the script can pick up SENDGRID_API_KEY and SENDGRID_FROM automatically
require('dotenv').config();

const sg = require('@sendgrid/mail');

const key = process.env.SENDGRID_API_KEY;
const from = process.env.SENDGRID_FROM;

if (!key) {
  console.error('SENDGRID_API_KEY is not set. Export it and re-run the script.');
  process.exitCode = 2;
} else if (!from) {
  console.error('SENDGRID_FROM is not set. Export it and re-run the script.');
  process.exitCode = 2;
} else {
  sg.setApiKey(key);
  (async () => {
    try {
      const res = await sg.send({
        to: from, // send to the same address for quick verification
        from: { email: from, name: 'UTEBook Test' },
        subject: 'SendGrid connectivity test',
        text: 'This is a test message to verify SendGrid API key and sender configuration.'
      });
      console.log('SendGrid response status:', res[0]?.statusCode || 'unknown');
      console.dir(res[0]);
    } catch (err) {
      console.error('SendGrid send failed. Full error object:');
      // Log as much as is safe for debugging (but do not print the API key)
      if (err.response && err.response.body) {
        console.error(JSON.stringify(err.response.body, null, 2));
      } else {
        console.error(err);
      }
      process.exitCode = 1;
    }
  })();
}
