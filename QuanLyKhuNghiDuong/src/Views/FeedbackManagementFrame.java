package Views;

import Controllers.FeedbackController;
import Models.Feedback;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FeedbackManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private FeedbackController feedbackController;
    private JButton deleteButton;

    public FeedbackManagementFrame() {
        setTitle("Quản lý Đánh giá");
        setSize(600, 400);
        setLocationRelativeTo(null);

        feedbackController = new FeedbackController();

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        // Table với 2 cột: User ID, Nội dung đánh giá
        tableModel = new DefaultTableModel(new String[]{"User ID", "Nội dung"}, 0);
        table = new JTable(tableModel);

        loadFeedbacks();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Thêm nút xóa
        deleteButton = new JButton("Xóa đánh giá");
        add(deleteButton, BorderLayout.SOUTH);

        // Bắt sự kiện click nút xóa
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedFeedback();
            }
        });
    }

    private void loadFeedbacks() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<Feedback> feedbackList = feedbackController.getAllFeedbacks();
        if (feedbackList != null) {
            for (Feedback fb : feedbackList) {
                tableModel.addRow(new Object[]{fb.getUserId(), fb.getContent()});
            }
        }
    }

    private void deleteSelectedFeedback() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đánh giá để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String content = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa đánh giá này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = feedbackController.deleteFeedback(userId, content);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa đánh giá thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadFeedbacks();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa đánh giá thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
