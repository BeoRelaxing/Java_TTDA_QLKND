/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;
import DAO.ServiceDAO;
import Models.Service;
import java.sql.Connection;
import java.util.List;
/**
 *
 * @author Beo
 */
public class ServiceController {
    private ServiceDAO dao = new ServiceDAO();

    public List<Service> getAllServices() {
        return dao.getAllServices();
    }

    public void addService(Service service) {
        dao.addService(service);
    }

    public void updateService(Service service) {
        dao.updateService(service);
    }

    public void deleteService(int serviceId) {
        dao.deleteService(serviceId);
    }
    // đếm
    public int getServiceCount() {
    return dao.getServiceCount();
}
    
 public List<Service> getServicesByResortName(String resortName) {
        return dao.getServicesByResortName(resortName);
    }

public List<Service> searchServicesByResort(String resortName, String serviceName, Double minPrice, Double maxPrice) {
    return dao.searchServicesByResort(resortName, serviceName, minPrice, maxPrice);
}


}



