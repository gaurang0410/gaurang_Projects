package model;

public class JobItem {
    private int itemId;
    private int serviceId;
    private String itemType;
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double lineTotal;

    public JobItem(int itemId, int serviceId, String itemType, String itemName, int quantity, double unitPrice, double lineTotal) {
        this.itemId = itemId;
        this.serviceId = serviceId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public int getItemId() { return itemId; }
    public int getServiceId() { return serviceId; }
    public String getItemType() { return itemType; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getLineTotal() { return lineTotal; }
}
