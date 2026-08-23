//package uta.cse3310; 
//
//import junit.framework.TestCase;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//
///**
// * JUnit 3 style tests for StorageManager
// * @author bjhon
// */
//
//public class StorageManagerTest extends TestCase {
//
//    private static final String TMP_FILE = System.getProperty("user.home") + "/group_03_users";
//
//    public StorageManagerTest(String testName) {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        // clean file before every test
//        Files.deleteIfExists(Paths.get(TMP_FILE));
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        // clean file after every test
//        Files.deleteIfExists(Paths.get(TMP_FILE));
//    }
//    
//    public void testReadUsersFromFileEmptyWhenNoFile() {
//        StorageManager sm = new StorageManager();
//        List<User> users = sm.readUsersFromFile();
//        assertTrue(users.isEmpty());
//    }
//
//    public void testWriteAndReadUsers() {
//        StorageManager sm = new StorageManager();
//        sm.appendUserToFile(new User("alice", "pass1"));
//        sm.appendUserToFile(new User("bob", "pass2"));
//
//        List<User> users = sm.readUsersFromFile();
//        assertEquals(2, users.size());
//        assertEquals("alice", users.get(0).getUsername());
//        assertEquals("pass1", users.get(0).getPassword());
//        assertEquals("bob", users.get(1).getUsername());
//    }
//
//    public void testAppendUserToFile() {
//        StorageManager sm = new StorageManager();
//        sm.appendUserToFile(new User("alice", "pass1"));
//        sm.appendUserToFile(new User("bob", "pass2"));
//
//        List<User> users = sm.readUsersFromFile();
//        assertEquals(2, users.size());
//    }
//
//    public void testWriteUsersToFileOverwritesPrevious() {
//        StorageManager sm = new StorageManager();
//        sm.appendUserToFile(new User("old", "oldpass"));
//        sm.writeUsersToFile(List.of(new User("new", "newpass")));
//
//        List<User> users = sm.readUsersFromFile();
//        assertEquals(1, users.size());
//        assertEquals("new", users.get(0).getUsername());
//    }
//
//    public void testPersistenceAcrossInstances() {
//        StorageManager sm = new StorageManager();
//        sm.appendUserToFile(new User("alice", "pass123"));
//
//        StorageManager fresh = new StorageManager();
//        List<User> users = fresh.readUsersFromFile();
//        assertEquals(1, users.size());
//        assertEquals("alice", users.get(0).getUsername());
//    }
//
//    public void testReadCorrectUsernameAndPassword() {
//        StorageManager sm = new StorageManager();
//        sm.appendUserToFile(new User("charlie", "secret"));
//
//        List<User> users = sm.readUsersFromFile();
//        assertEquals("charlie", users.get(0).getUsername());
//        assertEquals("secret", users.get(0).getPassword());
//    }
//
//}