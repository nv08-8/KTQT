require("dotenv").config();
const express = require("express");
const cors = require("cors");
const app = express();
const connectDB = require("./db");
const http = require('http');
const { Server } = require('socket.io');
const jwt = require('jsonwebtoken');

// 1. Import all the routes
const authRoutes = require('./routes/authRoutes');
const categoryRoutes = require('./routes/categoryRoutes');
const bookRoutes = require('./routes/bookRoutes');

// 2. Use Middleware
app.use(express.json());
app.use(cors());

// Middleware UTF-8 (Optional but good practice)
app.use((req, res, next) => {
    res.setHeader('Content-Type', 'application/json; charset=utf-8');
    next();
});

// Warn if SendGrid not configured properly
if (!process.env.SENDGRID_API_KEY) {
    console.warn('⚠️ SENDGRID_API_KEY is not set. Email sending will fail.');
}
if (!process.env.SENDGRID_FROM) {
    console.warn('⚠️ SENDGRID_FROM is not set. Using fallback sender address.');
}

// 3. Use the routes with correct prefixes
app.use('/api/auth', authRoutes);
app.use('/api/categories', categoryRoutes);
app.use('/api/books', bookRoutes);

// 4. Connect to the database
connectDB();

// 5. Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
