const express = require("express");
const router = express.Router();
const Book = require("../models/Book");
const Category = require("../models/Category");

// Get all categories
router.get("/categories", async (req, res) => {
  try {
    const categories = await Category.find();
    res.json(categories);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get all books
router.get("/books", async (req, res) => {
  try {
    const books = await Book.find();
    res.json(books);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get books by category
router.get("/books/category/:categoryName", async (req, res) => {
  try {
    const books = await Book.find({ category: req.params.categoryName });
    res.json(books);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
