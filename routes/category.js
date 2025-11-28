const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const categoryController = require('../controllers/categoryController');


// Create category (protected)
router.post('/', auth, categoryController.createCategory);
// Get all categories
router.get('/', categoryController.getCategories);
// Get single category by id
router.get('/:id', categoryController.getCategory);
// Update & delete could be added similarly


module.exports = router;