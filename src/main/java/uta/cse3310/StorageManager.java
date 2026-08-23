package uta.cse3310;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class StorageManager {

    // Constraint #8: persistent data stored outside the repo dir so it survives
    // deployments (which rm -rf the repo). Message data stored in /tmp/group_03_messages
    private static final String DATA_DIR = System.getProperty("user.home");
    private static final String FILE_PATH = DATA_DIR + "/group_03_users";
    private static final String MESSAGES_FILE_PATH = "/tmp/group_03_messages";
    private List<Post> posts = new ArrayList<>();
    private int postIdCounter = 0;

    public List<User> readUsersFromFile() {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        System.out.println("[StorageManager] Reading from: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("[StorageManager] File does not exist yet, returning empty list.");
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    users.add(new User(username, password));
                }
            }
            System.out.println("[StorageManager] Loaded " + users.size() + " user(s).");
        } catch (IOException e) {
            System.err.println("[StorageManager] Error reading users: " + e.getMessage());
        }

        return users;
    }

    public void writeUsersToFile(List<User> users) {
        System.out.println("[StorageManager] Writing " + users.size() + " user(s).");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
            System.out.println("[StorageManager] Write successful.");
        } catch (IOException e) {
            System.err.println("[StorageManager] Error writing users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void appendUserToFile(User user) {
        System.out.println("[StorageManager] Appending user '" + user.getUsername() + "'.");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.getUsername() + "," + user.getPassword());
            writer.newLine();

            System.out.println("[StorageManager] Append successful.");
        } catch (IOException e) {
            System.err.println("[StorageManager] Error appending user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void appendMessageToFile(String author, String timestamp, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGES_FILE_PATH, true))) {
            writer.write(author + "," + timestamp + "," + message.replace("\n", " ") + ",0,0,0");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending message: " + e.getMessage());
        }
        Post post = new Post(postIdCounter++, author, message, timestamp);
	    posts.add(post);
    }

    // rewrite the file with new reaction count
    public void writeAllMessagesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGES_FILE_PATH))) {
            for (Post post : posts) {
                writer.write(post.getAuthor() + "," + post.getTimestamp() + "," + post.getContent().replace("\n", " ") + "," + post.getLikeCount() + "," + post.getHappyCount() + "," + post.getSadCount());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing messages: " + e.getMessage());
        }
    }



    public List<Post> readMessagesFromFile() {
        posts.clear();        // Clear memory to avoid duplicates
        postIdCounter = 0;    // Reset IDs so they start at 0 every boot
        List<Post> messages = new ArrayList<>();
        File file = new File(MESSAGES_FILE_PATH);

        if (!file.exists())
            return messages;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Inside the while loop
                String[] parts = line.split(",", 6); 
                if (parts.length >= 3) {
                    try {
                        String author = parts[0].trim();
                        String time = parts[1].trim();
                        String content = parts[2];

                        Post post = new Post(postIdCounter++, author, content, time);
                        
                        if (parts.length == 6) {
                            post.setLikeCount(Integer.parseInt(parts[3].trim()));
                            post.setHappyCount(Integer.parseInt(parts[4].trim()));
                            post.setSadCount(Integer.parseInt(parts[5].trim()));
                        }

                        messages.add(post);
                        posts.add(post);
                    } catch (Exception parseEx) {
                        System.err.println("Skipping malformed line: " + line);
                }
            }
        }
        }catch(IOException e)
    {
        System.err.println("Error reading messages: " + e.getMessage());
    }
    return messages;
    }

    public Post getPostById(int id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }

    public void updatePost(Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId() == post.getId()) {
                posts.set(i, post);
                break;
            }
        }
    }
    public int getLastPostId() {
	return postIdCounter - 1;
    }
}
