let posts = JSON.parse(localStorage.getItem('posts')) || [];
let showNames = false; // Initially set to false to hide names

// Ask for user's name and store it in local storage
let username = localStorage.getItem('username');
if (!username) {
    username = prompt("Please enter your name:");
    localStorage.setItem('username', username);
}

// Welcome message
const welcomeMessage = document.createElement("p");
welcomeMessage.textContent = `Welcome to Shadow Chat, ${username}! Feel free to post anything you want as a shadow in the light.`;
document.getElementById("content").insertBefore(welcomeMessage, document.getElementById("postForm"));

function renderPosts() {
    const postContainer = document.getElementById("postContainer");
    postContainer.innerHTML = "";

    // Sort posts based on the total score (thumbs up - thumbs down)
    posts.sort((a, b) => (b.thumbsUp - b.thumbsDown) - (a.thumbsUp - a.thumbsDown));

    posts.forEach((post, index) => {
        const postElement = document.createElement("div");
        postElement.classList.add("post");
        const userNameDisplay = showNames ? `<strong>${post.username}: </strong>` : '';
        postElement.innerHTML = `<p>${userNameDisplay}${formatText(post.content)}</p><span class="thumbs" onclick="thumbsUp(${index})">👍 ${post.thumbsUp}</span><span class="thumbs" onclick="thumbsDown(${index})">👎 ${post.thumbsDown}</span>`;

        // Add event listener to track cursor movement for sparkle effect
        postElement.addEventListener("mousemove", function(event) {
            createSparkle(event.clientX, event.clientY);
        });

        // Reply form for each post
        const replyForm = document.createElement("form");
        replyForm.innerHTML = `<textarea id="replyContent_${index}" rows="2" cols="30" placeholder="Write a reply..." required></textarea><br><button type="submit">Reply</button>`;
        replyForm.addEventListener("submit", function(event) {
            event.preventDefault();
            const replyContent = document.getElementById(`replyContent_${index}`).value.trim();
            if (replyContent !== "") {
                addReply(index, replyContent);
                document.getElementById(`replyContent_${index}`).value = "";
            }
        });
        postElement.appendChild(replyForm);

        // Display replies
        if (post.replies.length > 0) {
            const repliesContainer = document.createElement("div");
            post.replies.forEach(reply => {
                const replyElement = document.createElement("div");
                replyElement.classList.add("reply");
                replyElement.innerHTML = `<p>${formatText(reply)}</p>`;
                repliesContainer.appendChild(replyElement);
            });
            postElement.appendChild(repliesContainer);
        }

        postContainer.appendChild(postElement);
    });
}

// Function to format long text into paragraphs
function formatText(text) {
    const maxLength = 40; // Maximum characters per line
    const words = text.split(' ');
    let formattedText = '';
    let line = '';
    for (let i = 0; i < words.length; i++) {
        if ((line + words[i]).length > maxLength) {
            formattedText += `<p>${line}</p>`;
            line = '';
        }
        line += words[i] + ' ';
    }
    if (line !== '') {
        formattedText += `<p>${line}</p>`;
    }
    return formattedText;
}

// Function to create sparkle effect
document.addEventListener("mousemove", function(event) {
    const target = event.target;
    // Check if the cursor is over the post boxes or reply boxes
    if (!target.classList.contains("post") && !target.classList.contains("reply")) {
        createSparkle(event.clientX, event.clientY);
    }
});

function createSparkle(x, y) {
    const sparkle = document.createElement("div");
    sparkle.classList.add("sparkle");
    sparkle.style.left = `${x}px`;
    sparkle.style.top = `${y}px`;
    document.body.appendChild(sparkle);

    // Remove sparkle element after animation
    sparkle.addEventListener("animationend", function() {
        sparkle.remove();
    });
}

function addPost(content) {
    const newPost = {
        username: username,
        content: content,
        thumbsUp: 0,
        thumbsDown: 0,
        replies: []
    };
    posts.push(newPost);
    savePostsToLocalStorage(); // Save posts to localStorage
    renderPosts();
}

function addReply(postIndex, replyContent) {
    const post = posts[postIndex];
    if (post) {
        post.replies.push(replyContent);
        savePostsToLocalStorage(); // Save posts to localStorage
        renderPosts();
    }
}

function thumbsUp(index) {
    const post = posts[index];
    if (post) {
        post.thumbsUp++;
        savePostsToLocalStorage(); // Save posts to localStorage
        renderPosts();
    }
}

function thumbsDown(index) {
    const post = posts[index];
    if (post) {
        post.thumbsDown++;
        savePostsToLocalStorage(); // Save posts to localStorage
        renderPosts();
    }
}

function savePostsToLocalStorage() {
    localStorage.setItem('posts', JSON.stringify(posts));
}

document.getElementById("postForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const postContent = document.getElementById("postContent").value.trim();
    if (postContent !== "") {
        addPost(postContent);
        document.getElementById("postContent").value = "";
    }
});

document.getElementById("clearStorage").addEventListener("click", function() {
    const password = prompt("Enter password to clear storage:");
    if (password === "ShadowGod101") {
        localStorage.clear();
        posts = [];
        renderPosts();
    } else {
        alert("Incorrect password!");
    }
});

document.getElementById("adminButton").addEventListener("click", function() {
    const password = prompt("Enter password to access admin options:");
    if (password === "ShadowGod101") {
        const options = ["Display Names", "Hide Names"];
        const choice = prompt("Select an option:\n1. Display Names\n2. Hide Names");
        if (choice === "1") {
            showNames = true;
        } else if (choice === "2") {
            showNames = false;
        }
        renderPosts();
    } else {
        alert("Incorrect password!");
    }
});

// Initially render posts when page loads
renderPosts();

// Show content after animation
document.getElementById("content").style.opacity = "1";
