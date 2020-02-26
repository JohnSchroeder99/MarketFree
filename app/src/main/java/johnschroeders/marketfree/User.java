package johnschroeders.marketfree;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String customerKey;
    private String userName;
    private String profileImageURL;
    private ArrayList<String> subscribedTo;
    private ArrayList<String> conversationsKeys;


    private String googleID;

    public User() {


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerKey);
        dest.writeString(userName);
        dest.writeString(profileImageURL);
        dest.writeString(googleID);
    }

    private User(Parcel in) {
        customerKey = in.readString();
        userName = in.readString();
        profileImageURL = in.readString();
        googleID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerKey() {
        return this.customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getProfileImageURL() {
        return this.profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public ArrayList<String> getSubscribedTo() {
        return subscribedTo;
    }

    public void setSubscribedTo(ArrayList<String> subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    public ArrayList<String> getConversationsKeys() {
        return conversationsKeys;
    }

    public void setConversationsKeys(ArrayList<String> conversationsKeys) {
        this.conversationsKeys = conversationsKeys;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }


}
