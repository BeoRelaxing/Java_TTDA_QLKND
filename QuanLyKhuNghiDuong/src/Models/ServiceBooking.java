/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author Beo
 */
public class ServiceBooking {
    private int serviceBookingId;
    private int bookingId;
    private int serviceId;
    private int quantity;
    private double totalPrice;
    
    public ServiceBooking() {
    }

    public ServiceBooking(int serviceBookingId, int bookingId, int serviceId, int quantity, double totalPrice) {
        this.serviceBookingId = serviceBookingId;
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getServiceBookingId() {
        return serviceBookingId;
    }

    public void setServiceBookingId(int serviceBookingId) {
        this.serviceBookingId = serviceBookingId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
