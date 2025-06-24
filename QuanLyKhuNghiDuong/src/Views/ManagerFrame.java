package Views;

import Controllers.NguoiDungController;
import Controllers.ResortController;
import Controllers.RoomController;
import Controllers.ServiceController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ManagerFrame extends JFrame {

    private JPanel sidebarPanel, mainPanel, headerPanel;
    private JButton btnResorts, btnRooms, btnServices, btnBookings, btnUsers, btnLogout;
    private JButton btnServiceBookings, btnThongBao;
    private JButton btnViewFeedback;

    public ManagerFrame() {
        setTitle("Bảng điều khiển Quản trị viên");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initSidebar();
        initHeader();
        initMainPanel();

        setVisible(true);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(52, 73, 94));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel lblMenu = new JLabel("QUẢN LÝ");
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setFont(new Font("Arial", Font.BOLD, 16));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(lblMenu);
        sidebarPanel.add(Box.createVerticalStrut(20));

        btnResorts = createSidebarButton("Khu nghỉ dưỡng");
        btnRooms = createSidebarButton("Phòng");
        btnServices = createSidebarButton("Dịch vụ");
        btnBookings = createSidebarButton("Đặt phòng");
        btnUsers = createSidebarButton("Người dùng");
        btnServiceBookings = createSidebarButton("Đặt Dịch Vụ");
        btnThongBao = createSidebarButton("Gửi thông báo");
        btnViewFeedback = createSidebarButton("Xem đánh giá");

        sidebarPanel.add(btnResorts);
        sidebarPanel.add(btnRooms);
        sidebarPanel.add(btnServices);
        sidebarPanel.add(btnBookings);
        sidebarPanel.add(btnUsers);
        sidebarPanel.add(btnServiceBookings);
        sidebarPanel.add(btnThongBao);
        sidebarPanel.add(btnViewFeedback);
        add(sidebarPanel, BorderLayout.WEST);

        // Sự kiện
        btnResorts.addActionListener(this::handleResorts);
        btnRooms.addActionListener(this::handleRooms);
        btnServices.addActionListener(this::handleServices);
        btnBookings.addActionListener(this::handleBookings);
        btnUsers.addActionListener(this::handleUsers);
        btnServiceBookings.addActionListener(this::handleServiceBookings);
        btnThongBao.addActionListener(this::handleThongBao);
        btnViewFeedback.addActionListener(this::handleViewFeedback);

    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        return button;
    }

    private void initHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(this::handleLogout);

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadDashboardStats();

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadDashboardStats() {
        mainPanel.removeAll();

        ResortController resortController = new ResortController();
        int totalResorts = resortController.countResorts();
        mainPanel.add(createStatCard("Tổng số Resort", String.valueOf(totalResorts)));

        RoomController roomController = new RoomController();
        int roomCount = roomController.getRoomCount();
        mainPanel.add(createStatCard("Tổng số Phòng", String.valueOf(roomCount)));

        ServiceController serviceController = new ServiceController();
        int serviceCount = serviceController.getServiceCount();
        mainPanel.add(createStatCard("Tổng số Dịch vụ", String.valueOf(serviceCount)));

        // Khởi tạo nguoiDungController và lấy thông tin tổng số người dùng
        NguoiDungController nguoiDungController = new NguoiDungController();
        int customerCount = nguoiDungController.getCustomerCount();
        mainPanel.add(createStatCard("Tổng số Người dùng", String.valueOf(customerCount)));

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createStatCard(String title, String count) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(236, 240, 241));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCount = new JLabel(count, SwingConstants.CENTER);
        lblCount.setFont(new Font("Arial", Font.BOLD, 32));
        lblCount.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(20));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblCount);
        card.add(Box.createVerticalStrut(20));

        return card;
    }

    // Xử lý sự kiện
    private void handleResorts(ActionEvent e) {
        new ResortManagementFrame();
    }

    private void handleRooms(ActionEvent e) {
        new RoomManagementFrame();
    }

    private void handleServices(ActionEvent e) {
        new ServiceManagementFrame();
    }

    private void handleBookings(ActionEvent e) {
        new BookingManagementFrame();
    }

    private void handleUsers(ActionEvent e) {
        new UserManagementFrame();
    }

    private void handleServiceBookings(ActionEvent e) {
        new ServiceBookingManagementFrame();
    }

    private void handleThongBao(ActionEvent e) {
        new NotificationForm();
    }

    private void handleViewFeedback(ActionEvent e) {
        new FeedbackManagementFrame();
    }

    private void handleLogout(ActionEvent e) {
        dispose();
        new LoginFrame();
    }
}
