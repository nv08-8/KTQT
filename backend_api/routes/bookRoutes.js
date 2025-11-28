const express = require('express');
const router = express.Router();
// Make sure the path to your Book model is correct
const Book = require('../models/Book');

// @route   GET /api/books
// @desc    Get books, optionally filtered by categoryId
router.get('/', async (req, res) => {
    try {
        const { categoryId } = req.query;
        let query = {};
        if (categoryId) {
            // The field in your Book model that links to the category's ID
            // This might be 'category', 'categoryId', or something else.
            // Let's assume it is 'category' as per your DB screenshot.
            query.category = categoryId; 
        }
        const books = await Book.find(query);

        // The Android app expects a PagedResponse format, so wrap the items.
        res.json({ items: books });
    } catch (err) {
        console.error("Error fetching books:", err);
        res.status(500).send('Server Error');
    }
});

module.exports = router;
