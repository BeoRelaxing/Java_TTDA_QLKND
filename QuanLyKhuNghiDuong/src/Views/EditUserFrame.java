package Views;

import Controllers.NguoiDungController;
import Models.NguoiDung;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditUserFrame extends JFrame {
    private JTextField txtName, txtEmail, txtPhone, txtRole, txtPassword;
    private JButton btnSave;
    private int userId;

    public EditUserFrame(int userId, String name, String email, String phone, String role, String password) {
        this.userId = userId;

        setTitle("Chỉnh sửa Người dùng");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Tên:"));
        txtName = new JTextField(name);
        add(txtName);

        add(new JLabel("Email:"));
        txtEmail = new JTextField(email);
        add(txtEmail);

        add(new JLabel("SĐT:"));
        txtPhone = new JTextField(phone);
        add(txtPhone);

        add(new JLabel("Vai trò:"));
        txtRole = new JTextField(role);
        add(txtRole);

        add(new JLabel("Mật khẩu:"));
        txtPassword = new JTextField(password);  // Hiển thị mật khẩu cũ
        add(txtPassword);

        btnSave = new JButton("Lưu");
        add(btnSave);

        // Để cân layout đẹp hơn (thêm 1 ô trống)
        add(new JLabel());

        btnSave.addActionListener(this::handleSave);

        setVisible(true);
    }

    private void handleSave(ActionEvent e) {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = txtRole.getText().trim();
        String password = txtPassword.getText().trim();

        NguoiDungController userController = new NguoiDungController();
        NguoiDung user = new NguoiDung(userId, name, email, password, phone, role, null);
        boolean success = userController.updateUser(user);

        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật không thành công!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
