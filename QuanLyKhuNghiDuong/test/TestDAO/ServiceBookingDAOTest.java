package TestDAO;

import DAO.*;
import Models.*;
import org.junit.*;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceBookingDAOTest {

    private static ServiceBookingDAO serviceBookingDAO;
    private static BookingDAO bookingDAO;
    private static ServiceDAO serviceDAO;

    private int testServiceId = -1;
    private int testBookingId = -1;
    private int testServiceBookingId = -1;

    @BeforeClass
    public static void setUpBeforeClass() {
        serviceBookingDAO = new ServiceBookingDAO();
        bookingDAO = new BookingDAO();
        serviceDAO = new ServiceDAO();
    }

    @Before
    public void setUp() {

        Service service = new Service();
        service.setResortId(1);
        service.setName("Test Service");
        service.setDescription("Service for testing");
        service.setPrice(100.0);
        serviceDAO.addService(service);

        List<Service> allServices = serviceDAO.getAllServices();
        testServiceId = allServices.get(allServices.size() - 1).getServiceId();

        // Tạo booking test
        Booking booking = new Booking(
                0,
                14,
                2,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000),
                500.0,
                "checkedin",
                new Date(System.currentTimeMillis())
        );
        bookingDAO.addBooking(booking);
        List<Booking> allBookings = bookingDAO.getAllBookings();
        testBookingId = allBookings.get(allBookings.size() - 1).getBookingId();
    }

    @After
    public void tearDown() {
        // Xoá service booking trước nếu có
        if (testBookingId > 0) {
            List<ServiceBooking> list = serviceBookingDAO.getAllServiceBookings();
            for (ServiceBooking sb : list) {
                if (sb.getBookingId() == testBookingId) {
                    serviceBookingDAO.deleteServiceBooking(sb.getServiceBookingId());
                }
            }
        }

        // Xoá booking
        if (testBookingId > 0) {
            bookingDAO.deleteBooking(testBookingId);
            testBookingId = -1;
        }

        // Xoá service
        if (testServiceId > 0) {
            serviceDAO.deleteService(testServiceId);
            testServiceId = -1;
        }
    }

    @Test

    public void testAddAndDeleteServiceBooking() {
        ServiceBooking sb = new ServiceBooking(0, testBookingId, testServiceId, 2, 100.0);
        boolean added = serviceBookingDAO.addServiceBooking(sb);
        assertTrue(added);

        // Lấy lại để kiểm tra và lấy ID
        List<ServiceBooking> list = serviceBookingDAO.getAllServiceBookings();
        ServiceBooking last = list.get(list.size() - 1);
        testServiceBookingId = last.getServiceBookingId();

        assertEquals(testBookingId, last.getBookingId());
        assertEquals(testServiceId, last.getServiceId());

        boolean deleted = serviceBookingDAO.deleteServiceBooking(testServiceBookingId);
        assertTrue(deleted);
        testServiceBookingId = -1; // đã xoá
    }

    @Test
    public void testUpdateServiceBooking() {
        ServiceBooking sb = new ServiceBooking(0, testBookingId, testServiceId, 2, 100.0);
        assertTrue(serviceBookingDAO.addServiceBooking(sb));

        List<ServiceBooking> list = serviceBookingDAO.getAllServiceBookings();
        ServiceBooking last = list.get(list.size() - 1);
        testServiceBookingId = last.getServiceBookingId();

        last.setQuantity(10);
        boolean updated = serviceBookingDAO.updateServiceBooking(last);
        assertTrue(updated);
    }

    @Test
    public void testGetBookingStatus() {
        String status = serviceBookingDAO.getBookingStatus(testBookingId);
        assertEquals("checkedin", status);
    }

    @Test
    public void testGetServicePrice() {
        double price = serviceBookingDAO.getServicePrice(testServiceId);
        assertEquals(100.0, price, 0.001);
    }

    @Test
    public void testGetServiceBookingsByUserId() {
        ServiceBooking sb = new ServiceBooking(0, testBookingId, testServiceId, 2, 100.0);
        serviceBookingDAO.addServiceBooking(sb);
        List<ServiceBooking> list = serviceBookingDAO.getServiceBookingsByUserId(14);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(s -> s.getBookingId() == testBookingId));
    }
}
