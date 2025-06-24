package Views;

import Controllers.FeedbackController;
import Controllers.NguoiDungController;
import Controllers.NotificationController;
import Models.Resort;
import Controllers.ResortController;
import DAO.NotificationDAO;
import Models.Feedback;
import Models.NguoiDung;
import Models.Notification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import utils.SessionManager;

public class CustomerFrame extends JFrame {

    private final int userId;
    private final JTabbedPane tabbedPane;
    private JTextField searchField;
    private JList<Resort> resortList;
    private DefaultListModel<Resort> listModel;
    private final ResortController controller;
    private final FeedbackController feedbackController;

    public CustomerFrame(int userId) {
        this.userId = userId;
        this.controller = new ResortController();
        this.feedbackController = new FeedbackController();

        setTitle("Khách hàng - Hệ thống Quản lý Resort");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Khu Nghỉ Dưỡng", createResortsTab());
        tabbedPane.addTab("Thông Tin Cá Nhân", createPersonalInfoTab());
        tabbedPane.addTab("Đánh Giá", createFeedbackTab(feedbackController));
        tabbedPane.addTab("Thông Báo", createNotificationsTab());

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(this::handleLogout);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(btnLogout);
        add(logoutPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        for (JButton btn : buttons) {
            btn.setPreferredSize(new Dimension(150, 40));
            buttonPanel.add(btn);
        }
        panel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

//    private void showDevelopingMessage() {
//        JOptionPane.showMessageDialog(this, "Chức năng đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//    }

    private JPanel createResortsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchField = new JTextField(20);
        JButton btnSearchResorts = new JButton("Tìm Khu Nghỉ Dưỡng");
        searchPanel.add(new JLabel("Tên/Vị trí:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearchResorts);

        // Resort list
        listModel = new DefaultListModel<>();
        resortList = new JList<>(listModel);
        resortList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resortList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Resort resort) {
                    setText(resort.getName() + " - " + resort.getLocation());
                }
                return this;
            }
        });

        // Buttons panel
        JButton btnSelectResort = new JButton("Chọn Khu Nghỉ Dưỡng");
        JButton btnViewResortDetails = new JButton("Xem Chi Tiết Khu Nghỉ Dưỡng");

