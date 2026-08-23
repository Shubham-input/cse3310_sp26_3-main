// This is example code provided to CSE3310 Spring 2026.
// You are free to use as is, or changed, any of the code provided

// Please comply with the licensing requirements for the
// open source packages being used.

// This code is based upon, and derived from the this repository
//            https:/thub.com/TooTallNate/Java-WebSocket/tree/master/src/main/example

// http server include is a GPL licensed package from
//            http://www.freeutils.net/source/jlhttp/

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package uta.cse3310;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.time.Instant;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class App extends WebSocketServer {

  // Message classes
  //////////////////////////////////////////////////
  private class idMsg {
    String msgType = "idMsg";
    int connectionId;

    private idMsg(int c) {
      connectionId = c;
    }
  }

  private class displayMsg {
    String msgType = "displayMsg";
    boolean displayName;
    boolean displayMain;
    boolean displaySend;
  }

  private class nameMsg {
    String msgType = "nameMsg";
    String name;
  }

  private class sendMsg {
    String msgType = "sendMsg";
    String msg;
  }

  private class sendToAllMsg {
    String msgType = "sendToAllMsg";
    String msg;
  }

  private class onlineUsersMsg {
    String msgType = "onlineUsersMsg";
    String[] users;
  }

  private class offlineUsersMsg {
    String msgType = "offlineUsersMsg";
    String[] users;
  }

  // Added: signup and login message classes (Constraint #2 - all comms via
  // WebSocket)
  private class signupMsg {
    String msgType = "signupMsg";
    String username;
    String password;
    String confirmPassword;
  }

  private class loginMsg {
    String msgType = "loginMsg";
    String username;
    String password;
  }

  private class authMsg {
    String msgType = "authMsg";
    String action;
    String status;
    String message;

    private authMsg(String action, String status, String message) {
      this.action = action;
      this.status = status;
      this.message = message;
    }
  }

  // Added: posts with usernames (for frontend postMsg handling)
  private class postMsg {
    String msgType = "postMsg";
    String username;
    String msg;
    String timestamp;

    int likes;
    int happy;
    int sad;
    int postId;
  }

  private class blockMsg {
    String msgType;
    String target;
  }

  private class blockListMsg {
    String msgType = "blockListMsg";
    String[] blockedUsers;
  }

  private class reactMsg {
    String msgType = "reactMsg";
    int postId;
    String reactionType;
  }

  private class updateReactionMsg {
    String msgType = "updateReactionMsg";
    int postId;
    int likes;
    int happy;
    int sad;
  }

  private int connectionId = 0;

  // Added: map each WebSocket connection to its assigned ID
  private Map<WebSocket, Integer> connectionIds = new HashMap<>();

  // Added: UserManager handles signup, login, logout, and persistence
  private UserManager userManager = new UserManager();

  private Instant startTime;
  private Statistics stats;

  public App(int port) {
    super(new InetSocketAddress(port));
  }

  public App(InetSocketAddress address) {
    super(address);
  }

  public App(int port, Draft_6455 draft) {
    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
  }

  // for broadcasting online users list
  private void broadcastOnlineUsers() {
    onlineUsersMsg msg = new onlineUsersMsg();

    msg.users = userManager.getOnlineUsernames().toArray(new String[0]);
    Gson gson = new Gson();

    broadcast(gson.toJson(msg));
  }

  // for broadcrasting offline users list
  private void broadcastOfflineUsers() {
    offlineUsersMsg msg = new offlineUsersMsg();
    msg.users = userManager.getOfflineUsernames().toArray(new String[0]);
    Gson gson = new Gson();
    broadcast(gson.toJson(msg));
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    connectionId++;

    // Added: track this connection's ID
    connectionIds.put(conn, connectionId);

    System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");

    Gson gson = new Gson();

    // Send client their unique ID
    idMsg E = new idMsg(connectionId);
    String jsonString = gson.toJson(E);
    conn.send(jsonString);

    // Show signup/login screen first
    displayMsg dm = new displayMsg();
    dm.displayMain = false;
    dm.displayName = true;
    dm.displaySend = false;

    jsonString = gson.toJson(dm);
    conn.send(jsonString);

    System.out.println("> " + Duration.between(startTime, Instant.now()).toMillis()
        + " " + connectionId + " " + escape(jsonString));

    stats.setRunningTime(Duration.between(startTime, Instant.now()).toSeconds());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println(conn + " has closed");

    // Added: log the user out and clean up their connection entry
    int closedId = connectionIds.getOrDefault(conn, -1);
    User user = userManager.getUserByConnectionId(closedId);
    if (user != null) {
      userManager.logout(user);
      broadcastOnlineUsers();
      broadcastOfflineUsers();
    }
    connectionIds.remove(conn);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    System.out.println("< " + Duration.between(startTime, Instant.now()).toMillis()
        + " " + "-" + " " + escape(message));
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    JsonObject obj = gson.fromJson(message, JsonObject.class);
    String msgType = obj.get("msgType").getAsString();
    System.out.println("< " + Duration.between(startTime, Instant.now()).toMillis()
        + " " + "-" + " " + escape(message));
    String reply = "";

    if (message.contains("reactionMsg")) {
      System.out.println("REACTION MSG RECEIVED: " + message);

      reactionMsg R = gson.fromJson(message, reactionMsg.class);

      broadcast(gson.toJson(R));
      return;
    }

    if (message.contains("msgType")) {

      // Added: handle signup via WebSocket (Constraint #2)
      if (message.contains("signupMsg")) {
        signupMsg S = gson.fromJson(message, signupMsg.class);
        System.out.println("SIGNUP RECEIVED for username: " + S.username);

        boolean created = userManager.createAccount(S.username, S.password, S.confirmPassword);
        System.out.println("SIGNUP RESULT created = " + created);

        authMsg response;
        if (created) {
          response = new authMsg("signup", "success", "Account created successfully");
        } else {
          response = new authMsg("signup", "error",
              "Signup failed: username exists, passwords do not match, or username too long");
        }
        conn.send(gson.toJson(response));
        reply = created ? S.username + " signed up" : "signup FAIL";
        return;

        // Added: handle login via WebSocket (Constraint #2)
      } else if (message.contains("loginMsg")) {
        loginMsg L = gson.fromJson(message, loginMsg.class);
        int myId = connectionIds.getOrDefault(conn, -1);
        User user = userManager.login(L.username, L.password, myId);
        authMsg response;
        if (user != null) {
          response = new authMsg("login", "success", "login SUCCESS");
          // Show main chat now that user is logged in
          displayMsg dm = new displayMsg();
          dm.displayMain = true;
          dm.displayName = false;
          dm.displaySend = true;

          List<Post> history = userManager.storageManager.readMessagesFromFile();
          for (Post p : history) {
            postMsg H = new postMsg();

            // SEND AUTHOR, CONTENT, AND TIMESTAMP
            H.username = p.getAuthor();
            H.msg = p.getContent();
            H.timestamp = p.getTimestamp().toString();

            // SEND REACT COUNTS
            H.likes = p.getLikeCount();
            H.happy = p.getHappyCount();
            H.sad = p.getSadCount();
            H.postId = p.getId();
            conn.send(gson.toJson(H));
          }

          conn.send(gson.toJson(dm));
          reply = L.username + " has joined";
          broadcastOnlineUsers();
          broadcastOfflineUsers();
        } else {
          response = new authMsg("login", "error", "invalid username/password");
          reply = "login FAIL for " + L.username;
        }
        conn.send(gson.toJson(response));

      } else if (message.contains("logoutMsg")) {
        int myid = connectionIds.getOrDefault(conn, -1);
        User user = userManager.getUserByConnectionId(myid);

        if (user != null) {
          userManager.logout(user);
          broadcastOnlineUsers();
          broadcastOfflineUsers();
        }

        // sending the user back to the login screen
        displayMsg dm = new displayMsg();
        dm.displayMain = false;
        dm.displayName = true;
        dm.displaySend = false;

        String jsonString = gson.toJson(dm);
        conn.send(jsonString);

        return;
      } else if (message.contains("nameMsg")) {
        System.out.println("nameMsg found in message");
        nameMsg N = gson.fromJson(message, nameMsg.class);
        System.out.println("name: " + N.name);
        reply = N.name + " has joined";
        displayMsg dm = new displayMsg();
        dm.displayMain = true;
        dm.displayName = false;
        dm.displaySend = true;
        String jsonString = gson.toJson(dm);
        conn.send(jsonString);

      } else if (message.contains("sendMsg")) {
        sendMsg S = gson.fromJson(message, sendMsg.class);
        int myId = connectionIds.getOrDefault(conn, -1);
        User user = userManager.getUserByConnectionId(myId);

        // prevent users who do not have an account from logging in
        if (user == null) {
          authMsg response = new authMsg("send", "error", "you don't have an account!! why not make one? :]");
          conn.send(gson.toJson(response));
          return;
        }

        System.out.println("msg: " + S.msg);

        // Enforce 256 Unicode codepoint limit
        String msg = S.msg;
        if (msg != null && msg.codePointCount(0, msg.length()) > 256) {
          msg = msg.substring(0, msg.offsetByCodePoints(0, 256));
        }

        // Check if message is empty or only whitespace
        if (msg == null || msg.trim().isEmpty()) {
          authMsg response = new authMsg("send", "error", "Cannot post empty messages");
          conn.send(gson.toJson(response));
          return;
        }

        reply = msg;

        postMsg P = new postMsg();
        P.username = user.username;
        P.msg = reply;
       
      P.timestamp = java.time.Instant.now().toString();
       
        // Save the timestamp to the file
        userManager.storageManager.appendMessageToFile(user.username, P.timestamp, reply);

        P.postId = userManager.storageManager.getLastPostId();
        P.likes = 0;
        P.happy = 0;
        P.sad = 0;

        String postJson = gson.toJson(P);

        // FIX: The loop header was missing!
        for (WebSocket client : getConnections()) {
          int recipientId = connectionIds.getOrDefault(client, -1);
          User recipient = userManager.getUserByConnectionId(recipientId);

          // Check if the recipient has blocked the sender
          if (recipient != null && userManager.isBlocked(user.username, recipient.username)) {
            continue;
          }
          client.send(postJson);
        } // FIX: Closing brace for the loop was missing

        stats.setRunningTime(Duration.between(startTime, Instant.now()).toSeconds());

        System.out.println("> " + Duration.between(startTime, Instant.now()).toMillis()
            + " " + "*" + " " + escape(postJson));
        return;
      } else if (message.contains("reactMsg")) {
        reactMsg R = gson.fromJson(message, reactMsg.class);
        int myId = connectionIds.getOrDefault(conn, -1);
        User user = userManager.getUserByConnectionId(myId);
        if (user == null)
          return;

        Post post = userManager.storageManager.getPostById(R.postId);
        if (post != null) {
          switch (R.reactionType) {
            case "likes":
              post.addLike(user.username); // Changed from incrementLikes()
              break;
            case "happy":
              post.addHappy(user.username); // Changed from incrementHappy()
              break;
            case "sad":
              post.addSad(user.username); // Changed from incrementSad()
              break;
          }
          userManager.storageManager.updatePost(post);
          userManager.storageManager.writeAllMessagesToFile();

          // Broadcast to all users
          updateReactionMsg update = new updateReactionMsg();
          update.postId = R.postId;
          update.likes = post.getLikeCount(); // Changed from getLikes()
          update.happy = post.getHappyCount(); // Changed from getHappy()
          update.sad = post.getSadCount(); // Changed from getSad()
          String updateJson = gson.toJson(update);
          for (WebSocket client : getConnections()) {
            client.send(updateJson);
          }
        }
        return;

      } // BLOCK TYPE MESSAGES
      else if (message.contains("blockMsg") || message.contains("unblockMsg")) {
        blockMsg B = gson.fromJson(message, blockMsg.class);
        int myId = connectionIds.getOrDefault(conn, -1);
        User user = userManager.getUserByConnectionId(myId);
        if (user == null)
          return;

        if (B.msgType.equals("blockMsg")) {
          userManager.blockUser(user.username, B.target);
        } else {
          userManager.unblockUser(user.username, B.target);
        }

        // notification to indicate blocking list change
        List<String> blList = userManager.getBlockList(user.username);
        blockListMsg bl = new blockListMsg();
        bl.blockedUsers = blList.toArray(new String[0]);
        conn.send(gson.toJson(bl));
        
      } else {
        System.out.println("unhandled msg: " + message);
        reply = "msg incorrect format: " + message;
      }

    } else {
      System.out.println("unhandled msg: " + message);
      reply = "msg incorrect format: " + message;
    }

    sendToAllMsg msg = new sendToAllMsg();
    msg.msg = reply;

    String jsonString = gson.toJson(msg);
    broadcast(jsonString);

    stats.setRunningTime(Duration.between(startTime, Instant.now()).toSeconds());

    System.out.println("> " + Duration.between(startTime, Instant.now()).toMillis()
        + " " + "*" + " " + escape(jsonString));
  }

  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    System.out.println(conn + ": " + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
    if (conn != null) {
    }
  }

  @Override
  public void onStart() {
    setConnectionLostTimeout(0);
    stats = new Statistics();
    startTime = Instant.now();
  }

  private String escape(String S) {
    String retval = new String();
    for (int i = 0; i < S.length(); i++) {
      Character ch = S.charAt(i);
      if (ch == '\"') {
        retval = retval + '\\';
      }
      retval = retval + ch;
    }
    return retval;
  }

  public static void main(String[] args) {

    // Constraint #9: HTTP port = 9030
    int port = 9030;
    String httpEnv = System.getenv("HTTP_PORT");
    if (httpEnv != null) {
      port = Integer.parseInt(httpEnv);
    }

    // Constraint #10: WebSocket port = 9031
    int wsPort = 9031;
    String wsEnv = System.getenv("WEBSOCKET_PORT");
    if (wsEnv != null) {
      wsPort = Integer.parseInt(wsEnv);
    }

    HttpServer H = new HttpServer(port, "./html");
    H.start();
    System.out.println("http Server started on port: " + port);

    App A = new App(wsPort);
    A.setReuseAddr(true);
    A.start();
    System.out.println("websocket Server started on port: " + wsPort);
  }

  class reactionMsg {
    String msgType = "reactionMsg";
    String reaction;
    String msg;
  }
}
