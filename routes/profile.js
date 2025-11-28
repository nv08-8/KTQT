const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const profileController = require('../controllers/profileController');


// Get current user profile (protected)
router.get('/', auth, profileController.getProfile);
// Update profile
router.put('/', auth, profileController.updateProfile);


module.exports = router;