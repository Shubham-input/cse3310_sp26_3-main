let postsData = [];
let postIdCounter = 0;
let blockedUsers = [];
let currentUser = "";
let myReactions = {};

const maxChars = 256;
const textarea = document.getElementById("sendMsg");
const charCounter = document.getElementById("charCounter");

function updateCharCounter() {
  const remaining = maxChars - textarea.value.length;
  charCounter.textContent = `${remaining} characters remaining`;
  charCounter.classList.toggle("over-limit", remaining < 0);
}

textarea.addEventListener("input", updateCharCounter);

let connection = new WebSocket(
  "ws://" +
  window.location.hostname +
  ":" +
  (parseInt(location.port) + 1)
);

connection.onopen = function () {
  console.log("connection OPENED");
};

connection.onclose = function () {
  console.log("connection CLOSED");
};

connection.onmessage = function (evt) {
  const obj = JSON.parse(evt.data);

  // for handle all the message types
  switch (obj.msgType) {
    case "idMsg":
      document.getElementById("connection-status-display").textContent =
        "Connected with ID: " + obj.connectionId;
      break;

    case "displayMsg":
      document.getElementById("input-name").hidden = !obj.displayName;
      document.getElementById("div-signup").hidden = !obj.displayName;
      document.getElementById("feed").hidden = !obj.displayMain;
      document.getElementById("message-bar").hidden = !obj.displaySend;
      break;

    /* login/signup handler */
    case "authMsg":
      if (obj.action === "signup") {
        if (obj.status === "success") {
          const box = document.getElementById("error-message");
          box.textContent = "Account created successfully! You can now log in.";
          box.classList.remove("hidden");
          box.style.color = "green";

          document.getElementById("signup-user").value = "";
          document.getElementById("signup-pass").value = "";
          document.getElementById("signup-confirm").value = "";
          console.log("signup SUCCESS: ", obj.msgType);
        } else {
          showError(obj.message);
          console.log("signup FAILED: ", obj.msgType);
        }
      }

      if (obj.action === "login") {
        if (obj.status === "success") {
          clearError();
          currentUser = document.getElementById("username").value.trim();
          
          document.getElementById("input-name").hidden = true;
          document.getElementById("div-signup").hidden = true;
          document.getElementById("message-bar").hidden = false;
          document.getElementById("logout-btn").hidden = false;
          document.getElementById("feed-controls").hidden = false;
          document.getElementById("username").value = "";
          document.getElementById("password").value = "";
          console.log("login SUCCESS: ", obj.msgType);
        } else {
          showError("Hmm, that username and/or password is invalid... Please try again!");
          console.log("login FAILED: ", obj.msgType);
        }
      }
      break;

    /* online users list */
    case "onlineUsersMsg":
      updateOnlineUsers(obj.users);
      break;

    // offline users list
    case "offlineUsersMsg":
      updateOfflineUsers(obj.users);
      break;

    /* posts have username of sender attached */
    case "postMsg":
     addPost(
        obj.username, 
        obj.msg, 
        obj.timestamp, 
        obj.postId, 
        obj.likes, 
        obj.happy, 
        obj.sad
      );
      console.log("message posted: ", obj.msgType);
      break;

    case "blockListMsg":
      blockedUsers = obj.blockedUsers;
      refreshBlockedVisuals();
      console.log("blocked users updated: ", obj.msgType);
      break;

    case "sendToAllMsg":
      break;

    case "updateReactionMsg":
      const postToUpdate = postsData.find(p => p.id === obj.postId);
      if (postToUpdate) {
        postToUpdate.likes = obj.likes;
        postToUpdate.happy = obj.happy;
        postToUpdate.sad = obj.sad;
        renderFeed(postsData);
      }


      break;

    case "reactionMsg":
      console.log("RECEIVED REACTION", obj);

      const targetPost = postsData.find(p => p.msg === obj.msg);

    if (targetPost) {
      if (obj.reaction === "like") targetPost.likes++;
      else if (obj.reaction === "happy") targetPost.happy++;
      else if (obj.reaction === "sad") targetPost.sad++;

      renderFeed(postsData);
    }
    break;

    default:
      console.log("unrecognized message type!:", obj.msgType);


  }
};

/* buttons */

function loginButton() {
  currentUser = document.getElementById("username").value.trim();
  connection.send(
    JSON.stringify({
      msgType: "loginMsg",
      username: document.getElementById("username").value,
      password: document.getElementById("password").value,
    })
  );
}



