const mongoose = require("mongoose");
const dns = require('dns').promises;

async function connectDB() {
  // Use environment variable. The .env file should define MONGO_URI
  const uri = process.env.MONGO_URI;

  if (!uri) {
    console.error("MongoDB connection error: MONGO_URI environment variable is not set in .env file.");
    process.exit(1); // Stop the server if DB connection string is missing
  }

  try {
    // Sanitize URI for logging (do not leak credentials)
    const safeUri = uri.replace(/:\/\/(.*?):(.*?)@/, '://<user>:<pass>@');
    console.info('Attempting MongoDB connection to:', safeUri);

    await mongoose.connect(uri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });

    console.log("MongoDB connected!");
  } catch (error) {
    console.error("MongoDB connection error:", error);
    if (error && error.reason) console.error('Detailed reason:', error.reason);
    process.exit(1); // Stop the server on connection failure
  }
}

module.exports = connectDB;
