const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  id: { type: Number, unique: true, sparse: true },
  name: { type: String, required: true, trim: true },
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  password: { type: String, required: true },
  phone: { type: String, default: null },
  // created_at will be populated by timestamps option
  otp_code: { type: Number, default: null },
  status: { type: String, enum: ['inactive', 'active'], default: 'inactive' },
  role: { type: String, enum: ['user', 'admin'], default: 'user' }
}, {
  timestamps: { createdAt: 'created_at', updatedAt: 'updated_at' }
});

// Ensure indexes (email unique already creates an index, keep id unique/sparse)
userSchema.index({ id: 1 }, { unique: true, sparse: true });
userSchema.index({ email: 1 }, { unique: true });

// Remove sensitive fields when converting to JSON (e.g., responses)
userSchema.set('toJSON', {
  virtuals: true,
  transform: function (doc, ret) {
    delete ret.password;
    delete ret.__v;
    return ret;
  }
});

module.exports = mongoose.model('User', userSchema);
