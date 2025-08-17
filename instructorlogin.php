<?php
header("Content-Type: application/json");
require "conn.php"; // Use only ONE connection file

$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Read JSON input
    $data = json_decode(file_get_contents("php://input"), true);

    $email = isset($data['email']) ? trim($data['email']) : '';
    $password = isset($data['password']) ? trim($data['password']) : '';

    if (empty($email) || empty($password)) {
        echo json_encode([
            "status" => "error",
            "message" => "Email and password are required"
        ]);
        exit;
    }

    // ✅ Adjust table name based on your signup script
    $stmt = $conn->prepare("SELECT instructor_id, first_name, last_name, email, phone, gender, education, password 
                            FROM instructor 
                            WHERE email = ? LIMIT 1");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 1) {
        $user = $result->fetch_assoc();

        // ✅ If password is hashed
        if (password_verify($password, $user['password'])) {
            $response = [
                "status" => "success",
                "message" => "Login successful",
                "user" => [
                    "id" => $user['instructor_id'],
                    "first_name" => $user['first_name'],
                    "last_name" => $user['last_name'],
                    "email" => $user['email'],
                    "phone" => $user['phone'],
                    "gender" => $user['gender'],
                    "education" => $user['education']
                ]
            ];
        } else {
            $response = [
                "status" => "error",
                "message" => "Invalid password"
            ];
        }
    } else {
        $response = [
            "status" => "error",
            "message" => "User not found"
        ];
    }

    echo json_encode($response);

    $stmt->close();
    $conn->close();
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request method"
    ]);
}
?>
