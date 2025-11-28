const express = require("express");
const connectDB = require("./db");
const Book = require("./models/Book");
const User = require("./models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

const app = express();
app.use(express.json());

// Connect to MongoDB
connectDB();

// --- PRODUCT & CATEGORY APIs ---

// GET all unique categories
app.get('/api/categories', async (req, res) => {
  try {
    const categories = await Book.distinct('category');
    // Map to the object structure your Android app expects
    const categoryObjects = categories.map(cat => ({ id: cat, name: cat, iconUrl: '' }));
    res.json(categoryObjects);
  } catch (error) {
    console.error('/api/categories error:', error);
    res.status(500).json({ message: error.message });
  }
});

// GET products by category with pagination
app.get('/api/categories/:id/products', async (req, res) => {
  try {
    const categoryId = req.params.id;
    const page = parseInt(req.query.page) || 1;
    const pageSize = parseInt(req.query.pageSize) || 10;
    const sort = req.query.sort || 'title'; // default sort

    const skip = (page - 1) * pageSize;

    const products = await Book.find({ category: categoryId })
      .sort(sort)
      .skip(skip)
      .limit(pageSize);

    const totalProducts = await Book.countDocuments({ category: categoryId });
    const totalPages = Math.ceil(totalProducts / pageSize);

    res.json({
      items: products,
      page: page,
      pageSize: pageSize,
      totalItems: totalProducts,
      totalPages: totalPages
    });
  } catch (error) {
    console.error('/api/categories/:id/products error:', error);
    res.status(500).json({ message: error.message });
  }
});


// --- AUTH APIs ---

// Register endpoint
app.post('/api/register', async (req, res) => {
  try {
    const { name, email, password, phone } = req.body;
    if (!name || !email || !password) return res.status(400).json({ message: 'Missing required fields' });

    const existing = await User.findOne({ email });
    if (existing) return res.status(400).json({ message: 'Email already registered' });

    const hashed = await bcrypt.hash(password, 10);
    const last = await User.findOne().sort({ id: -1 }).select('id');
    const nextId = last && last.id ? last.id + 1 : 1;

    const user = await User.create({
      id: nextId,
      name,
      email,
      password: hashed,
      phone,
      status: 'inactive',
      otp_code: Math.floor(100000 + Math.random() * 900000)
    });

    res.status(201).json({ message: 'User registered', userId: user.id, otp_code: user.otp_code });
  } catch (error) {
    console.error('/api/register error:', error);
    res.status(500).json({ message: error.message });
  }
});

// Login endpoint
app.post('/api/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Missing email or password' });

    const user = await User.findOne({ email });
    if (!user) return res.status(401).json({ message: 'Invalid credentials' });

    const ok = await bcrypt.compare(password, user.password);
    if (!ok) return res.status(401).json({ message: 'Invalid credentials' });

    const token = jwt.sign({ id: user.id, email: user.email, role: user.role }, process.env.JWT_SECRET || 'secretkey', { expiresIn: '7d' });

    res.json({ message: 'Login successful', token, user: { id: user.id, name: user.name, email: user.email, phone: user.phone, role: user.role } });
  } catch (error) {
    console.error('/api/login error:', error);
    res.status(500).json({ message: error.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
