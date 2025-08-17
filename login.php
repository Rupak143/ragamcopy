<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
require_once 'conn.php';

// Check required fields
if (empty($_POST['email']) || empty($_POST['password'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing email or password"
    ]);
    exit;
}

$email = mysqli_real_escape_string($conn, $_POST['email']);
$password = $_POST['password'];

// Find user by email
$sql = "SELECT id, full_name, email, phone_number, gender, dob, password 
        FROM users 
        WHERE email = '$email' 
        LIMIT 1";

$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid email or password"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($result);

// Verify password hash
if (password_verify($password, $user['password'])) {
    // Remove password before sending response
    unset($user['password']);
    echo json_encode([
        "status" => "success",
        "message" => "Login successful",
        "user" => $user
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid email or password"
    ]);
}

mysqli_close($conn);
?>
