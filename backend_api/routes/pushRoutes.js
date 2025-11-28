const express = require('express');
const router = express.Router();

// Push routes removed - FCM not used in this project per user request
router.post('/push/register', (req, res) => {
  res.status(404).json({ message: 'Push/register disabled in this build' });
});

module.exports = router;
