var ws;

// function connect() {
//     var username = document.getElementById("username").value;
//     var wsserver = document.getElementById("wsserver").value;
//     var url = wsserver + username;
//     //var url = "ws://echo.websocket.org";

//     ws = new WebSocket(url);

//     ws.onmessage = function(event) { // Called when client receives a message from the server
//         console.log(event.data);

//         // display on browser
//         var log = document.getElementById("log");
//         log.innerHTML += "message from server: " + event.data + "\n";
//     };

//     ws.onopen = function(event) { // called when connection is opened
//         var log = document.getElementById("log");
//         log.innerHTML += "Connected to " + event.currentTarget.url + "\n";
//     };
// }

function connect() {
  var username = document.getElementById("username").value;
  var wsserver = document.getElementById("wsserver").value;
  var url = wsserver + username; // Adjust based on server expectations

  ws = new WebSocket(url);

  ws.onopen = function (event) {
    var log = document.getElementById("log");
    console.log(log.value);
    log.value += "Connected to " + event.currentTarget.url + "\n";
  };

  ws.onmessage = function (event) {
    console.log(event.data);
    var log = document.getElementById("log");
    log.value += "Message from server: " + event.data + "\n";
  };

  ws.onerror = function (event) {
    var log = document.getElementById("log");
    log.value += "WebSocket error occurred.\n";
    console.error("WebSocket error:", event);
  };

  ws.onclose = function (event) {
    var log = document.getElementById("log");
    log.value += "WebSocket connection closed.\n";
    console.warn("WebSocket closed:", event);
  };
}

function send() {
  // this is how to send messages
  var content = document.getElementById("msg").value;
  ws.send(content);
}
