const Product = require('../models/Product');
const Category = require('../models/Category');


exports.createProduct = async(req, res) => {
    const { name, description, price, category } = req.body;
    try {
        const cat = await Category.findById(category);
        if (!cat) return res.status(400).json({ msg: 'Invalid category' });


        const product = new Product({ name, description, price, category });
        await product.save();
        res.json(product);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};


exports.getProductsByCategory = async(req, res) => {
    try {
        const categoryId = req.params.categoryId;
        const products = await Product.find({ category: categoryId }).populate('category');
        res.json(products);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};


exports.getProducts = async(req, res) => {
    try {
        const products = await Product.find().populate('category');
        res.json(products);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};