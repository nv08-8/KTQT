const User = require('../models/User');
const bcrypt = require('bcrypt');


exports.getProfile = async(req, res) => {
    try {
        const user = await User.findById(req.user.id).select("-password");
        if (!user) return res.status(404).json({ message: "User not found" });

        res.json(user);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};


exports.updateProfile = async(req, res) => {
    try {
        const { name, phone, avatar } = req.body;

        const updateData = {};
        if (name) updateData.name = name;
        if (phone) updateData.phone = phone;
        if (avatar) updateData.avatar = avatar;

        const user = await User.findByIdAndUpdate(
            req.user.id,
            updateData, { new: true }
        ).select("-password");

        if (!user) return res.status(404).json({ message: "User not found" });

        res.json({
            message: "Profile updated successfully",
            user
        });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};


exports.changePassword = async(req, res) => {
    try {
        const { oldPassword, newPassword } = req.body;

        const user = await User.findById(req.user.id);
        if (!user) return res.status(404).json({ message: "User not found" });

        // Check old password
        const isMatch = await bcrypt.compare(oldPassword, user.password);
        if (!isMatch)
            return res.status(400).json({ message: "Old password is incorrect" });

        // Hash new password
        const salt = await bcrypt.genSalt(10);
        user.password = await bcrypt.hash(newPassword, salt);
        await user.save();

        res.json({ message: "Password changed successfully" });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

module.exports = {
    getProfile: exports.getProfile,
    updateProfile: exports.updateProfile,
    changePassword: exports.changePassword
};