function signupButton() {
  console.log("signupButton clicked");

  connection.send(
    JSON.stringify({
      msgType: "signupMsg",
      username: document.getElementById("signup-user").value,
      password: document.getElementById("signup-pass").value,
      confirmPassword: document.getElementById("signup-confirm").value,
    })
  );
}

function postButton() {
  if (textarea.value.length > maxChars) {
    return;
  }
  if (textarea.value.trim().length === 0) {
    showError("Cannot post empty messages");
    return;
  }
  connection.send(
    JSON.stringify({
      msgType: "sendMsg",
      msg: textarea.value,
    })
  );

  textarea.value = "";
  updateCharCounter();
}

function logoutButton() {
  currentUser = "";
  console.log(document.getElementById("logout-btn"));

  connection.send(
    JSON.stringify({
      msgType: "logoutMsg"
    })
  );

  currentUser = "";

  document.getElementById("logout-btn").hidden = true;
  document.getElementById("input-name").hidden = false;
  document.getElementById("div-signup").hidden = false;
  document.getElementById("message-bar").hidden = true;

  updateOfflineUsers([]);
}

// error handling
function showError(msg) {
  const box = document.getElementById("error-message");
  box.textContent = msg;
  box.classList.remove("hidden");
  box.style.color = "red";
}

// for clearing out an error message
function clearError() {
  const box = document.getElementById("error-message");
  box.textContent = "";
  box.classList.add("hidden");
}

/* updates.. online users */
function updateOnlineUsers(users) {
  const div = document.getElementById("online-users");
  div.innerHTML = "";
  users.forEach((u) => {
    const isBlocked = blockedUsers.includes(u);
    const style = isBlocked ? 'style="color:red; text-decoration:line-through;"' : '';
    const btnText = isBlocked ? "Unblock" : "Block";

    let buttonHTML = "";

    if (u !== currentUser) {
      buttonHTML = `<button class="block-btn" onclick="toggleBlock('${u}')">${btnText}</button>`;
    }
    
    div.innerHTML += `
      <div class="online-user">
        <span class="username" ${style}>${u === currentUser ? u + " (You)" : u}</span>
        ${buttonHTML}
      </div>`;
  });
}
function toggleBlock(username) {
  if (blockedUsers.includes(username)) {
    connection.send(JSON.stringify({ msgType: "unblockMsg", target: username }));
  } else {
    connection.send(JSON.stringify({ msgType: "blockMsg", target: username }));
  }
}

function refreshBlockedVisuals() {
  const userDivs = document.querySelectorAll("#online-users .online-user");
  userDivs.forEach((div) => {
    const span = div.querySelector(".username"); 
    const btn = div.querySelector(".block-btn"); 
    const username = span.textContent;
    if (blockedUsers.includes(username)) {
      span.style.color = "red";
      span.style.textDecoration = "line-through";
      btn.textContent = "Unblock";
    } else {
      span.style.color = "";
      span.style.textDecoration = "";
      btn.textContent = "Block";
    }
  });
}

function updateOfflineUsers(users) {
  const div = document.getElementById("offline-users");
  div.innerHTML = "";
  users.forEach((u) => {
    div.innerHTML += `<div class="offline-user">${u}</div>`;
  });
}

function addPost(username, msg, timestamp, postId, likes, happy, sad) {
  if (blockedUsers.includes(username)) return;

  // Add the new post to the beginning of the array
  postsData.unshift({ 
    id: postId || postIdCounter++, 
    username, 
    msg, 
    timestamp: timestamp || new Date().toISOString(), 
    likes: likes || 0, 
    happy: happy || 0, 
    sad: sad || 0,
    reactedBy: {} // <--- ADD THIS LINE
  });
  
  renderFeed(postsData);
}

function replyToUser(username) {
  textarea.value = `@${username} `;
  textarea.focus();
  updateCharCounter();
}

// A function that clears curse words that stand alone
// It can not clear words that have the cursed word combined into it
// For example, hello. 'hell'o. As such, "fuckyeah" won't be censored
// But "fuck yeah" will be "**** yeah".
function filterCurseWords(msg) {
  // Please help increase the list of curse words
  const offensiveWords = ["hell", "damn", "fuck", "goddamn", "ass", "asshole", "bastard", "bitch", "bollocks", "bullshit", "cunt", "dick", "dickhead", "dumbass", "fag", "faggot", "fucker", "horseshit", "jackass", "motherfucker", "prick", "pussy", "shit", "slut", "tranny", "twat", "wanker", "stfu", "cum", "kys", "die", "kill", "whore"]; 
  let filteredMsg = msg;

  offensiveWords.forEach(word => {
    const lowerCaseWord = word.toLowerCase(); 
    const regex = new RegExp(`\\b${lowerCaseWord}\\b`, 'g');
    const censored = "*".repeat(word.length);
    filteredMsg = filteredMsg.replace(regex, censored);
  });

  return filteredMsg;
}

