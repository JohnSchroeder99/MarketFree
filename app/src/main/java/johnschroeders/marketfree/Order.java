package johnschroeders.marketfree;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

//TODO handle this error for implementing parcelable
public class Order implements Parcelable {

    private String orderID;
    private String producerKey;
    private String customerKey;
    private String productID;
    private String productDescription;
    private int productQuantity;
    private String orderStatus;
    private Date dateOrdered;
    private Date dateDelivered;
    private Date dateCanceled;
    private double amountPaid;
    static final String TAG = "OrderStatusActivity";




    //TODO return this from the firestore according to orderID
    public String getOrderID() {
        return orderID;
    }

    public String getProductDescription() {
        return productDescription;
    }

    void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }


    void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    void setProducerKey(String producerKey) {
        this.producerKey = producerKey;
    }

    void setCustomerKey(String customerKey) {
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

    void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    void setAmountPaid(double amountPaid) {
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

    public Date getDateCanceled() {
        return dateCanceled;
    }

    void setDateCanceled(Date dateCanceled) {
        this.dateCanceled = dateCanceled;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
