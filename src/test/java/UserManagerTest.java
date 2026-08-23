//package uta.cse3310; 
//
//import junit.framework.TestCase;
//import uta.cse3310.User;
//import uta.cse3310.UserManager;
//
///**
// * JUnit 3 style tests for UserManager
// * @author bjhon
// */
//
//public class UserManagerTest extends TestCase {
//
//    private static final String TMP_FILE = System.getProperty("user.home") + "/group_03_users"; // Must match StorageManager's path
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        // clean file before every test
//        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(TMP_FILE));
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        // clean file after every test
//        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(TMP_FILE));
//    }
//    
//    public void testCreateAccountSuccess() {
//        UserManager um = new UserManager();
//        assertTrue(um.createAccount("alice", "pass123", "pass123"));
//    }
//
//    public void testCreateAccountUsernameTooLong() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("thisistoolong", "pass", "pass"));
//    }
//
//    public void testCreateAccountExactlyTenChars() {
//        UserManager um = new UserManager();
//        assertTrue(um.createAccount("tencharact", "pass", "pass"));
//    }
//
//    public void testCreateAccountPasswordMismatch() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("bob", "pass123", "different"));
//    }
//
//    public void testCreateAccountDuplicateUsername() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        assertFalse(um.createAccount("alice", "newpass", "newpass"));
//    }
//
//    public void testCreateAccountNullUsername() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount(null, "pass", "pass"));
//    }
//
//    public void testCreateAccountNullPassword() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("alice", null, "pass"));
//    }
//
//    public void testCreateAccountNullConfirmPassword() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("alice", "pass", null));
//    }
//
//    public void testCreateAccountEmptyUsername() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("", "pass", "pass"));
//    }
//
//    public void testCreateAccountEmptyPassword() {
//        UserManager um = new UserManager();
//        assertFalse(um.createAccount("alice", "", ""));
//    }
//
//    public void testUsernameExistsTrue() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass", "pass");
//        assertTrue(um.usernameExists("alice"));
//    }
//
//    public void testUsernameExistsFalse() {
//        UserManager um = new UserManager();
//        assertFalse(um.usernameExists("ghost"));
//    }
//
//    public void testLoginSuccess() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        User user = um.login("alice", "pass123", 0);
//        assertNotNull(user);
//        assertEquals("alice", user.getUsername());
//    }
//
//    public void testLoginSetsOnlineStatus() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        User user = um.login("alice", "pass123", 0);
//        assertNotNull(user);    
//        assertTrue(user.isOnline());
//    }
//
//    public void testLoginAddsToOnlineList() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        um.login("alice", "pass123", 0);
//        assertEquals(1, um.getOnlineUsers().size());
//    }
//
//    public void testLoginWrongPassword() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        assertNull(um.login("alice", "wrongpass", 0));
//    }
//
//    public void testLoginUnknownUser() {
//        UserManager um = new UserManager();
//        assertNull(um.login("ghost", "pass123", 0));
//    }
//
//    public void testLoginNullUsername() {
//        UserManager um = new UserManager();
//        assertNull(um.login(null, "pass123", 0));
//    }
//
//    public void testLoginNullPassword() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        assertNull(um.login("alice", null, 0));
//    }
//
//    public void testLogoutSetsOffline() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        User user = um.login("alice", "pass123", 0);
//        um.logout(user);
//        assertFalse(user.isOnline());
//    }
//
//    public void testLogoutRemovesFromOnlineList() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass123", "pass123");
//        User user = um.login("alice", "pass123", 0);
//        um.logout(user);
//        assertFalse(um.getOnlineUsers().contains(user));
//    }
//
//    public void testLogoutNullUserDoesNotThrow() {
//        UserManager um = new UserManager();
//        try {
//            um.logout(null);
//        } catch (Exception e) {
//            fail("logout(null) should not throw an exception");
//        }
//    }
//
//    public void testGetUsersReturnsAllRegistered() {
//        UserManager um = new UserManager();
//        um.createAccount("alice", "pass", "pass");
//        um.createAccount("bob", "pass", "pass");
//        assertEquals(2, um.getUsers().size());
//    }
//
//    public void testGetOnlineUsersEmptyBeforeLogin() {
//        UserManager um = new UserManager();
//        assertEquals(0, um.getOnlineUsers().size());
//    }
//}