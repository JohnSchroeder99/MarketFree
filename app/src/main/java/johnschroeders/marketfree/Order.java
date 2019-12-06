package johnschroeders.marketfree;



import android.util.Log;

import java.util.Date;
import java.util.HashMap;

public class Order {

    private String orderID;
    private String producerKey;
    private String customerKey;
    private String productID;
    private String orderStatus;
    private HashMap<String, Integer> orderDescriptionAndQuantity;
    private Date dateOrdered;
    private Date dateDelivered;
    private Date dateCanceled;
    private double amountPaid;

    public void setOrderDescriptionAndQuantity(HashMap<String, Integer> orderDescriptionAndQuantity) {
        this.orderDescriptionAndQuantity = orderDescriptionAndQuantity;
        Log.d("Order", "new order description and quantity");

    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public HashMap<String, Integer> getOrderDescriptionAndQuantity() {
        Log.d("Order", "order description and quantity requested");
        return orderDescriptionAndQuantity;
    }

    public void putOrderDescriptionAndQuantity(String description, Integer amount) {
        this.orderDescriptionAndQuantity.put(description, amount);
    }

    public void removeOrderDescriptionAndQuantity(String description) {
        this.orderDescriptionAndQuantity.remove(description);
    }


    public void setProducerKey(String producerKey) {
        this.producerKey = producerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }


    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getProducerKey() {
        return producerKey;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public String getProductID() {
        return productID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public Date getDateDelivered() {
        return dateDelivered;
    }

    public double getAmountPaid() {
        return amountPaid;
    }


}
