const express = require("express");
const connectDB = require("./db");
const Book = require("./models/Book");

const app = express();
app.use(express.json());

// Connect to MongoDB
connectDB();

// Test add data
app.get("/test", async (req, res) => {
  try {
    const book = await Book.create({
      title: "UTEBook",
      price: 99
    });
    res.json(book);
  } catch (error) {
    console.error("/test error:", error);
    res.status(500).json({ error: error.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

