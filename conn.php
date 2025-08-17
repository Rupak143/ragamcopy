<?php
// Database configuration
$servername = "localhost"; // or your server IP
$username   = "root";       // your DB username
$password   = "";           // your DB password
$dbname     = "ragam"; // your DB name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Optional: set character set to UTF-8
$conn->set_charset("utf8");

?>
