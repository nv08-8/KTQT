const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  id: { type: Number, unique: true, sparse: true },
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  phone: { type: String },
  created_at: { type: Date, default: Date.now },
  otp_code: { type: Number },
  status: { type: String, enum: ['inactive', 'active'], default: 'inactive' },
  role: { type: String, default: 'user' }
});

module.exports = mongoose.model('User', userSchema);

