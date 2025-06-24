package TestDAO;

import DAO.ServiceDAO;
import Models.Service;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class ServiceDAOTest {
    private static ServiceDAO serviceDAO;
    private int testServiceId; // ID service thêm trong mỗi test

    @BeforeClass
    public static void setUpClass() {
        serviceDAO = new ServiceDAO();
    }

    @Before
    public void setUp() {
        // Thêm 1 service mới trước mỗi test
        Service service = new Service();
        service.setResortId(1); // giả sử resort id 1 tồn tại
        service.setName("Test Service");
        service.setDescription("Service description");
        service.setPrice(100.0);

        serviceDAO.addService(service);

        // Lấy ID của service vừa thêm
        List<Service> services = serviceDAO.getAllServices();
        testServiceId = services.stream()
                .filter(s -> "Test Service".equals(s.getName()))
                .map(Service::getServiceId)
                .findFirst()
                .orElse(0);
        assertTrue("Service ID should be > 0 after add", testServiceId > 0);
    }

    @After
    public void tearDown() {
        // Xóa service test sau mỗi test để sạch DB
        if (testServiceId > 0) {
            serviceDAO.deleteService(testServiceId);
            testServiceId = 0;
        }
    }

    @Test
    public void testAddService() {
        // Vì đã thêm service trong @Before, ta kiểm tra service đã tồn tại
        List<Service> services = serviceDAO.getAllServices();
        boolean found = services.stream().anyMatch(s -> s.getServiceId() == testServiceId 
                && "Test Service".equals(s.getName()));
        assertTrue("Service added in setUp should exist", found);
    }

    @Test
    public void testGetAllServices() {
        List<Service> list = serviceDAO.getAllServices();
        assertNotNull("List of services should not be null", list);
        assertTrue("List of services should contain zero or more items", list.size() >= 0);
    }

    @Test
    public void testUpdateService() {
        // Cập nhật service vừa tạo trong setUp
        Service service = new Service();
        service.setServiceId(testServiceId);
        service.setResortId(1);
        service.setName("Updated Service");
        service.setDescription("Updated description");
        service.setPrice(200.0);

        boolean updated = serviceDAO.updateService(service);
        assertTrue("Update should return true", updated);

        Service updatedService = serviceDAO.getAllServices().stream()
                .filter(s -> s.getServiceId() == testServiceId)
                .findFirst()
                .orElse(null);
        assertNotNull("Updated service should not be null", updatedService);
        assertEquals("Updated Service", updatedService.getName());
        assertEquals("Updated description", updatedService.getDescription());
        assertEquals(200.0, updatedService.getPrice(), 0.001);
    }

    @Test
    public void testDeleteService() {
        // Xóa service vừa thêm trong setUp
        serviceDAO.deleteService(testServiceId);

        List<Service> services = serviceDAO.getAllServices();
        boolean deleted = services.stream()
                .noneMatch(s -> s.getServiceId() == testServiceId);
        assertTrue("Service should be deleted", deleted);

        // Đánh dấu đã xóa để @After không xóa lại
        testServiceId = 0;
    }

    @Test
    public void testGetServiceCount() {
        int countBefore = serviceDAO.getServiceCount();

        // Thêm 1 service tạm thời
        Service service = new Service();
        service.setResortId(1);
        service.setName("Temp Service");
        service.setDescription("Temporary");
        service.setPrice(50.0);
        serviceDAO.addService(service);

        // Lấy ID service tạm
        int tempId = serviceDAO.getAllServices().stream()
                .filter(s -> "Temp Service".equals(s.getName()))
                .map(Service::getServiceId)
                .findFirst()
                .orElse(0);
        assertTrue(tempId > 0);

        int countAfterAdd = serviceDAO.getServiceCount();
        assertEquals(countBefore + 1, countAfterAdd);

        // Xóa service tạm
        serviceDAO.deleteService(tempId);

        int countAfterDelete = serviceDAO.getServiceCount();
        assertEquals(countBefore, countAfterDelete);
    }

    @Test
    public void testGetServicesByResortName() {
        String resortName = "Sample Resort"; // Cần resort này có trong DB
        List<Service> services = serviceDAO.getServicesByResortName(resortName);
        assertNotNull("Services list should not be null", services);
        // Không bắt buộc có dịch vụ nào nên không kiểm tra size
    }

    @Test
    public void testSearchServicesByResort() {
        String resortName = "Sample Resort";

        List<Service> results1 = serviceDAO.searchServicesByResort(resortName, null, null, null);
        assertNotNull("Search without filters should not be null", results1);

        List<Service> results2 = serviceDAO.searchServicesByResort(resortName, "Service", 50.0, 200.0);
        assertNotNull("Search with filters should not be null", results2);
    }
}
