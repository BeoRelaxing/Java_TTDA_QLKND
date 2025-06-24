package Views;

import Controllers.BookingController;
import Controllers.NguoiDungController;
import Controllers.RoomController;
import Controllers.ServiceBookingController;
import Controllers.ServiceController;
import Models.Booking;
import Models.NguoiDung;
import Models.Resort;
import Models.Room;
import Models.Service;
import Models.ServiceBooking;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import utils.SessionManager;

public class BookingServiceFrame extends JFrame {

    private ServiceController serviceController = new ServiceController();
    private JTextField txtMinPrice, txtMaxPrice, txtRoomType;
    private JComboBox<String> cbStatus;
    private JTable resultTable;
    private Resort resort;
    private List<Room> allRooms;

    public BookingServiceFrame(Resort resort) {
        super("Quản lý đặt phòng và dịch vụ - " + resort.getName());
        this.resort = resort;

        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Khu nghỉ dưỡng: " + resort.getName(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Đặt Phòng", createBookingTab());
        tabbedPane.addTab("Dịch Vụ", createServiceTab(resort.getResortId()));
        tabbedPane.addTab("Hóa đơn", createServiceBookingTab());
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createBookingTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm phòng"));

        txtMinPrice = new JTextField();
        txtMaxPrice = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"", "Available", "Booaked", "Maintenance"});
        txtRoomType = new JTextField();

        inputPanel.add(new JLabel("Giá tối thiểu:"));
        inputPanel.add(txtMinPrice);
        inputPanel.add(new JLabel("Trạng thái:"));
        inputPanel.add(cbStatus);
        inputPanel.add(new JLabel("Giá tối đa:"));
        inputPanel.add(txtMaxPrice);
        inputPanel.add(new JLabel("Loại phòng (nhập tay):"));
        inputPanel.add(txtRoomType);

        panel.add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Số phòng", "Loại phòng", "Giá/đêm", "Trạng thái", "Sức chứa", "ID Resort"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadRoomData();

        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnConfirm = new JButton("Xác nhận đặt phòng");
        JButton btnHistory = new JButton("Lịch sử đặt phòng");
        JButton btnCancel = new JButton("Hủy đặt phòng");

        btnSearch.addActionListener(this::performSearch);

        btnConfirm.addActionListener(e -> {
    int selectedRow = resultTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng để đặt.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int roomId = (int) resultTable.getValueAt(selectedRow, 0);
        double pricePerNight = (double) resultTable.getValueAt(selectedRow, 3);
        String roomStatus = (String) resultTable.getValueAt(selectedRow, 4);

        if (!roomStatus.equalsIgnoreCase("Available")) {
            JOptionPane.showMessageDialog(this, "Phòng này không khả dụng để đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy danh sách các khoảng thời gian đã được đặt
        BookingController bookingController = new BookingController();
        List<Booking> existingBookings = bookingController.getBookingsByRoomId(roomId);

        StringBuilder bookedDatesMessage = new StringBuilder("Phòng đã được đặt trong các khoảng thời gian sau:\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (existingBookings.isEmpty()) {
            bookedDatesMessage.append("Không có lịch đặt nào.\n");
        } else {
            for (Booking b : existingBookings) {
                bookedDatesMessage.append("- Từ ")
                    .append(sdf.format(b.getCheckInDate()))
                    .append(" đến ")
                    .append(sdf.format(b.getCheckOutDate()))
                    .append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, bookedDatesMessage.toString(), "Thông tin đặt trước", JOptionPane.INFORMATION_MESSAGE);

        // Hiển thị JDateChooser để chọn ngày
        JDateChooser checkInChooser = new JDateChooser();
        JDateChooser checkOutChooser = new JDateChooser();
        checkInChooser.setDate(new Date());
        checkOutChooser.setDate(new Date());

        Object[] message = {
            "Ngày check-in:", checkInChooser,
            "Ngày check-out:", checkOutChooser
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Nhập thông tin đặt phòng", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        Date checkInDate = checkInChooser.getDate();
        Date checkOutDate = checkOutChooser.getDate();

        // Kiểm tra tính hợp lệ
        Date today = new Date();
        if (checkInDate == null || checkOutDate == null || checkInDate.before(today)) {
            JOptionPane.showMessageDialog(this, "Ngày check-in/out không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!checkOutDate.after(checkInDate)) {
            JOptionPane.showMessageDialog(this, "Ngày check-out phải sau ngày check-in.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (bookingController.isRoomBooked(roomId, checkInDate, checkOutDate)) {
            JOptionPane.showMessageDialog(this, "Phòng đã được đặt trong khoảng thời gian này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (days <= 0) {
            JOptionPane.showMessageDialog(this, "Phải đặt ít nhất 1 đêm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPrice = pricePerNight * days;

        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập trước khi đặt phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = session.getCurrentUser().getId();
        String status = "Pending";
        Date createdAt = new Date();

        Booking booking = new Booking(0, userId, roomId, checkInDate, checkOutDate, totalPrice, status, createdAt);
        boolean success = bookingController.createBooking(booking);

        if (success) {
            loadRoomData();
            JOptionPane.showMessageDialog(this, "Đặt phòng thành công! Vui lòng chờ Admin xác nhận.");
        } else {
            JOptionPane.showMessageDialog(this, "Đặt phòng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi đặt phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
});


        btnHistory.addActionListener(e -> {
            if (!SessionManager.getInstance().isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem lịch sử đặt phòng.");
                return;
            }

            int userId = SessionManager.getInstance().getCurrentUser().getId(); // hoặc getUserId()
            BookingController controller = new BookingController();
            List<Booking> history = controller.getBookingsByUser(userId);

            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa có lịch sử đặt phòng nào.");
            } else {
                StringBuilder message = new StringBuilder("Lịch sử đặt phòng:\n");
                for (Booking booking : history) {
                    message.append(String.format(
                            "- Mã #%d | Phòng: %d | Nhận: %s | Trả: %s | Trạng thái: %s\n",
                            booking.getBookingId(),
                            booking.getRoomId(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getStatus()
                    ));
                }
                JOptionPane.showMessageDialog(this, message.toString());
            }
        });

        btnCancel.addActionListener(e -> {
            if (!SessionManager.getInstance().isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập.");
                return;
            }

            int userId = SessionManager.getInstance().getCurrentUser().getId();
            BookingController controller = new BookingController();
            List<Booking> checkedInBookings = controller.getCheckedInBookings(userId);

            if (checkedInBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn không có đặt phòng nào đang hoạt động (checked-in).");
                return;
            }

            StringBuilder message = new StringBuilder("Danh sách các phòng đang hoạt động:\n");
            for (Booking booking : checkedInBookings) {
                message.append(String.format("- Mã #%d | Phòng %d | Nhận: %s | Trả: %s\n",
                        booking.getBookingId(),
                        booking.getRoomId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate()));
            }

            String input = JOptionPane.showInputDialog(this, message + "\nNhập mã đặt phòng bạn muốn hủy:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int bookingId = Integer.parseInt(input.trim());

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn xóa đặt phòng #" + bookingId + " khỏi hệ thống?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = controller.deleteBooking(bookingId);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Đặt phòng đã được xóa khỏi hệ thống.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Xóa thất bại. Vui lòng kiểm tra mã hoặc thử lại.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Mã đặt phòng không hợp lệ.");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        for (JButton btn : new JButton[]{btnSearch, btnConfirm, btnHistory, btnCancel}) {
            btn.setPreferredSize(new Dimension(180, 35));
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadRoomData() {
        RoomController controller = new RoomController();
        allRooms = controller.getRoomsByResortName(resort.getName());
        updateTable(allRooms);
    }

    private void performSearch(ActionEvent e) {
        try {
            double min = txtMinPrice.getText().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(txtMinPrice.getText());
            double max = txtMaxPrice.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtMaxPrice.getText());
            String status = cbStatus.getSelectedItem().toString();
            String type = txtRoomType.getText().trim();

            List<Room> filtered = allRooms.stream()
                    .filter(r -> r.getPricePerNight() >= min && r.getPricePerNight() <= max)
                    .filter(r -> status.isEmpty() || r.getStatus().equalsIgnoreCase(status))
                    .filter(r -> type.isEmpty() || r.getRoomType().equalsIgnoreCase(type))
                    .toList();

            updateTable(filtered);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Room> rooms) {
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        for (Room room : rooms) {
            model.addRow(new Object[]{
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getStatus(),
                room.getCapacity(),
                room.getResortId()
            });
        }
    }

    private DefaultTableModel serviceTableModel; // Dùng để cập nhật bảng từ nhiều nơi

    private JPanel createServiceTab(int resortId) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel nhập liệu tìm kiếm dịch vụ
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm dịch vụ"));

        JTextField txtServiceName = new JTextField();
        JTextField txtMinPrice = new JTextField();
        JTextField txtMaxPrice = new JTextField();

        inputPanel.add(new JLabel("Tên dịch vụ:"));
        inputPanel.add(txtServiceName);
        inputPanel.add(new JLabel("Giá tối thiểu:"));
        inputPanel.add(txtMinPrice);
        inputPanel.add(new JLabel("Giá tối đa:"));
        inputPanel.add(txtMaxPrice);

        // 2 ô trống cho cân đối giao diện
        inputPanel.add(new JLabel());
        inputPanel.add(new JLabel());

        panel.add(inputPanel, BorderLayout.NORTH);

        // Bảng hiển thị dịch vụ
        String[] columnNames = {"Mã DV", "Resort Id", "Tên dịch vụ", "Mô tả", "Giá"};
        serviceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable serviceTable = new JTable(serviceTableModel);
        JScrollPane scrollPane = new JScrollPane(serviceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel nút chức năng
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnBookService = new JButton("Đặt dịch vụ");
        JButton btnCancelService = new JButton("Hủy dịch vụ");
        JButton btnDvdd = new JButton("Dịch vụ đã đặt");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        for (JButton btn : new JButton[]{btnSearch, btnBookService, btnCancelService, btnDvdd}) {
            btn.setPreferredSize(new Dimension(180, 35));
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý nút tìm kiếm
        btnSearch.addActionListener(e -> {
            String name = txtServiceName.getText().trim();
            String minPriceStr = txtMinPrice.getText().trim();
            String maxPriceStr = txtMaxPrice.getText().trim();

            Double minPrice = null, maxPrice = null;
            try {
                if (!minPriceStr.isEmpty()) {
                    minPrice = Double.parseDouble(minPriceStr);
                }
                if (!maxPriceStr.isEmpty()) {
                    maxPrice = Double.parseDouble(maxPriceStr);
                }
                if (minPrice != null && minPrice < 0 || maxPrice != null && maxPrice < 0) {
                    throw new NumberFormatException("Giá không được âm.");
                }
                if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                    JOptionPane.showMessageDialog(panel, "Giá tối thiểu không thể lớn hơn giá tối đa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Giá phải là số dương hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi hàm tìm kiếm từ controller
            List<Service> services = serviceController.searchServicesByResort(resort.getName(), name, minPrice, maxPrice);
            updateServiceTable(services);

        });

        // Xử lý nút đặt dịch vụ
        btnBookService.addActionListener(e -> {
            // 1. Kiểm tra đăng nhập
            SessionManager session = SessionManager.getInstance();
            if (!session.isLoggedIn()) {
                JOptionPane.showMessageDialog(panel, "Vui lòng đăng nhập trước khi đặt dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int userId = session.getCurrentUser().getId();

            // 2. Lấy danh sách đặt phòng có trạng thái "checkedin"
            BookingController bookingController = new BookingController();
            List<Booking> checkedInBookings = bookingController.getCheckedInBookings(userId);

            if (checkedInBookings.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Bạn không có đặt phòng nào đang ở trạng thái checked-in để đặt dịch vụ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 3. Hiển thị danh sách đặt phòng để chọn
            String[] bookingOptions = new String[checkedInBookings.size()];
            for (int i = 0; i < checkedInBookings.size(); i++) {
                Booking booking = checkedInBookings.get(i);
                bookingOptions[i] = String.format("Mã #%d | Phòng %d | Nhận: %s | Trả: %s",
                        booking.getBookingId(), booking.getRoomId(),
                        booking.getCheckInDate(), booking.getCheckOutDate());
            }

            String selectedBooking = (String) JOptionPane.showInputDialog(
                    panel,
                    "Chọn đặt phòng để đặt dịch vụ:",
                    "Chọn Đặt Phòng",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    bookingOptions,
                    bookingOptions[0]
            );

            if (selectedBooking == null) {
                return; // Người dùng nhấn Cancel
            }

            // Lấy bookingId từ lựa chọn
            int bookingId = Integer.parseInt(selectedBooking.split("\\|")[0].replace("Mã #", "").trim());

            // 4. Kiểm tra trạng thái booking (dù đã lọc checkedin, nhưng để chắc chắn)
            String status = bookingController.getBookingStatus(bookingId);
            if (!"checkedin".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(panel, "Đặt phòng này không ở trạng thái checked-in. Vui lòng đặt phòng trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Hiển thị danh sách dịch vụ để chọn
            List<Service> services = serviceController.getServicesByResortName(resort.getName());
            if (services.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Không có dịch vụ nào để đặt tại khu nghỉ dưỡng này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] serviceOptions = new String[services.size()];
            for (int i = 0; i < services.size(); i++) {
                Service service = services.get(i);
                serviceOptions[i] = String.format("%s (Mã #%d) - %.2f$", service.getName(), service.getServiceId(), service.getPrice());
            }

            String selectedService = (String) JOptionPane.showInputDialog(
                    panel,
                    "Chọn dịch vụ để đặt:",
                    "Chọn Dịch Vụ",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    serviceOptions,
                    serviceOptions[0]
            );

            if (selectedService == null) {
                return; // Người dùng nhấn Cancel
            }

            // Lấy serviceId từ lựa chọn
            int serviceId = Integer.parseInt(selectedService.split("\\)")[0].split("Mã #")[1].trim());
            double servicePrice = services.stream()
                    .filter(s -> s.getServiceId() == serviceId)
                    .findFirst()
                    .get()
                    .getPrice();

            // 6. Yêu cầu nhập số lượng
            String quantityInput = JOptionPane.showInputDialog(panel, "Nhập số lượng:", "Số Lượng", JOptionPane.PLAIN_MESSAGE);
            if (quantityInput == null) {
                return; // Người dùng nhấn Cancel
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityInput.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(panel, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 7. Tính tổng giá
            double totalPrice = servicePrice * quantity;

            // 8. Tạo ServiceBooking và thêm vào cơ sở dữ liệu
            ServiceBooking serviceBooking = new ServiceBooking(0, bookingId, serviceId, quantity, totalPrice);
            ServiceBookingController serviceBookingController = new ServiceBookingController();
            boolean success = serviceBookingController.addServiceBooking(serviceBooking);

            // 9. Hiển thị thông báo kết quả
            if (success) {
                JOptionPane.showMessageDialog(panel, "Đặt dịch vụ thành công!\nTổng giá: " + totalPrice + "$", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                // Cập nhật lại bảng dịch vụ (nếu cần)
                List<Service> updatedServices = serviceController.getServicesByResortName(resort.getName());
                updateServiceTable(updatedServices);
            } else {
                JOptionPane.showMessageDialog(panel, "Đặt dịch vụ thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        // hủy dịch vụ
        btnCancelService.addActionListener(e -> {
            // Lấy userId từ SessionManager
            int userId = SessionManager.getInstance().getCurrentUser().getId(); // Giả sử bạn có SessionManager

            // Lấy danh sách dịch vụ đã đặt theo userId
            ServiceBookingController serviceBookingController = new ServiceBookingController();
            List<ServiceBooking> userServiceBookings = serviceBookingController.getServiceBookingsByUserId(userId);

            if (userServiceBookings.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Bạn chưa đặt dịch vụ nào để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tạo danh sách các lựa chọn dịch vụ để hủy
            String[] serviceOptions = new String[userServiceBookings.size()];
            for (int i = 0; i < userServiceBookings.size(); i++) {
                ServiceBooking sb = userServiceBookings.get(i);
                serviceOptions[i] = String.format("Mã #%d | Booking ID: %d | Service ID: %d | Số lượng: %d | Tổng tiền: %.2f$",
                        sb.getServiceBookingId(), sb.getBookingId(), sb.getServiceId(), sb.getQuantity(), sb.getTotalPrice());
            }

            String selectedService = (String) JOptionPane.showInputDialog(
                    panel,
                    "Chọn dịch vụ để hủy:",
                    "Hủy Dịch Vụ",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    serviceOptions,
                    serviceOptions[0]
            );

            if (selectedService == null) {
                return; // Thoát nếu người dùng nhấn Cancel
            }

            // Lấy serviceBookingId từ lựa chọn
            int serviceBookingId;
            try {
                serviceBookingId = Integer.parseInt(selectedService.split("\\|")[0].replace("Mã #", "").trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Định dạng mã dịch vụ không hợp lệ. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xác nhận hủy
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Bạn có chắc chắn muốn hủy dịch vụ mã #" + serviceBookingId + "?",
                    "Xác nhận hủy",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = serviceBookingController.deleteServiceBooking(serviceBookingId);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Hủy dịch vụ thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    // Cập nhật lại bảng dịch vụ
                    List<Service> updatedServices = serviceController.getServicesByResortName(resort.getName());
                    updateServiceTable(updatedServices);
                } else {
                    JOptionPane.showMessageDialog(panel, "Hủy dịch vụ thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDvdd.addActionListener(e -> {
            // Lấy userId từ SessionManager
            int userId = SessionManager.getInstance().getCurrentUser().getId(); // Giả sử bạn có SessionManager

            // Lấy danh sách dịch vụ đã đặt theo userId
            ServiceBookingController serviceBookingController = new ServiceBookingController();
            List<ServiceBooking> userServiceBookings = serviceBookingController.getServiceBookingsByUserId(userId);

            if (userServiceBookings.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Bạn chưa đặt dịch vụ nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tạo thông điệp hiển thị lịch sử dịch vụ
            StringBuilder message = new StringBuilder("Lịch sử đặt dịch vụ:\n\n");
            for (ServiceBooking sb : userServiceBookings) {
                message.append(String.format(
                        "Mã đặt dịch vụ: #%d | Booking ID: %d | Service ID: %d | Số lượng: %d | Tổng tiền: %.2f$\n",
                        sb.getServiceBookingId(),
                        sb.getBookingId(),
                        sb.getServiceId(),
                        sb.getQuantity(),
                        sb.getTotalPrice()
                ));
            }

            // Hiển thị danh sách trong một JOptionPane với JTextArea có thể cuộn
            JTextArea textArea = new JTextArea(message.toString());
            textArea.setEditable(false);
            JScrollPane historyScrollPane = new JScrollPane(textArea);
            historyScrollPane.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(panel, historyScrollPane, "Lịch Sử Dịch Vụ", JOptionPane.INFORMATION_MESSAGE);
        });

        // Load dữ liệu mặc định khi mở tab (toàn bộ dịch vụ của resortId)
        List<Service> defaultServices = serviceController.getServicesByResortName(resort.getName());
        updateServiceTable(defaultServices);

        return panel;
    }
// ----- Hàm cập nhật bảng dịch vụ -----

    private void updateServiceTable(List<Service> services) {
        serviceTableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (Service s : services) {
            Object[] row = {
                s.getServiceId(),
                s.getResortId(),
                s.getName(),
                s.getDescription(),
                s.getPrice()
            };
            serviceTableModel.addRow(row);
        }
    }

    // tab chức năng xuất hóa đơn 
    private JPanel createServiceBookingTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        NguoiDungController userController = new NguoiDungController();
        BookingController bookingController = new BookingController();
        ServiceBookingController serviceBookingController = new ServiceBookingController();

        // Lấy user hiện tại từ SessionManager
        NguoiDung currentUser = utils.SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            JLabel noLoginLabel = new JLabel("Vui lòng đăng nhập để xem thông tin đặt dịch vụ.");
            noLoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noLoginLabel, BorderLayout.CENTER);
            return panel;
        }

        int userId = currentUser.getId();

        // Lấy thông tin người dùng
        NguoiDung user = userController.getUserById(userId);

        // Lấy danh sách booking của user (mình lấy booking đầu tiên để demo)
        List<Booking> bookings = bookingController.getBookingsByUser(userId);
        Booking booking = bookings.isEmpty() ? null : bookings.get(0);

        // Thông tin người dùng panel
        JPanel userPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Người Dùng"));
        userPanel.add(new JLabel("Tên:"));
        userPanel.add(new JLabel(user != null ? user.getUsername() : ""));
        userPanel.add(new JLabel("Email:"));
        userPanel.add(new JLabel(user != null ? user.getEmail() : ""));
        userPanel.add(new JLabel("Số điện thoại:"));
        userPanel.add(new JLabel(user != null ? user.getPhone() : ""));

        // Thông tin booking panel
        JPanel bookingPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Đặt Phòng"));
        if (booking != null) {
            bookingPanel.add(new JLabel("Mã đặt phòng:"));
            bookingPanel.add(new JLabel(String.valueOf(booking.getBookingId())));
            bookingPanel.add(new JLabel("Ngày nhận phòng:"));
            bookingPanel.add(new JLabel(booking.getCheckInDate().toString()));
            bookingPanel.add(new JLabel("Ngày trả phòng:"));
            bookingPanel.add(new JLabel(booking.getCheckOutDate().toString()));

            int nights = bookingController.getNumberOfNights(booking.getBookingId());
            double roomPrice = booking.getTotalPrice() * nights;
            bookingPanel.add(new JLabel("Giá phòng:"));
            bookingPanel.add(new JLabel("$" + String.format("%.2f", roomPrice)));
        } else {
            bookingPanel.add(new JLabel("Chưa có đặt phòng nào."));
        }

        // Thông tin dịch vụ đã đặt
        String[] serviceColumns = {"Mã Dịch Vụ", "Số Lượng", "Giá Dịch Vụ"};
        List<ServiceBooking> serviceBookings = serviceBookingController.getServiceBookingsByUserId(userId);

        Object[][] serviceData;
        if (serviceBookings.isEmpty()) {
            serviceData = new Object[0][3];
        } else {
            serviceData = new Object[serviceBookings.size()][3];
            for (int i = 0; i < serviceBookings.size(); i++) {
                ServiceBooking sb = serviceBookings.get(i);
                serviceData[i][0] = sb.getServiceId();
                serviceData[i][1] = sb.getQuantity();
                serviceData[i][2] = serviceBookingController.getServicePrice(sb.getServiceId()) * sb.getQuantity();
            }
        }

        JTable serviceTable = new JTable(serviceData, serviceColumns);
        serviceTable.setFillsViewportHeight(true);
        JScrollPane serviceScrollPane = new JScrollPane(serviceTable);
        serviceScrollPane.setBorder(BorderFactory.createTitledBorder("Dịch Vụ Đã Đặt"));

        // Tính tổng giá dịch vụ
        double totalServicePrice = 0;
        for (Object[] row : serviceData) {
            totalServicePrice += (double) row[2];
        }

        // Tính tổng giá (giá phòng + giá dịch vụ)
        double totalPrice = 0;
        if (booking != null) {
            int nights = bookingController.getNumberOfNights(booking.getBookingId());
            double roomPrice = booking.getTotalPrice() * nights;
            totalPrice = roomPrice + totalServicePrice;
        }
        JLabel totalLabel = new JLabel("Tổng Cộng: $" + String.format("%.2f", totalPrice));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Panel chính chứa các thông tin
        JPanel invoicePanel = new JPanel();
        invoicePanel.setLayout(new BoxLayout(invoicePanel, BoxLayout.Y_AXIS));
        invoicePanel.add(userPanel);
        invoicePanel.add(Box.createVerticalStrut(10));
        invoicePanel.add(bookingPanel);
        invoicePanel.add(Box.createVerticalStrut(10));
        invoicePanel.add(serviceScrollPane);
        invoicePanel.add(Box.createVerticalStrut(10));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        invoicePanel.add(totalPanel);
        totalLabel.setForeground(Color.RED);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPay = new JButton("Thanh Toán");
        JButton btnExport = new JButton("Xuất Hóa Đơn");

        btnPay.setPreferredSize(new Dimension(150, 35));
        btnExport.setPreferredSize(new Dimension(150, 35));
        
        // Chức năng thanh toán
        btnPay.addActionListener(e -> {
            String message = "Thông tin thanh toán:\n"
                    + "Chủ tài khoản: PHAM TUAN ANH\n"
                    + "Số tài khoản: 123456\n"
                    + "Ngân hàng: Viettinbank";
            JOptionPane.showMessageDialog(panel, message, "Thanh Toán", JOptionPane.INFORMATION_MESSAGE);
        });

        btnExport.addActionListener(e -> {
            if (booking == null) {
                JOptionPane.showMessageDialog(panel, "Không có thông tin đặt phòng để xuất hóa đơn.");
                return;
            }

            try {
                double totalService = 0;
                for (Object[] row : serviceData) {
                    totalService += (double) row[2];
                }

                int nights = bookingController.getNumberOfNights(booking.getBookingId());
                double roomTotal = booking.getTotalPrice() * nights;
                double finalTotal = roomTotal + totalService;

                StringBuilder invoice = new StringBuilder();
                invoice.append("=== HÓA ĐƠN THANH TOÁN ===\n\n");

                invoice.append("Thông Tin Người Dùng:\n");
                invoice.append("Tên: ").append(user.getUsername()).append("\n");
                invoice.append("Email: ").append(user.getEmail()).append("\n");
                invoice.append("SĐT: ").append(user.getPhone()).append("\n\n");

                invoice.append("Thông Tin Đặt Phòng:\n");
                invoice.append("Mã đặt phòng: ").append(booking.getBookingId()).append("\n");
                invoice.append("Nhận phòng: ").append(booking.getCheckInDate()).append("\n");
                invoice.append("Trả phòng: ").append(booking.getCheckOutDate()).append("\n");
                invoice.append("Giá phòng: $").append(String.format("%.2f", roomTotal)).append("\n\n");

                invoice.append("Dịch Vụ Đã Đặt:\n");
                invoice.append(String.format("%-10s %-10s %-10s\n", "Mã DV", "Số lượng", "Giá"));
                for (Object[] row : serviceData) {
                    invoice.append(String.format("%-10s %-10s %-10s\n", row[0], row[1], row[2]));
                }

                invoice.append("\nTổng Cộng: $").append(String.format("%.2f", finalTotal)).append("\n");

                String fileName = "HoaDon_" + booking.getBookingId() + ".txt";
                File file = new File(fileName);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(invoice.toString());
                }

                Desktop.getDesktop().open(file);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Lỗi khi xuất hóa đơn: " + ex.getMessage());
            }
        });

        buttonPanel.add(btnPay);
        buttonPanel.add(btnExport);

        panel.add(new JScrollPane(invoicePanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

}
