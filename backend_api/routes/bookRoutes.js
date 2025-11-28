const express = require('express');
const router = express.Router();
// Make sure the path to your Book model is correct
const Book = require('../models/Book');

// @route   GET /api/books
// @desc    Get books, optionally filtered by category and sorted
router.get('/', async (req, res) => {
    try {
        const { category, sort } = req.query;
        let query = {};
        if (category) {
            query.category = category;
        }

        let sortOptions = {};
        if (sort === 'price_asc') {
            sortOptions.price = 1; // 1 for ascending
        } else if (sort === 'price_desc') {
            sortOptions.price = -1; // -1 for descending
        }

        const books = await Book.find(query).sort(sortOptions);

        // The Android app expects a PagedResponse format, so wrap the items.
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
