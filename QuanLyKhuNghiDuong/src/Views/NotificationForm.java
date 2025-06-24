package Views;

import Controllers.NotificationController;
import Models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class NotificationForm extends JFrame {
    private JTextField txtTitle, txtMessage;
    private JButton btnSend, btnDelete;
    private JTable tableNotifications;
    private DefaultTableModel tableModel;

    private NotificationController notificationController;

    public NotificationForm() {  
        setTitle("Quản Lý Thông Báo");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        notificationController = new NotificationController();

        // Giao diện form nhập
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gửi Thông Báo"));

        txtTitle = new JTextField();
        txtMessage = new JTextField();

        inputPanel.add(new JLabel("Tiêu đề:"));
        inputPanel.add(txtTitle);
        inputPanel.add(new JLabel("Nội dung:"));
        inputPanel.add(txtMessage);

        btnSend = new JButton("Gửi Thông Báo");
        btnSend.addActionListener(this::sendNotification);
        inputPanel.add(btnSend);

        btnDelete = new JButton("Xóa Thông Báo");
        btnDelete.addActionListener(this::deleteNotification);
        inputPanel.add(btnDelete);

        // Bảng hiển thị danh sách thông báo
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tiêu đề", "Nội dung", "Ngày gửi"}, 0);
        tableNotifications = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableNotifications);

        loadNotifications(); // Load dữ liệu vào bảng

        // Layout chính
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Hàm gửi thông báo cho tất cả người dùng (trừ admin)
    private void sendNotification(ActionEvent e) {
        String title = txtTitle.getText();
        String message = txtMessage.getText();

        if (title.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        boolean success = notificationController.sendPromoNotificationForAll(title, message);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thông báo đã được gửi thành công!");
            loadNotifications(); // Reload bảng
        } else {
            JOptionPane.showMessageDialog(this, "Gửi thông báo thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm xóa thông báo
    private void deleteNotification(ActionEvent e) {
        int selectedRow = tableNotifications.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa thông báo này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int notificationId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean success = notificationController.deleteNotification(notificationId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa thông báo thành công!");
                loadNotifications();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hàm load dữ liệu vào bảng
    private void loadNotifications() {
        tableModel.setRowCount(0); // Xóa hết dòng cũ
        List<Notification> notifications = notificationController.getAllNotifications();
        for (Notification n : notifications) {
            tableModel.addRow(new Object[]{n.getNotificationId(), n.getTitle(), n.getMessage(), n.getSentAt()});
        }
    }
}
