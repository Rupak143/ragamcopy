<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
require_once 'conn.php';

// Check for required fields - matching what Android sends
if (
    empty($_POST['full_name']) || 
    empty($_POST['email']) || 
    empty($_POST['phone_number']) || 
    empty($_POST['gender']) || 
    empty($_POST['dob']) || 
    empty($_POST['password'])
) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields"
    ]);
    exit;
}

// Get data using correct field names
$full_name = mysqli_real_escape_string($conn, $_POST['full_name']);
$email = mysqli_real_escape_string($conn, $_POST['email']);
$phone_number = mysqli_real_escape_string($conn, $_POST['phone_number']);
$gender = mysqli_real_escape_string($conn, $_POST['gender']);
$dob = mysqli_real_escape_string($conn, $_POST['dob']);
$password = password_hash($_POST['password'], PASSWORD_DEFAULT);

// Check if email already exists
$checkEmail = "SELECT id FROM users WHERE email = '$email' LIMIT 1";
$result = mysqli_query($conn, $checkEmail);

if (mysqli_num_rows($result) > 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Email already exists"
    ]);
    exit;
}

// Insert new user
$sql = "INSERT INTO users (full_name, email, phone_number, gender, dob, password, created_at)
        VALUES ('$full_name', '$email', '$phone_number', '$gender', '$dob', '$password', NOW())";

if (mysqli_query($conn, $sql)) {
    echo json_encode([
        "status" => "success",
        "message" => "User registered successfully"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Database error: " . mysqli_error($conn)
    ]);
}

mysqli_close($conn);
?>