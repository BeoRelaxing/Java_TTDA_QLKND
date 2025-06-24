/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author Beo
 */
public class Service {
    private int serviceId;
    private int resortId;
    private String name;
    private String description;
    private double price;
    
    public Service() {
    // Constructor rỗng để tiện cho DAO load dữ liệu từ ResultSet
    }
    // Constructor
    public Service(int serviceId, int resortId, String name, String description, double price) {
        this.serviceId = serviceId;
        this.resortId = resortId;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    
    // Getter and Setter
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getResortId() {
        return resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
