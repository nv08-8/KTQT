const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const productController = require('../controllers/productController');


// Create product (protected)
router.post('/', auth, productController.createProduct);
// Get products by category (query param or path)
router.get('/by-category/:categoryId', productController.getProductsByCategory);
// Get all products
router.get('/', productController.getProducts);


module.exports = router;