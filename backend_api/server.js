require('dotenv').config();
const express = require("express");
const connectDB = require("./db");
const Book = require("./models/Book");
const Category = require("./models/Category");
const User = require("./models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const sendOtpEmail = require("./utils/sendOtp");

const app = express();
app.use(express.json());

// Connect to MongoDB
connectDB();


// =========================
//     TEST API
// =========================
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


// =========================
//     AUTH API
// =========================

// REGISTER
const registerHandler = async (req, res) => {
  try {
    const { name, email, password, phone } = req.body;
    if (!name || !email || !password)
      return res.status(400).json({ message: "Missing required fields" });

    const existing = await User.findOne({ email });
    if (existing)
      return res.status(400).json({ message: "Email already registered" });

    const hashed = await bcrypt.hash(password, 10);

    // auto-increment ID
    const last = await User.findOne().sort({ id: -1 }).select("id");
    const nextId = last?.id ? last.id + 1 : 1;

    const otp = Math.floor(100000 + Math.random() * 900000);

    const user = await User.create({
      id: nextId,
      name,
      email,
      password: hashed,
      phone,
      status: "inactive",
      otp_code: otp,
      role: "user"
    });

    await sendOtpEmail(email, otp);

    res.status(201).json({
      message: "User registered",
      userId: user.id,
      otp_code: user.otp_code
    });
  } catch (error) {
    console.error("/api/register error:", error);
    res.status(500).json({ message: error.message });
  }
};

// LOGIN
const loginHandler = async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password)
      return res.status(400).json({ message: "Missing email or password" });

    const user = await User.findOne({ email }).select("+password");
    if (!user) return res.status(401).json({ message: "Invalid credentials" });

    const ok = await bcrypt.compare(password, user.password);
    if (!ok) return res.status(401).json({ message: "Invalid credentials" });

    const token = jwt.sign(
      { id: user.id, email: user.email, role: user.role },
      process.env.JWT_SECRET || "secretkey",
      { expiresIn: "7d" }
    );

    res.json({
      message: "Login successful",
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phone: user.phone,
        role: user.role
      }
    });
  } catch (error) {
    console.error("/api/login error:", error);
    res.status(500).json({ message: error.message });
  }
};

// SEND OTP
const sendOtpHandler = async (req, res) => {
  try {
    const { email } = req.body;
    if (!email)
      return res.status(400).json({ message: "Email is required" });

    const otp = Math.floor(100000 + Math.random() * 900000);

    const user = await User.findOneAndUpdate({ email }, { otp_code: otp });

    if (!user) return res.status(404).json({ message: "User not found" });

    await sendOtpEmail(email, otp);

    res.status(200).json({ message: "OTP sent successfully" });
  } catch (error) {
    console.error("/api/send-otp error:", error);
    res.status(500).json({ message: error.message });
  }
};


// MOUNT ENDPOINTS
app.post("/api/register", registerHandler);
app.post("/auth/register", registerHandler);

app.post("/api/login", loginHandler);
app.post("/auth/login", loginHandler);

app.post("/api/send-otp", sendOtpHandler);


// =========================
//     CATEGORY API
// =========================

app.get("/api/categories", async (req, res) => {
  try {
    const categories = await Category.find();
    res.json(categories);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});


// =========================
//     BOOK API
// =========================

// All books
app.get("/api/books", async (req, res) => {
  try {
    const books = await Book.find();
    res.json(books);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Books by category
app.get("/api/books/category/:name", async (req, res) => {
  try {
    const books = await Book.find({ category: req.params.name }).sort({ price: 1 });
    res.json(books);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Book detail
app.get("/api/books/:id", async (req, res) => {
  try {
    const book = await Book.findById(req.params.id);
    if (!book)
      return res.status(404).json({ message: "Book not found" });

    res.json(book);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});


// =========================
//     RUN SERVER
// =========================

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
