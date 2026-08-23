package uta.cse3310;

import java.util.ArrayList;
import java.util.List;

// for user information
public class User {
    String username;
    String password;
    boolean onlineStatus;
    private List<String> blockedUsers;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.onlineStatus = false;
        this.blockedUsers = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void blockUser(String username) {
        if (username == null || username.equals(this.username)) return;
        if (!blockedUsers.contains(username)) {
            blockedUsers.add(username);
        }
    }

    public void unblockUser(String username) {
        blockedUsers.remove(username);
    }

    public boolean isBlocked(String username) {
        return blockedUsers.contains(username);
    }
}

