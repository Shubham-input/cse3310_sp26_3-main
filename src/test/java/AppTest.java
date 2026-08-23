//
//import junit.framework.TestCase;
//import uta.cse3310.User;
//import uta.cse3310.UserManager;
//import uta.cse3310.StorageManager;
//import uta.cse3310.PostManager;
//import uta.cse3310.Post;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//
///**
// * JUnit 3 tests
// *
// * @author:
// */
//public class AppTest extends TestCase {
//
//    private static final String TMP_FILE = System.getProperty("user.home") + "/group_03_users";
//
//    public AppTest(String testName) {
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
//    // =========================================================
//    // PostManager Search Tests
//    // =========================================================
//    public void testSearchReturnsMatchingPost() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello world");
//        List<Post> results = pm.searchPosts("Hello");
//        assertEquals(1, results.size());
//    }
//
//    public void testSearchIsCaseInsensitive() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello World");
//        List<Post> results = pm.searchPosts("hello");
//        assertEquals(1, results.size());
//    }
//
//    public void testSearchReturnsEmptyWhenNoMatch() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello World");
//        List<Post> results = pm.searchPosts("java");
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchReturnsMultipleMatches() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "I love cats");
//        pm.addPost("bob", "cats are great");
//        pm.addPost("carol", "dogs are cool");
//        List<Post> results = pm.searchPosts("cats");
//        assertEquals(2, results.size());
//    }
//
//    public void testSearchNullKeywordReturnsEmpty() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello World");
//        List<Post> results = pm.searchPosts(null);
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchEmptyKeywordReturnsEmpty() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello World");
//        List<Post> results = pm.searchPosts("");
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchOnEmptyPostList() {
//        PostManager pm = new PostManager();
//        List<Post> results = pm.searchPosts("anything");
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchMatchesPartialWord() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "I enjoy programming");
//        List<Post> results = pm.searchPosts("gram");
//        assertEquals(1, results.size());
//    }
//
//    // =========================================================
//    // User Block Tests
//    // =========================================================
//    public void testBlockUserIsBlocked() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("bob");
//        assertTrue(alice.isBlocked("bob"));
//    }
//
//    public void testUnblockedUserIsNotBlocked() {
//        User alice = new User("alice", "pass");
//        assertFalse(alice.isBlocked("bob"));
//    }
//
//    public void testUnblockUser() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("bob");
//        alice.unblockUser("bob");
//        assertFalse(alice.isBlocked("bob"));
//    }
//
//    public void testBlockSameUserTwiceNoDuplicate() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("bob");
//        alice.blockUser("bob");
//        // only one entry, unblocking once should fully remove
//        alice.unblockUser("bob");
//        assertFalse(alice.isBlocked("bob"));
//    }
//
//    public void testUnblockUserNotBlockedDoesNotThrow() {
//        User alice = new User("alice", "pass");
//        try {
//            alice.unblockUser("ghost");
//        } catch (Exception e) {
//            fail("unblockUser on non-blocked user should not throw");
//        }
//    }
//
//    public void testBlockNullDoesNotThrow() {
//        User alice = new User("alice", "pass");
//        try {
//            alice.blockUser(null);
//        } catch (Exception e) {
//            fail("blockUser(null) should not throw");
//        }
//    }
//
//    public void testCannotBlockYourself() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("alice");
//        assertFalse(alice.isBlocked("alice"));
//    }
//
//    public void testBlockMultipleUsers() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("bob");
//        alice.blockUser("carol");
//        assertTrue(alice.isBlocked("bob"));
//        assertTrue(alice.isBlocked("carol"));
//    }
//
//    public void testBlockDoesNotAffectOtherUsers() {
//        User alice = new User("alice", "pass");
//        alice.blockUser("bob");
//        assertFalse(alice.isBlocked("carol"));
//    }
//
//    // =========================================================
//    // Post Reaction Tests
//    // =========================================================
//    public void testAddLikeIncrementsCount() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        p.addLike("test");
//        assertEquals(1, p.getLikeCount());
//    }
//
//    public void testAddHappyIncrementsCount() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        p.addHappy("test");
//        assertEquals(1, p.getHappyCount());
//    }
//
//    public void testAddSadIncrementsCount() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        p.addSad("test");
//        assertEquals(1, p.getSadCount());
//    }
//
//    public void testReactionCountsStartAtZero() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        assertEquals(0, p.getLikeCount());
//        assertEquals(0, p.getHappyCount());
//        assertEquals(0, p.getSadCount());
//    }
//
//    public void testMultipleReactions() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        p.addLike("test");
//        p.addLike("test1");
//        p.addLike("test2");
//        assertEquals(3, p.getLikeCount());
//    }
//
//    // =========================================================
//    // Sort by Most Liked Tests
//    // =========================================================
//    public void testSortByMostLikedOrder() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "post one");
//        pm.addPost("bob", "post two");
//        pm.getPosts().get(0).addLike("test");
//        pm.getPosts().get(1).addLike("test");
//        pm.getPosts().get(1).addLike("test1");
//
//        List<Post> sorted = pm.sortByMostLiked();
//        assertEquals("post two", sorted.get(0).getContent());
//        assertEquals("post one", sorted.get(1).getContent());
//    }
//
//    public void testSortByMostLikedDoesNotMutateOriginal() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "first");
//        pm.addPost("bob", "second");
//        pm.getPosts().get(1).addLike("test");
//        pm.sortByMostLiked();
//        assertEquals("first", pm.getPosts().get(0).getContent());
//    }
//
//    public void testSortByMostLikedEmptyList() {
//        PostManager pm = new PostManager();
//        assertEquals(0, pm.sortByMostLiked().size());
//    }
//
//    // =========================================================
//    // Sort by Most Recent Tests
//    // =========================================================
//    public void testSortByMostRecentReturnsSameCount() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "first");
//        pm.addPost("bob", "second");
//        assertEquals(2, pm.sortByMostRecent().size());
//    }
//
//    public void testSortByMostRecentDoesNotMutateOriginal() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "first");
//        pm.addPost("bob", "second");
//        pm.sortByMostRecent();
//        assertEquals("first", pm.getPosts().get(0).getContent());
//    }
//
//    public void testSortByMostRecentEmptyList() {
//        PostManager pm = new PostManager();
//        assertEquals(0, pm.sortByMostRecent().size());
//    }
//
//    // =========================================================
//    // Search by Username Tests
//    // =========================================================
//    public void testSearchByUsernameFindsMatch() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        pm.addPost("bob", "World");
//        List<Post> results = pm.searchByUsername("alice");
//        assertEquals(1, results.size());
//        assertEquals("alice", results.get(0).getAuthor());
//    }
//
//    public void testSearchByUsernameIsCaseInsensitive() {
//        PostManager pm = new PostManager();
//        pm.addPost("Alice", "Hello");
//        List<Post> results = pm.searchByUsername("alice");
//        assertEquals(1, results.size());
//    }
//
//    public void testSearchByUsernameNoMatch() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        List<Post> results = pm.searchByUsername("carol");
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchByUsernameNullReturnsEmpty() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        List<Post> results = pm.searchByUsername(null);
//        assertEquals(0, results.size());
//    }
//
//    public void testSearchByUsernameEmptyReturnsEmpty() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        List<Post> results = pm.searchByUsername("");
//        assertEquals(0, results.size());
//    }
//
//    // =========================================================
//    // Timestamp Tests
//    // =========================================================
//    public void testPostHasTimestamp() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        assertNotNull(p.getTimestamp());
//    }
//
//    public void testPostTimestampIsBeforeNow() {
//        PostManager pm = new PostManager();
//        pm.addPost("alice", "Hello");
//        Post p = pm.getPosts().get(0);
//        assertTrue(java.time.LocalDateTime.parse(p.getTimestamp()).isBefore(java.time.LocalDateTime.now().plusSeconds(1)));
//    }
//}
