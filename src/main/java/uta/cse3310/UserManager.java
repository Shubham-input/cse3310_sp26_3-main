package uta.cse3310;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    List<User> users;
    List<User> onlineUsers;
    List<User> offlineUsers;
    StorageManager storageManager;

    // Maps connectionId -> User for tracking who is logged in on which connection
    private Map<Integer, User> connectionMap = new HashMap<>();
    private Map<String, List<String>> blockLists = new HashMap<>();

    public UserManager() {
        storageManager = new StorageManager();
        // Load persisted users from file on startup (Constraint #8)
        users       = storageManager.readUsersFromFile();
        onlineUsers = new ArrayList<>();
        offlineUsers = new ArrayList<>(users);
        System.out.println("[UserManager] Initialized with " + users.size() + " user(s) from file.");
    }

    public boolean usernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Top Level Requirement #1: unique handle, max 10 chars
    public boolean createAccount(String username, String password, String confirmPassword) {
        System.out.println("[UserManager] createAccount called with username='" + username + "'");

        if (username == null || password == null || confirmPassword == null) {
            System.out.println("[UserManager] FAIL: null input.");
            return false;
        }

        username        = username.trim();
        password        = password.trim();
        confirmPassword = confirmPassword.trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("[UserManager] FAIL: empty field.");
            return false;
        }

        if (username.length() > 10) {
            System.out.println("[UserManager] FAIL: username too long (" + username.length() + " chars).");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("[UserManager] FAIL: passwords do not match.");
            return false;
        }

        if (usernameExists(username)) {
            System.out.println("[UserManager] FAIL: username '" + username + "' already exists.");
            return false;
        }

        User newUser = new User(username, password);
        users.add(newUser);
        storageManager.appendUserToFile(newUser);
        System.out.println("[UserManager] SUCCESS: user '" + username + "' created.");
        return true;
    }

    // Login and associate the user with their WebSocket connectionId
    public User login(String username, String password, int connectionId) {
        if (username == null || password == null) return null;
        username = username.trim();
        password = password.trim();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                user.setOnlineStatus(true);
                if (!onlineUsers.contains(user)) {
                    onlineUsers.add(user);
                }

                if (offlineUsers.contains(user))
                    offlineUsers.remove(user);

                // Track which connectionId this user is on
                connectionMap.put(connectionId, user);
                return user;
            }
        }
        return null;
    }

    public void logout(User user) {
        if (user != null) {
            user.setOnlineStatus(false);
            onlineUsers.remove(user);
            offlineUsers.add(user);
            // Remove from connectionMap
            connectionMap.values().remove(user);
        }
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Used by onClose() to find which user was on a given connection
    public User getUserByConnectionId(int connectionId) {
        return connectionMap.get(connectionId);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<User> getOnlineUsers() {
        return onlineUsers;
    }

    public List<User> getOfflineUsers() {
        return offlineUsers;
    }

    // obtain names of all current online users as strings for broadcasting
    public List<String> getOnlineUsernames() {
        List<String> names = new ArrayList<>();
        for (User u : onlineUsers) {
            names.add(u.getUsername());
        }
        return names;
    }

    public List<String> getOfflineUsernames() {
        List<String> names = new ArrayList<>();
        for (User u : offlineUsers) {
            names.add(u.getUsername());
       }
       return names;
    }
    public void blockUser(String blocker, String blocked) {
        if (!blockLists.containsKey(blocker)) {
            blockLists.put(blocker, new ArrayList<>());
        }
        blockLists.get(blocker).add(blocked);
    }

    public void unblockUser(String blocker, String blocked) {
        if (blockLists.containsKey(blocker)) {
            blockLists.get(blocker).remove(blocked);
        }
    }

    public boolean isBlocked(String blocker, String blocked) {
        if (!blockLists.containsKey(blocker)) {
            return false;
        }
        return blockLists.get(blocker).contains(blocked);
    }

    public List<String> getBlockList(String username) {
        if (!blockLists.containsKey(username)) {
            return new ArrayList<>();
        }
        return blockLists.get(username);
    }
}
