const jwt = require('jsonwebtoken');


module.exports = function(req, res, next) {
    // token có thể nằm trong header Authorization: Bearer <token>
    const authHeader = req.header('Authorization');
    const token = authHeader && authHeader.startsWith('Bearer ') ? authHeader.split(' ')[1] : null;
    if (!token) return res.status(401).json({ msg: 'No token, authorization denied' });


    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded.user; // payload: { user: { id } }
        next();
    } catch (err) {
        res.status(401).json({ msg: 'Token is not valid' });
    }
};