const mongoose = require("mongoose");
const dns = require('dns').promises;

async function connectDB() {
  // Use environment variable when available to avoid hardcoding credentials
  const uri = process.env.MONGODB_URI ||
    "mongodb+srv://admin:4kTVlrRaaTthqvgW@bookstore.5ku2c2q.mongodb.net/bookstore?retryWrites=true&w=majority";

  try {
    // If using mongodb+srv, attempt to resolve SRV records for diagnostics
    const srvMatch = uri.match(/^mongodb\+srv:\/\/([^/]+)/);
    if (srvMatch) {
      const host = srvMatch[1].split(':')[0];
      try {
        const srvName = `_mongodb._tcp.${host}`;
        const records = await dns.resolveSrv(srvName);
        console.info('Resolved SRV records for', host, records);
      } catch (srvErr) {
        console.info('SRV resolution failed for', host, srvErr && srvErr.message ? srvErr.message : srvErr);
      }
    }

    // Sanitize URI for logging (do not leak credentials)
    const safeUri = uri.replace(/:\/\/(.*?):(.*?)@/, '://<user>:<pass>@');
    console.info('Attempting MongoDB connection to:', safeUri);

    // Optional: enable mongoose debug via env var
    if (process.env.MONGODB_DEBUG === 'true') mongoose.set('debug', true);

    await mongoose.connect(uri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });

    console.log("MongoDB connected!");
  } catch (error) {
    console.error("MongoDB connection error:", error);
    // If driver includes a 'reason' object, log it separately for clarity
    if (error && error.reason) console.error('Detailed reason:', error.reason);
  }
}

module.exports = connectDB;
