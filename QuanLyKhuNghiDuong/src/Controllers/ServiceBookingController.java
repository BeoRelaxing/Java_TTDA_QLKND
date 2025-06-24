package Controllers;
import DAO.ServiceBookingDAO;
import Models.ServiceBooking;
import java.util.List;

public class ServiceBookingController {
    private ServiceBookingDAO dao = new ServiceBookingDAO();

    public List<ServiceBooking> getAllServiceBookings() {
        return dao.getAllServiceBookings();
    }

    public boolean updateServiceBooking(ServiceBooking sb) {
        return dao.updateServiceBooking(sb);
    }
    
    public boolean addServiceBooking(ServiceBooking sb) {
        return dao.addServiceBooking(sb);
    }

    public String getBookingStatus(int bookingId) {
        return dao.getBookingStatus(bookingId);
    }

    public double getServicePrice(int serviceId) {
        return dao.getServicePrice(serviceId);
    }
    
    public boolean deleteServiceBooking(int serviceBookingId) {
        return dao.deleteServiceBooking(serviceBookingId);
    }
    
    public List<ServiceBooking> getServiceBookingsByUserId(int userId) {
        return dao.getServiceBookingsByUserId(userId);
    }
}