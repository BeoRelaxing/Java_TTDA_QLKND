package Views;

import Models.NguoiDung;
import Controllers.NguoiDungController;
import utils.SessionManager; // Đảm bảo import SessionManager
import java.awt.event.ActionEvent;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    // Khai báo controller
    private NguoiDungController userController = new NguoiDungController();

    public LoginFrame() {
        
        setTitle("Hệ thống quản lý đặt phòng và dịch vụ nghỉ dưỡng");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setBounds(50, 50, 120, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(180, 50, 150, 25);
        txtUsername.setName("txtUsername");
        add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(50, 90, 120, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 90, 150, 25);
        txtPassword.setName("txtPassword");
        add(txtPassword);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBounds(50, 150, 120, 30);
        btnLogin.setName("btnLogin");
        add(btnLogin);

        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(210, 150, 120, 30);
        txtUsername.setName("txtUsername");
        add(btnRegister);

        // Sự kiện nút
        btnLogin.addActionListener(this::handleLogin);
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        setVisible(true);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi Controller để xử lý login
        NguoiDung user = userController.login(username, password);

        if (user != null) {
            // Lưu thông tin người dùng vào SessionManager
            SessionManager.getInstance().setCurrentUser(user);
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng Login Frame

            // Phân quyền
            if ("admin".equalsIgnoreCase(user.getRole())) {
                new ManagerFrame().setVisible(true);
            } else {
                // Truyền userId vào CustomerFrame
                new CustomerFrame(user.getId()).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
}