function renderFeed(posts) {
  const searchVal = (document.getElementById("username-search").value || "").toLowerCase();
  const toShow = searchVal ? posts.filter(p => p.username.toLowerCase().includes(searchVal)) : posts;
  const feed = document.getElementById("feed");
  feed.innerHTML = toShow.map(p => {
    // FIX: Parse the timestamp and check for validity
    const postDate = new Date(p.timestamp);
    
    // Fallback to "Just now" if the date is invalid (e.g., if p.timestamp is malformed)
  const formattedTime = isNaN(postDate) 
  ? "Recent" 
  : postDate.toLocaleString('en-US', { 
      timeZone: 'America/Chicago',
      month: 'short', 
      day: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: true 
    });

    p.msg = filterCurseWords(p.msg);

    return `
          <div class="post">
            <div class="post-header">
              ${p.username}
              <span class="post-timestamp">${formattedTime}</span>
            </div>
            <div>${p.msg}</div>
            <div class="post-reactions">
              <button class="reaction-btn" onclick="react(${p.id}, 'likes')">👍 ${p.likes}</button>
              <button class="reaction-btn" onclick="react(${p.id}, 'happy')">😊 ${p.happy}</button>
              <button class="reaction-btn" onclick="react(${p.id}, 'sad')">😢 ${p.sad}</button>
              <button class="reply-btn" onclick='replyToUser("${p.username}")'>Reply</button>
            </div>
          </div>
        `;
  }).join("");
}

function react(postId, type) {
  if(!currentUser || currentUser.trim() === "") {
    alert("Please log in to react.");
    return;
  }
  const user = currentUser.trim();
  const post = postsData.find(p => p.id === postId);

  if(!post) return;
  if(!post.reactedBy) { post.reactedBy = {}; }

  const previousReaction = post.reactedBy[user];

  if (previousReaction === type) {
    post[type]--;
    delete post.reactedBy[user];
  } else if(previousReaction) {
    post[previousReaction]--;
    post[type]++;
    post.reactedBy[user] = type;
  } else {
    post[type]++;
    post.reactedBy[user] = type;
  }

  connection.send(JSON.stringify({
    msgType: "reactMsg",
    postId: postId,
    reactionType: type
  }));

  renderFeed(postsData);
}

function sortByLikes() {
  const sorted = [...postsData].sort((a, b) => b.likes - a.likes);
  renderFeed(sorted);
}

function sortByRecent() {
  renderFeed(postsData);
}

function filterByUsername() {
  renderFeed(postsData);
}

/* dropdown function */
function toggleDropdown() {
  const box = document.getElementById("signup-fields");
  const icon = document.getElementById("plus-icon");

  if (box.style.display === "block") {
    box.style.display = "none";
    icon.textContent = "+";
  } else {
    box.style.display = "block";
    icon.textContent = "-";
  }
}
//toggle dark mode class
function toggleDarkMode() {
  document.body.classList.toggle("dark");
  document.documentElement.classList.toggle("dark");
  const btn = document.getElementById("dark-mode-btn");
  if (document.body.classList.contains("dark")) {
    btn.textContent = "Light Mode ☀️";
    localStorage.setItem("darkMode", "on");
  } else {
    btn.textContent = "Dark Mode 🌙 ";
    localStorage.setItem("darkMode", "off");
  }
}

//toggle show password class
function togglePassword(inputId, buttonId) {
  const input = document.getElementById(inputId);
  const button = document.getElementById(buttonId);

  if(input.type === "password") {
    input.type = "text";
    button.textContent = "Hide password";
  } else {
    input.type = "password";
    button.textContent = "Show password";
  }
}

if (localStorage.getItem("darkMode") === "on") {
  document.body.classList.add("dark");
  document.documentElement.classList.add("dark");
  document.getElementById("dark-mode-btn").textContent = "Light Mode ☀️";
}


updateCharCounter();
function openContributors() {
  document.getElementById("contributors-modal").classList.add("show");
}

function closeContributors() {
  document.getElementById("contributors-modal").classList.remove("show");
}

window.addEventListener("click", function(e) {
  const modal = document.getElementById("contributors-modal");
  if (e.target === modal) {
    closeContributors();
  }
});
