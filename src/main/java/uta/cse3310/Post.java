package uta.cse3310;

import java.time.LocalDateTime;

// every post has an author and a message of up to 256 characters
// POST-XX In requirements 
public class Post {
	int id;
	String author;
	String post;
	String timestamp;

	// reaction counters 
	int likeCount;
	int happyCount;
	int sadCount;
	
    // lists to track which users have reacted to this post
    java.util.List<String> likedBy = new java.util.ArrayList<>();
    java.util.List<String> happyBy = new java.util.ArrayList<>();
    java.util.List<String> sadBy = new java.util.ArrayList<>();

	public Post(int id, String author, String post) {
        this.id = id;
        this.author = author;
        this.post = post;
        this.likeCount = 0;
        this.happyCount = 0;
        this.sadCount = 0;
}

public Post(int id, String author, String post, String timestamp) {
        this.id = id;
        this.author = author;
        this.post = post;
        this.timestamp = timestamp;
        this.likeCount = 0;
        this.happyCount = 0;
        this.sadCount = 0;
    }

	public int getId() {
        return id;
    }

	public String getAuthor() {
		return author;
	}

	public String getContent() {
		return post;
	}

	public String getTimestamp() {
		return timestamp;
	}

	// if user already reacted, remove their reaction; otherwise add it
    public void addLike(String username) {
        if (likedBy.contains(username)) {
            likedBy.remove(username);
            likeCount--;
        } else {
            likedBy.add(username);
            likeCount++;
        }
    }

    public void addHappy(String username) {
        if (happyBy.contains(username)) {
            happyBy.remove(username);
            happyCount--;
        } else {
            happyBy.add(username);
            happyCount++;
        }
    }

    public void addSad(String username) {
        if (sadBy.contains(username)) {
            sadBy.remove(username);
            sadCount--;
        } else {
            sadBy.add(username);
            sadCount++;
        }
    }

    public int getLikeCount() { return likeCount; }
    public int getHappyCount() { return happyCount; }
    public int getSadCount() { return sadCount; }

    public void setLikeCount(int count) { likeCount = count; }
    public void setHappyCount(int count) { happyCount = count; }
    public void setSadCount(int count) { sadCount = count; }
}
