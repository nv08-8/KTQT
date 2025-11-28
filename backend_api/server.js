require("dotenv").config();
const express = require("express");
const cors = require("cors");
const app = express();
const connectDB = require("./db");
const http = require('http');
const { Server } = require('socket.io');
const jwt = require('jsonwebtoken');

// Import all routes
const authRoutes = require('./routes/authRoutes');
const categoryRoutes = require('./routes/categoryRoutes'); // <--- IMPORT
const bookRoutes = require('./routes/bookRoutes');       // <--- IMPORT

app.use(express.json());
app.use(cors());

// Middleware UTF-8
app.use((req, res, next) => {
    // This middleware is no longer needed as Express and libraries handle UTF-8 by default
    // res.setHeader('Content-Type', 'application/json; charset=utf-8');
    next();
});

// Warn if SendGrid not configured properly
if (!process.env.SENDGRID_API_KEY) {
    console.warn('⚠️ SENDGRID_API_KEY is not set. Email sending will fail. Set it in backend_api/.env or environment variables.');
}
if (!process.env.SENDGRID_FROM) {
    console.warn('⚠️ SENDGRID_FROM is not set. Using fallback sender address.');
}

// Use API routes
app.use('/api/auth', authRoutes);
app.use('/api/categories', categoryRoutes); // <--- USE
app.use('/api/books', bookRoutes);           // <--- USE

// Connect to MongoDB
connectDB();

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
