package johnschroeders.marketfree;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;


public class Order implements Parcelable {


    private String orderTitle;
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
    private String productURI;
    private String cancelReason;
    static final String TAG = "OrderStatusActivity";


    private Date dateAccepted;


    private Order(Parcel in) {
        orderID = in.readString();
        producerKey = in.readString();
        customerKey = in.readString();
        productID = in.readString();
        productDescription = in.readString();
        productQuantity = in.readInt();
        orderStatus = in.readString();
        amountPaid = in.readDouble();
        orderTitle = in.readString();
        productURI = in.readString();
        cancelReason = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderID);
        dest.writeString(producerKey);
        dest.writeString(customerKey);
        dest.writeString(productID);
        dest.writeString(productDescription);
        dest.writeInt(productQuantity);
        dest.writeString(orderStatus);
        dest.writeDouble(amountPaid);
        dest.writeString(productURI);
        dest.writeString(cancelReason);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Order() {

    }

    public Date getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public String getProductURI() {
        return productURI;
    }

    public void setProductURI(String productURI) {
        this.productURI = productURI;
    }

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

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

}
