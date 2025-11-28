const express = require('express');
const router = express.Router();
// Make sure the path to your Book model is correct
const Book = require('../models/Book');

// @route   GET /api/books
// @desc    Get books, optionally filtered by category name
router.get('/', async (req, res) => {
    try {
        const { category } = req.query; // Use 'category' to match the Android app
        let query = {};
        if (category) {
            query.category = category; 
        }
        const books = await Book.find(query);

        // The Android app expects a PagedResponse format, so wrap the items.
        // For simplicity, we are not implementing full pagination here yet.
        res.json({ 
            items: books,
            page: 1,
            totalPages: 1
        });
    } catch (err) {
        console.error("Error fetching books:", err);
        res.status(500).send('Server Error');
    }
});

module.exports = router;
