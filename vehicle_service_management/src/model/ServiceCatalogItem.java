package model;

public class ServiceCatalogItem {
    private int catalogId;
    private String serviceName;
    private String description;
    private double baseCost;
    private String estimatedTime;
    private String category;

    public ServiceCatalogItem() {}

    public ServiceCatalogItem(int catalogId, String serviceName, String description, double baseCost, String estimatedTime, String category) {
        this.catalogId = catalogId;
        this.serviceName = serviceName;
        this.description = description;
        this.baseCost = baseCost;
        this.estimatedTime = estimatedTime;
        this.category = category;
    }

    public int getCatalogId() { return catalogId; }
    public void setCatalogId(int catalogId) { this.catalogId = catalogId; }
    
    public int getServiceId() { return catalogId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }

    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return serviceName;
    }
}
