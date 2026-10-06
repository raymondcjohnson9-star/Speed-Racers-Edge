// ==========================================
// SPEED RACERS EDGE
// Main Game JavaScript
// ==========================================

console.log("Speed Racers Edge started!");

// Find our menu buttons
const playButton = document.getElementById("playButton");
const garageButton = document.getElementById("garageButton");
const friendsButton = document.getElementById("friendsButton");
const shopButton = document.getElementById("shopButton");
const profileButton = document.getElementById("profileButton");

// PLAY button
playButton.addEventListener("click", function () {
    console.log("Player selected PLAY");

    alert("Race mode is coming next!");
});

// GARAGE button
garageButton.addEventListener("click", function () {
    console.log("Player selected GARAGE");

    alert("Garage system is coming next!");
});

// FRIENDS button
friendsButton.addEventListener("click", function () {
    console.log("Player selected FRIENDS");

    alert("Friends system is coming next!");
});

// SHOP button
shopButton.addEventListener("click", function () {
    console.log("Player selected SHOP");

    alert("Shop system is coming next!");
});

// PROFILE button
profileButton.addEventListener("click", function () {
    console.log("Player selected PROFILE");

    alert("Profile system is coming next!");
});
