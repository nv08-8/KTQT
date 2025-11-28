const Category = require('../models/Category');


exports.createCategory = async(req, res) => {
    const { name, description } = req.body;
    try {
        let cat = await Category.findOne({ name });
        if (cat) return res.status(400).json({ msg: 'Category already exists' });
        cat = new Category({ name, description });
        await cat.save();
        res.json(cat);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};


exports.getCategories = async(req, res) => {
    try {
        const cats = await Category.find();
        res.json(cats);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};


exports.getCategory = async(req, res) => {
    try {
        const cat = await Category.findById(req.params.id);
        if (!cat) return res.status(404).json({ msg: 'Category not found' });
        res.json(cat);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
};