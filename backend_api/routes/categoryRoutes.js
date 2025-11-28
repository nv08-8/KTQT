const express = require('express');
const router = express.Router();
// Make sure the path to your Category model is correct
const Category = require('../models/Category');

// @route   GET /api/categories
// @desc    Get all categories
router.get('/', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (err) {
        console.error("Error fetching categories:", err);
        res.status(500).send('Server Error');
    }
});

module.exports = router;
