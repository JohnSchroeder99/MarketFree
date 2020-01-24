package johnschroeders.marketfree;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    private String messageID;
    private String messageContent;
    private String messageFromCustomerKey;
    private String messageToCustomerKey;
    private String messageFromEmail;
    private String dateSent;
    private boolean hasPreviousMessage;
    private String previousMessageSentID;
    private String associatedProductID;
    private String associatedProductDescription;
    private String associatedProductTitle;
    private String associatedProductImageURL;


    public Message(Parcel in) {
        messageID = in.readString();
        messageContent = in.readString();
        messageFromCustomerKey = in.readString();
        messageToCustomerKey = in.readString();
        messageFromEmail = in.readString();
        previousMessageSentID = in.readString();
        dateSent = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasPreviousMessage = in.readBoolean();
        }
    }

    public Message() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageID);
        dest.writeString(messageContent);
        dest.writeString(messageFromCustomerKey);
        dest.writeString(messageToCustomerKey);
        dest.writeString(messageFromEmail);
        dest.writeString(previousMessageSentID);
        dest.writeString(dateSent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(hasPreviousMessage);
        }
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageFromCustomerKey() {
        return messageFromCustomerKey;
    }

    public void setMessageFromCustomerKey(String messageFromCustomerKey) {
        this.messageFromCustomerKey = messageFromCustomerKey;
    }

    public String getMessageToCustomerKey() {
        return messageToCustomerKey;
    }

    public void setMessageToCustomerKey(String messageToCustomerKey) {
        this.messageToCustomerKey = messageToCustomerKey;
    }

    public String getMessageFromEmail() {
        return messageFromEmail;
    }

    public void setMessageFromEmail(String messageFromEmail) {
        this.messageFromEmail = messageFromEmail;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getPreviousMessageSentID() {
        return previousMessageSentID;
    }

    public void setPreviousMessageSentID(String previousMessageSentID) {
        this.previousMessageSentID = previousMessageSentID;
    }

    public boolean isHasPreviousMessage() {
        return hasPreviousMessage;
    }

    public void setHasPreviousMessage(boolean hasPreviousMessage) {
        this.hasPreviousMessage = hasPreviousMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getAssociatedProductID() {
        return associatedProductID;
    }

    public void setAssociatedProductID(String associatedProductID) {
        this.associatedProductID = associatedProductID;
    }

    public String getAssociatedProductDescription() {
        return associatedProductDescription;
    }

    public void setAssociatedProductDescription(String associatedProductDescription) {
        this.associatedProductDescription = associatedProductDescription;
    }

    public String getAssociatedProductTitle() {
        return associatedProductTitle;
    }

    public void setAssociatedProductTitle(String associatedProductTitle) {
        this.associatedProductTitle = associatedProductTitle;
    }

    public String getAssociatedProductImageURL() {
        return associatedProductImageURL;
    }

    public void setAssociatedProductImageURL(String associatedProductImageURL) {
        this.associatedProductImageURL = associatedProductImageURL;
    }


}
