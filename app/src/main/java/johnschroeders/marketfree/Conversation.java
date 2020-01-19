package johnschroeders.marketfree;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Conversation implements Parcelable {
    public String conversationKey;
    public String associatedProductID;
    public String associatedProductTitle;
    public String associatedProductImage;
    public String inquiringCustomerUniqueKey;
    public String productOwnerUniqueKey;
    public Date conversationStartedDate;

    public Conversation(){


    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }

    public String getAssociatedProductID() {
        return associatedProductID;
    }

    public void setAssociatedProductID(String associatedProductID) {
        this.associatedProductID = associatedProductID;
    }

    public String getInquiringCustomerUniqueKey() {
        return inquiringCustomerUniqueKey;
    }

    public void setInquiringCustomerUniqueKey(String inquiringCustomerUniqueKey) {
        this.inquiringCustomerUniqueKey = inquiringCustomerUniqueKey;
    }

    public String getProductOwnerUniqueKey() {
        return productOwnerUniqueKey;
    }

    public void setProductOwnerUniqueKey(String productOwnerUniqueKey) {
        this.productOwnerUniqueKey = productOwnerUniqueKey;
    }

    public Date getConversationStartedDate() {
        return conversationStartedDate;
    }

    public void setConversationStartedDate(Date conversationStartedDate) {
        this.conversationStartedDate = conversationStartedDate;
    }

    protected Conversation(Parcel in) {
        conversationKey = in.readString();
        associatedProductID = in.readString();
        inquiringCustomerUniqueKey = in.readString();
        productOwnerUniqueKey = in.readString();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    public String getAssociatedProductTitle() {
        return associatedProductTitle;
    }

    public void setAssociatedProductTitle(String associatedProductTitle) {
        this.associatedProductTitle = associatedProductTitle;
    }

    public String getAssociatedProductImage() {
        return associatedProductImage;
    }

    public void setAssociatedProductImage(String associatedProductImage) {
        this.associatedProductImage = associatedProductImage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationKey);
        dest.writeString(associatedProductID);
        dest.writeString(inquiringCustomerUniqueKey);
        dest.writeString(productOwnerUniqueKey);
    }
}
