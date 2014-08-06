//var ws = new WebSocket("ws://localhost:8080/");
var protocol = "ws://";
var host = "localhost";
var urlDelim = ":";
var listenPort = "8080";
var contextPath = "/hurricane/server/jetty/primary";
//var ws = new WebSocket("ws://localhost:8080/hurricane");
var clientSock = new WebSocket(protocol + host + urlDelim + listenPort + contextPath);

ws.onopen = function() {
    alert("Opened!");
    ws.send("Hello Server");
};

ws.onmessage = function (evt) {
    alert("Message: " + evt.data);
};

ws.onclose = function() {
    alert("Closed!");
};

ws.onerror = function(err) {
    alert("Error: " + err);
};