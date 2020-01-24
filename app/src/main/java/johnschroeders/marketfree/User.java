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

    public User() {


    }

    private User(Parcel in) {
        customerKey = in.readString();
        userName = in.readString();
        profileImageURL = in.readString();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerKey);
        dest.writeString(userName);
        dest.writeString(profileImageURL);
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

}