        // Event handlers
        btnSearchResorts.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            listModel.clear();
            List<Resort> foundResorts = controller.searchResortsWithoutIdAndCreatedAt(query);
            if (foundResorts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khu nghỉ dưỡng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                foundResorts.forEach(listModel::addElement);
            }
        });

        btnViewResortDetails.addActionListener(e -> {
            Resort selectedResort = resortList.getSelectedValue();
            if (selectedResort == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khu nghỉ dưỡng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showResortDetailsDialog(selectedResort);
        });

        btnSelectResort.addActionListener(e -> {
            Resort selectedResort = resortList.getSelectedValue();
            if (selectedResort == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khu nghỉ dưỡng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BookingServiceFrame bookingFrame = new BookingServiceFrame(selectedResort);
            bookingFrame.setVisible(true);
        });

        // Layout
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resortList), BorderLayout.CENTER);
        panel.add(createButtonPanel(btnSelectResort, btnViewResortDetails), BorderLayout.SOUTH);

        return panel;
    }

    private void showResortDetailsDialog(Resort resort) {
        JDialog dialog = new JDialog(this, "Chi Tiết Khu Nghỉ Dưỡng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("SansSerif", Font.PLAIN, 12));
        details.setText(
                "Tên: " + resort.getName() + "\n"
                + "Vị trí: " + resort.getLocation() + "\n"
                + "Loại: " + resort.getType() + "\n"
                + "Mô tả: " + resort.getDescription() + "\n"
                + "Phạm vi giá: " + resort.getPriceRange() + "\n"
                + "Tiện ích: " + resort.getAmenities()
        );

        dialog.add(new JScrollPane(details), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createPersonalInfoTab() {
        JButton btnUpdateInfo = new JButton("Cập Nhật Thông Tin");
        btnUpdateInfo.addActionListener(e -> {
            NguoiDung currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng.");
                return;
            }

            JTextField txtName = new JTextField(currentUser.getUsername());
            JTextField txtEmail = new JTextField(currentUser.getEmail());
            JTextField txtPhone = new JTextField(currentUser.getPhone());

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Tên đăng nhập:"));
            panel.add(txtName);
            panel.add(new JLabel("Email:"));
            panel.add(txtEmail);
            panel.add(new JLabel("Số điện thoại:"));
            panel.add(txtPhone);

            int result = JOptionPane.showConfirmDialog(null, panel, "Cập nhật thông tin cá nhân",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                currentUser.setUsername(txtName.getText());
                currentUser.setEmail(txtEmail.getText());
                currentUser.setPhone(txtPhone.getText());

                NguoiDungController controller = new NguoiDungController();
                boolean success = controller.updateUser(currentUser);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Cập nhật thông tin thành công.");
                } else {
                    JOptionPane.showMessageDialog(null, "Cập nhật thất bại.");
                }
            }
        });

        JButton btnChangePassword = new JButton("Đổi Mật Khẩu");
        btnChangePassword.addActionListener(e -> {
            NguoiDung currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng.");
                return;
            }

            JPasswordField txtOldPassword = new JPasswordField();
            JPasswordField txtNewPassword = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Mật khẩu cũ:"));
            panel.add(txtOldPassword);
            panel.add(new JLabel("Mật khẩu mới:"));
            panel.add(txtNewPassword);

            int result = JOptionPane.showConfirmDialog(null, panel, "Đổi mật khẩu",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String oldPass = new String(txtOldPassword.getPassword());
                String newPass = new String(txtNewPassword.getPassword());

                // Bạn cần mã hóa mật khẩu giống như cách bạn đang lưu trong DB (ví dụ: MD5, SHA...)
                if (!currentUser.getPassword().equals(oldPass)) {
                    JOptionPane.showMessageDialog(null, "Mật khẩu cũ không đúng.");
                    return;
                }

                currentUser.setPassword(newPass);

                NguoiDungController controller = new NguoiDungController();
                boolean success = controller.updateUser(currentUser);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Đổi mật khẩu thành công.");
                } else {
                    JOptionPane.showMessageDialog(null, "Đổi mật khẩu thất bại.");
                }
            }
        });

        return createButtonPanel(btnUpdateInfo, btnChangePassword);
    }

    private JPanel createFeedbackTab(FeedbackController controller) {
        JButton btnGiveFeedback = new JButton("Gửi Đánh Giá");

        btnGiveFeedback.addActionListener(e -> {
            NguoiDung currentUser = SessionManager.getInstance().getCurrentUser();

            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "Bạn cần đăng nhập để gửi đánh giá!");
                return;
            }

            JTextArea feedbackArea = new JTextArea(5, 20);
            int result = JOptionPane.showConfirmDialog(
                    null,
                    new JScrollPane(feedbackArea),
                    "Nhập đánh giá của bạn (" + currentUser.getUsername() + ")",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String content = feedbackArea.getText().trim();
                if (!content.isEmpty()) {
                    Feedback feedback = new Feedback(userId, content);  // tạo đối tượng Feedback
                    boolean success = controller.addFeedback(feedback);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Cảm ơn bạn đã gửi đánh giá!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gửi đánh giá thất bại. Vui lòng thử lại.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Nội dung đánh giá không được để trống.");
                }
            }
        });

        return createButtonPanel(btnGiveFeedback);
    }

    // chức năng tab thông báo 
    private JPanel createNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        NguoiDung currentUser = utils.SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            JOptionPane.showMessageDialog(panel, "Bạn chưa đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return panel;
        }
        int currentUserId = currentUser.getId();

        NotificationController controller = new NotificationController();

        // Tạo model và bảng cho thông báo chưa xem
        String[] columns = {"ID", "User ID", "Tiêu đề", "Nội dung", "Thời gian gửi"};
        DefaultTableModel unreadModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable unreadTable = new JTable(unreadModel);
        JScrollPane unreadScroll = new JScrollPane(unreadTable);
        unreadScroll.setBorder(BorderFactory.createTitledBorder("Thông báo chưa xem"));

        // Nút xác nhận và nút xem lịch sử
        JButton btnConfirm = new JButton("Xác nhận thông báo");
        JButton btnViewHistory = new JButton("Lịch sử thông báo đã nhận");

        // Load dữ liệu chưa xem
        Runnable loadUnread = () -> {
            unreadModel.setRowCount(0);
            List<Notification> notifications = controller.getAllNotifications();

            for (Notification n : notifications) {
                if (n.getUserId() == currentUserId && !n.isRead()) {
                    unreadModel.addRow(new Object[]{
                        n.getNotificationId(),
                        n.getUserId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getSentAt()
                    });
                }
            }
        };

        loadUnread.run();

        // Xác nhận thông báo
        btnConfirm.addActionListener(e -> {
            int selectedRow = unreadTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một thông báo để xác nhận.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int notificationId = (int) unreadModel.getValueAt(selectedRow, 0);
            boolean updated = controller.markNotificationAsRead(notificationId);

            if (updated) {
                JOptionPane.showMessageDialog(panel, "Xác nhận thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadUnread.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Lỗi khi xác nhận thông báo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Lịch sử thông báo đã nhận
        btnViewHistory.addActionListener(e -> {
            List<Notification> notifications = controller.getAllNotifications();
            StringBuilder history = new StringBuilder();

            for (Notification n : notifications) {
                if (n.getUserId() == currentUserId && n.isRead()) {
                    history.append("• ").append(n.getTitle()).append(" - ")
                            .append(n.getMessage()).append(" (").append(n.getSentAt()).append(")\n\n");
                }
            }

            if (history.length() == 0) {
                history.append("Chưa có thông báo nào được xác nhận.");
            }

            JTextArea textArea = new JTextArea(history.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(panel, scrollPane, "Lịch sử thông báo đã nhận", JOptionPane.INFORMATION_MESSAGE);
        });

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnViewHistory);

        // Thêm vào panel chính
        panel.add(unreadScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private boolean updateNotificationAsRead(int notificationId) {
        try {
            NotificationDAO dao = new NotificationDAO();
            return dao.markNotificationAsRead(notificationId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleLogout(ActionEvent e) {
        dispose();
        new LoginFrame();
    }
}
