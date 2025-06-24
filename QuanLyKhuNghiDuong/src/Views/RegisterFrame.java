package Views;

import Models.NguoiDung;
import Controllers.NguoiDungController;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class RegisterFrame extends JFrame {
    private JTextField txtName, txtPhone, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnBack;

    // Khai báo controller
    private NguoiDungController userController = new NguoiDungController();

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblName = new JLabel("Tên người dùng:");
        lblName.setBounds(50, 50, 120, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(180, 50, 150, 25);
        add(txtName);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(50, 90, 120, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 90, 150, 25);
        add(txtPassword);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setBounds(50, 130, 120, 25);
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(180, 130, 150, 25);
        add(txtPhone);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 170, 120, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(180, 170, 150, 25);
        add(txtEmail);

        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(50, 220, 120, 30);
        add(btnRegister);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(210, 220, 120, 30);
        add(btnBack);

        btnRegister.addActionListener(this::handleRegister);
        btnBack.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private void handleRegister(ActionEvent e) {
    String username = txtName.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();
    String phone = txtPhone.getText().trim();
    String email = txtEmail.getText().trim();

    if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Kiểm tra tồn tại
    boolean userExists = userController.checkUserExists(username, email);
    if (userExists) {
        JOptionPane.showMessageDialog(this, "Tên người dùng hoặc email đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Không mã hóa mật khẩu nữa, lưu mật khẩu thô
    String hashedPassword = password;  // Lưu mật khẩu thô

    // Tạo user mới
    Date currentDate = new Date();
    NguoiDung newUser = new NguoiDung(0, username, email, hashedPassword, phone, "customer", currentDate);
    boolean success = userController.register(newUser);

    if (success) {
        JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mời bạn đăng nhập.");
        dispose();
        new LoginFrame();
    } else {
        JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

}
