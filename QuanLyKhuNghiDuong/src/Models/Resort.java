package Models;

import java.sql.Timestamp;

public class Resort {
    
    private int resortId;
    private String name;
    private String location;
    private String type;
    private String description;
    private String priceRange;
    private String amenities;
    private Timestamp createdAt;

    // Constructors
    public Resort() {
    }

    public Resort(int resortId, String name, String location, String type, String description, String priceRange, String amenities, Timestamp createdAt) {
        this.resortId = resortId;
        this.name = name;
        this.location = location;
        this.type = type;
        this.description = description;
        this.priceRange = priceRange;
        this.amenities = amenities;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getResortId() { return resortId; }
    public void setResortId(int resortId) { this.resortId = resortId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
