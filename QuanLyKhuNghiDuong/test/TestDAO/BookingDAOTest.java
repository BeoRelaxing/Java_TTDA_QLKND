package TestDAO;

import DAO.BookingDAO;
import Models.Booking;
import org.junit.*;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class BookingDAOTest {

    private static BookingDAO bookingDAO;
    private int testBookingId = -1;  // Không static để tránh race condition khi chạy song song

    @BeforeClass
    public static void setUpBeforeClass() {
        bookingDAO = new BookingDAO();
    }

    @Before
    public void setUp() {
        // Xóa booking test cũ trước mỗi test nếu còn tồn tại
        if (testBookingId > 0) {
            bookingDAO.deleteBooking(testBookingId);
            testBookingId = -1;
        }
    }

    @After
    public void tearDown() {
        // Xóa booking test sau mỗi test
        if (testBookingId > 0) {
            bookingDAO.deleteBooking(testBookingId);
            testBookingId = -1;
        }
    }

    @Test
    public void testAddBooking() {
        Booking booking = new Booking(
            0, 
            14, // id user
            2,  // id phòng 
            new java.sql.Date(System.currentTimeMillis()),  
            new java.sql.Date(System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000), // +2 ngày
            500.0,
            "checkedin",  // phải là 'checkedin' để isRoomBooked nhận diện đúng
            new java.sql.Date(System.currentTimeMillis())
        );

        boolean added = bookingDAO.addBooking(booking);
        assertTrue("Booking should be added successfully", added);

        // Tìm booking mới tạo để lấy bookingId
        List<Booking> allBookings = bookingDAO.getAllBookings();
        Booking found = allBookings.stream()
            .filter(b -> b.getUserId() == booking.getUserId()
                && b.getRoomId() == booking.getRoomId()
                && "checkedin".equals(b.getStatus()))
            .findFirst()
            .orElse(null);

        assertNotNull("Booking added should be found", found);

        testBookingId = found.getBookingId();
        assertTrue("Booking ID should be positive", testBookingId > 0);
    }

    @Test
    public void testIsRoomBooked() {
        // Thêm booking trạng thái 'checkedin' để test
        testAddBooking();

        Booking booking = bookingDAO.getBookingById(testBookingId);
        assertNotNull("Booking must not be null", booking);

        java.sql.Date sqlCheckInDate = new java.sql.Date(booking.getCheckInDate().getTime());
        java.sql.Date sqlCheckOutDate = new java.sql.Date(booking.getCheckOutDate().getTime());

        boolean booked = bookingDAO.isRoomBooked(
            booking.getRoomId(),
            sqlCheckInDate,
            sqlCheckOutDate
        );

        assertTrue("Room should be marked as booked", booked);
    }

    @Test
    public void testCancelBooking() {
        // Thêm booking trước khi test hủy
        testAddBooking();

        boolean canceled = bookingDAO.cancelBooking(testBookingId);
        assertTrue("Cancel booking should return true", canceled);

        String status = bookingDAO.getBookingStatus(testBookingId);
        assertEquals("Booking status should be 'Cancelled'", "Cancelled", status);
    }
}
