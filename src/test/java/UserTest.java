//package uta.cse3310; 
//
//import junit.framework.TestCase;
//
//public class UserTest extends TestCase {
//
//    public void testUserConstructorSetsUsername() {
//        User user = new User("alice", "pass123");
//        assertEquals("alice", user.getUsername());
//    }
//
//    public void testUserConstructorSetsPassword() {
//        User user = new User("alice", "pass123");
//        assertEquals("pass123", user.getPassword());
//    }
//
//    public void testUserDefaultOfflineStatus() {
//        User user = new User("alice", "pass123");
//        assertFalse(user.isOnline());
//    }
//
//    public void testUserSetOnlineStatusTrue() {
//        User user = new User("alice", "pass123");
//        user.setOnlineStatus(true);
//        assertTrue(user.isOnline());
//    }
//
//    public void testUserSetOnlineStatusFalse() {
//        User user = new User("alice", "pass123");
//        user.setOnlineStatus(true); // set to true first to ensure the change to false is real
//        user.setOnlineStatus(false);
//        assertFalse(user.isOnline());
//    }
//}