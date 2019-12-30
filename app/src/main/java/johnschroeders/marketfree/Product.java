package johnschroeders.marketfree;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;


public class Product implements Parcelable {

    private String productID;
    private String customerKey;
    private String productDescription;
    private int quantity;
    private double cost;
    private Date dateCreated;
    private String productTitle;
    private String uri;

    public Product(Parcel in) {
        productID = in.readString();
        customerKey = in.readString();
        productDescription = in.readString();
        quantity = in.readInt();
        uri = in.readString();
    }

    public Product() {

    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productID);
        dest.writeString(customerKey);
        dest.writeString(productDescription);
        dest.writeInt(quantity);
        dest.writeString(uri);
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getProductTitle() {

        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
