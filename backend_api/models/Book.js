const mongoose = require("mongoose");

const productSchema = new mongoose.Schema({
  title: { type: String, required: true },
  description: { type: String },
  price: { type: Number, required: true },
  image_url: { type: String, required: true },
  category: { type: String, required: true },
  publisher: { type: String },
  year: { type: Number },
});

// The third argument specifies the exact collection name 'product' in your MongoDB.
module.exports = mongoose.model("Product", productSchema, "product");
