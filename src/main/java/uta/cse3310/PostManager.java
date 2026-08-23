package uta.cse3310;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostManager {
  // list containing all posts
  private List<Post> posts;
  private int idCounter = 0; // to assign unique IDs to posts

  public PostManager() {
      posts = new ArrayList<>();
  }

  public void addPost(String author, String content) {
      posts.add(new Post(idCounter++,author, content));
  }

  

  public List<Post> getPosts() {
      return posts;
  }
  public void addReaction(Post post, String reaction) {
    if (post == null || reaction == null) return;
      switch (reaction.toLowerCase()) {
        case "like":
          post.addLike("system");
          break;
        case "happy":
          post.addHappy("system");
          break;
        case "sad":
          post.addSad("system");
          break;
        default:
          // ignore invalid reactions
      }
  }

  // returns posts whose content contains the keyword (case-insensitive)
  public List<Post> searchPosts(String keyword) {
      if (keyword == null || keyword.isEmpty()) return new ArrayList<>();

      List<Post> results = new ArrayList<>();
      String lower = keyword.toLowerCase();
      for (Post p : posts) {
          if (p.post.toLowerCase().contains(lower)) {
              results.add(p);
          }
      }
      return results;
  }

  // returns posts whose author matches the username (case-insensitive)
  public List<Post> searchByUsername(String username) {
      if (username == null || username.isEmpty()) return new ArrayList<>();

      List<Post> results = new ArrayList<>();
      String lower = username.toLowerCase();
      for (Post p : posts) {
          if (p.author.toLowerCase().contains(lower)) {
              results.add(p);
          }
      }
      return results;
  }

  // returns posts sorted by like count, highest first
  public List<Post> sortByMostLiked() {
      List<Post> sorted = new ArrayList<>(posts);
      sorted.sort(Comparator.comparingInt(Post::getLikeCount).reversed());
      return sorted;
  }

  // returns posts sorted by timestamp, newest first
  public List<Post> sortByMostRecent() {
      List<Post> sorted = new ArrayList<>(posts);
      sorted.sort(Comparator.comparing(Post::getTimestamp).reversed());
      return sorted;
  }

}
