require("dotenv").config();
const express = require("express");
const cors = require("cors");
const app = express();
const connectDB = require("./db");
const http = require('http');
const { Server } = require('socket.io');
const jwt = require('jsonwebtoken');
const authRoutes = require('./routes/authRoutes');

app.use(express.json());
app.use(cors());

// Middleware UTF-8
app.use((req, res, next) => {
    res.setHeader('Content-Type', 'application/json; charset=utf-8');
    next();
});

// Warn if SendGrid not configured properly
if (!process.env.SENDGRID_API_KEY) {
    console.warn('⚠️ SENDGRID_API_KEY is not set. Email sending will fail. Set it in backend_api/.env or environment variables.');
}
if (!process.env.SENDGRID_FROM) {
    console.warn('⚠️ SENDGRID_FROM is not set. Using fallback sender address.');
}

// API routes
app.use('/api', authRoutes);

// Connect to MongoDB
connectDB();

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
