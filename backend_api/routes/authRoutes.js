const express = require("express");
const router = express.Router();
const User = require("../models/User");
const bcrypt = require("bcryptjs");
const sendEmail = require("../utils/sendOtp");
const SALT_ROUNDS = 10;
const jwt = require('jsonwebtoken');
const JWT_SECRET = process.env.JWT_SECRET || 'dev_jwt_secret_change_me';

router.post("/send-otp", async (req, res) => {
    const { email } = req.body;

    if (!email)
        return res.status(400).json({ message: "Thiếu email!" });

    const otp = Math.floor(100000 + Math.random() * 900000);

    try {
        let user = await User.findOne({ email });

        if (!user) {
            user = new User({
                email,
                name: email.split('@')[0], // Use part of email as a temporary name
                password: 'temp_password_please_change', // Temporary password
                otp_code: otp,
                status: 'inactive'
            });
            await user.save();
        } else {
            if (user.status === "active") {
                return res.status(400).json({ message: "Email đã tồn tại!" });
            }
            user.otp_code = otp;
            await user.save();
        }

        res.json({ message: "OTP đã được gửi đến email!" });
        sendEmail(email, "Mã OTP đăng ký", `Mã OTP của bạn là: ${otp}`)
            .catch(emailErr => console.error("Gửi email thất bại:", emailErr));
    } catch (err) {
        console.error("Lỗi gửi OTP:", err);
        res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.post("/verify-otp", async (req, res) => {
    const { email, otp } = req.body;
    try {
        const user = await User.findOne({ email, otp_code: otp });
        if (!user) {
            return res.status(400).json({ success: false, message: "OTP sai!" });
        }
        return res.json({ success: true, message: "OTP hợp lệ!" });
    } catch (err) {
        console.error("Lỗi verify OTP:", err);
        return res.status(500).json({ success: false, message: "Lỗi phía server." });
    }
});

router.post("/finish-register", async (req, res) => {
    const { name, phone, email, password } = req.body;
    if (!name || !phone || !email || !password)
        return res.status(400).json({ message: "Thiếu thông tin!" });

    const phoneStr = phone.toString().trim();
    if (!/^\d{9,11}$/.test(phoneStr)) {
        return res.status(400).json({ message: "Số điện thoại không hợp lệ." });
    }

    try {
        const existingPhone = await User.findOne({ phone: phoneStr, status: 'active' });
        if (existingPhone) {
            return res.status(400).json({ message: "Số điện thoại đã được sử dụng!" });
        }

        const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);
        
        const updatedUser = await User.findOneAndUpdate(
            { email, status: 'inactive' },
            {
                name,
                phone: phoneStr,
                password: hashedPassword,
                otp_code: null,
                status: 'active'
            },
            { new: true }
        );

        if (!updatedUser) {
            return res.status(404).json({ message: "Không tìm thấy tài khoản hoặc tài khoản đã được kích hoạt." });
        }

        return res.json({ message: "Tạo tài khoản thành công!" });
    } catch (err) {
        console.error("Lỗi tạo tài khoản:", err);
        return res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.post("/login", async (req, res) => {
    const { email, password } = req.body;
    try {
        const user = await User.findOne({ email }).select('+password');;
        if (!user) {
            return res.status(401).json({ message: "Sai email hoặc mật khẩu!" });
        }

        if (user.status !== "active") {
            return res.status(403).json({ message: "Tài khoản chưa xác thực email!" });
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(401).json({ message: "Sai email hoặc mật khẩu!" });
        }

        const token = jwt.sign({ id: user._id }, JWT_SECRET, { expiresIn: '7d' });

        return res.json({
            message: "Đăng nhập thành công!",
            user: user.toJSON(),
            token
        });
    } catch (err) {
        console.error("Lỗi đăng nhập:", err);
        return res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.post("/forgot-password", async (req, res) => {
    const { email } = req.body;
    const otp = Math.floor(100000 + Math.random() * 900000);
    try {
        const user = await User.findOneAndUpdate(
            { email, status: 'active' },
            { otp_code: otp },
            { new: true }
        );

        if (!user) {
            return res.status(404).json({ message: "Email không tồn tại hoặc chưa active!" });
        }

        res.json({ success: true, message: "OTP đã được gửi đến email!" });
        sendEmail(email, "Mã OTP đặt lại mật khẩu", `Mã OTP của bạn là: ${otp}`)
            .catch(emailErr => console.error("Gửi email quên mật khẩu thất bại:", emailErr));
    } catch (err) {
        console.error("Lỗi forgot password:", err);
        return res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.post("/reset-password", async (req, res) => {
    // Make sure client sends email, otp, and newPassword
    const { email, otp, newPassword } = req.body;
    if (!email || !otp || !newPassword) {
        return res.status(400).json({ message: "Thiếu thông tin!" });
    }

    try {
        const hashedPassword = await bcrypt.hash(newPassword, SALT_ROUNDS);
        const user = await User.findOneAndUpdate(
            { email, otp_code: otp, status: 'active' },
            { password: hashedPassword, otp_code: null },
            { new: true }
        );

        if (!user) {
            return res.status(400).json({ message: "OTP không hợp lệ hoặc email không tồn tại." });
        }
        res.json({ success: true, message: "Đặt lại mật khẩu thành công!" });
    } catch (err) {
        console.error("Lỗi reset mật khẩu:", err);
        return res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.get("/user/:id", async (req, res) => {
    const { id } = req.params;
    try {
        const user = await User.findById(id);
        if (!user || user.status !== 'active') {
            return res.status(404).json({ message: "User not found" });
        }
        res.json(user);
    } catch (err) {
        console.error("Lỗi lấy user:", err);
        if (err.name === 'CastError') {
            return res.status(400).json({ message: "Invalid user ID format" });
        }
        return res.status(500).json({ message: "Lỗi phía server." });
    }
});

router.post("/change-password", async (req, res) => {
    const { email, currentPassword, newPassword } = req.body;
    if (!email || !currentPassword || !newPassword) {
        return res.status(400).json({ success: false, message: "Thiếu thông tin!" });
    }

    try {
        const user = await User.findOne({ email, status: 'active' }).select('+password');
        if (!user) {
            return res.status(404).json({ success: false, message: "User not found" });
        }

        const isMatch = await bcrypt.compare(currentPassword, user.password);
        if (!isMatch) {
            return res.status(401).json({ success: false, message: "Mật khẩu hiện tại không đúng!" });
        }

        user.password = await bcrypt.hash(newPassword, SALT_ROUNDS);
        await user.save();
        res.json({ success: true, message: "Đổi mật khẩu thành công!" });
    } catch (err) {
        console.error("Lỗi đổi mật khẩu:", err);
        return res.status(500).json({ success: false, message: "Lỗi phía server." });
    }
});

router.put("/user/:id", async (req, res) => {
    const { id } = req.params;
    const { name, phone } = req.body;
    if (!name && !phone) {
        return res.status(400).json({ success: false, message: "Không có thông tin để cập nhật!" });
    }

    try {
        const updateData = {};
        if (name) updateData.name = name;
        if (phone) {
             const phoneStr = phone.toString().trim();
            if (!/^\d{9,11}$/.test(phoneStr)) {
                return res.status(400).json({ success: false, message: "Số điện thoại không hợp lệ." });
            }
            const existingPhoneUser = await User.findOne({ phone: phoneStr, _id: { $ne: id } });
            if (existingPhoneUser) {
                return res.status(400).json({ success: false, message: "Số điện thoại đã được sử dụng!" });
            }
            updateData.phone = phoneStr;
        }

        const updatedUser = await User.findByIdAndUpdate(
            id,
            { $set: updateData },
            { new: true }
        );

        if (!updatedUser) {
            return res.status(404).json({ success: false, message: "User not found" });
        }
        res.json({ success: true, message: "Cập nhật thông tin thành công!" });
    } catch (err) {
        console.error("Lỗi cập nhật user:", err);
        if (err.name === 'CastError') {
            return res.status(400).json({ message: "Invalid user ID format" });
        }
        return res.status(500).json({ success: false, message: "Lỗi phía server." });
    }
});

module.exports = router;
