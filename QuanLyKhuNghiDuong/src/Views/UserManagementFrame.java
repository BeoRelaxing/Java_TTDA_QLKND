package Views;

import Controllers.NguoiDungController;
import Models.NguoiDung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private NguoiDungController userController = new NguoiDungController();
    private JButton btnEdit, btnDelete, btnLoad;

    public UserManagementFrame() {
        setTitle("Quản lý Người dùng");
        setSize(800, 500);  
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initTable();
        initButtons();

        loadUserData();

        setVisible(true);
    }

    private void initTable() {
        // Cập nhật tên các cột để hiển thị thêm mật khẩu
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên", "Email", "SĐT", "Vai trò", "Mật khẩu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa bảng
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel panel = new JPanel();
        btnEdit = new JButton("Chỉnh sửa");
        btnDelete = new JButton("Xóa");
        btnLoad = new JButton("Tải lại");
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnLoad); // Thêm nút Load
        add(panel, BorderLayout.SOUTH);

        btnEdit.addActionListener(this::handleEdit);
        btnDelete.addActionListener(this::handleDelete);
        btnLoad.addActionListener(this::handleLoad);  // Thêm sự kiện nút Load
    }

    private void loadUserData() {
        List<NguoiDung> customers = userController.getAllUsers();
        tableModel.setRowCount(0);  // Xóa tất cả các dòng trong bảng
        for (NguoiDung user : customers) {
            tableModel.addRow(new Object[]{
                    user.getId(),            // ID người dùng
                    user.getUsername(),      // Tên người dùng
                    user.getEmail(),         // Email người dùng
                    user.getPhone(),         // SĐT người dùng
                    user.getRole(),          // Vai trò người dùng
                    user.getPassword()               
            });
        }
    }

    private void handleEdit(ActionEvent e) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần chỉnh sửa!");
        return;
    }

    int userId = (int) tableModel.getValueAt(selectedRow, 0);

    // Lấy người dùng từ database (lấy mật khẩu thật)
    NguoiDung user = userController.getUserById(userId);

    if (user != null) {
        new EditUserFrame(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getPassword() // ✅ Thêm dòng này cho đủ 6 tham số
        );
    } else {
        JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}



    private void handleDelete(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa người dùng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userController.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadUserData();  // Cập nhật lại dữ liệu bảng
            } else {
                JOptionPane.showMessageDialog(this, "Xóa không thành công!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hàm tải lại dữ liệu khi nhấn nút "Tải lại"
    private void handleLoad(ActionEvent e) {
        loadUserData();  // Cập nhật lại bảng
    }
}
