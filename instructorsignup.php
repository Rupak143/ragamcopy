<?php
require 'conn.php';
header('Content-Type: application/json');

// Read raw JSON input
$input = json_decode(file_get_contents("php://input"), true);

if (
    isset($input['first_name']) &&
    isset($input['last_name']) &&
    isset($input['email']) &&
    isset($input['phone']) &&
    isset($input['gender']) &&
    isset($input['dob']) &&
    isset($input['education']) &&
    isset($input['password'])
) {
    // Sanitize inputs
    $firstName = mysqli_real_escape_string($conn, trim($input['first_name']));
    $lastName = mysqli_real_escape_string($conn, trim($input['last_name']));
    $email = mysqli_real_escape_string($conn, trim($input['email']));
    $phone = mysqli_real_escape_string($conn, trim($input['phone']));
    $gender = mysqli_real_escape_string($conn, trim($input['gender']));
    $dob = mysqli_real_escape_string($conn, trim($input['dob']));
    $education = mysqli_real_escape_string($conn, trim($input['education']));
    $password = trim($input['password']);
    $confirmPassword = isset($input['confirm_password']) ? trim($input['confirm_password']) : null;

    // ✅ Validations
    if ($confirmPassword !== null && $password !== $confirmPassword) {
        echo json_encode(["status" => "error", "message" => "Passwords do not match"]);
        exit;
    }
    if (strlen($password) < 6) {
        echo json_encode(["status" => "error", "message" => "Password must be at least 6 characters long"]);
        exit;
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(["status" => "error", "message" => "Invalid email format"]);
        exit;
    }
    if (strlen($phone) < 10 || !ctype_digit($phone)) {
        echo json_encode(["status" => "error", "message" => "Phone number must be at least 10 digits"]);
        exit;
    }
    $validGenders = ['Male', 'Female', 'Other'];
    if (!in_array($gender, $validGenders)) {
        echo json_encode(["status" => "error", "message" => "Invalid gender selection"]);
        exit;
    }
    $dobDate = DateTime::createFromFormat('Y-m-d', $dob);
    if (!$dobDate || $dobDate->format('Y-m-d') !== $dob) {
        echo json_encode(["status" => "error", "message" => "Invalid date format. Use YYYY-MM-DD"]);
        exit;
    }
    $today = new DateTime();
    $age = $today->diff($dobDate)->y;
    if ($age < 18) {
        echo json_encode(["status" => "error", "message" => "Instructor must be at least 18 years old"]);
        exit;
    }

    // ✅ Duplicate checks
    $checkEmailQuery = "SELECT instructor_id FROM instructor WHERE email = ? LIMIT 1";
    $stmt = $conn->prepare($checkEmailQuery);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "An account already exists with this email address"]);
        $stmt->close();
        exit;
    }
    $stmt->close();

    $checkPhoneQuery = "SELECT instructor_id FROM instructor WHERE phone = ? LIMIT 1";
    $stmt = $conn->prepare($checkPhoneQuery);
    $stmt->bind_param("s", $phone);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "An account already exists with this phone number"]);
        $stmt->close();
        exit;
    }
    $stmt->close();

    // ✅ Hash password
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    // ✅ Insert into correct columns (no confirm_password now)
    $insertQuery = "INSERT INTO instructor 
        (first_name, last_name, email, phone, gender, dob, education, password, created_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("ssssssss", 
        $firstName, $lastName, $email, $phone, $gender, $dob, $education, $hashedPassword
    );

    if ($stmt->execute()) {
        $instructorId = $conn->insert_id;
        echo json_encode([
            "status" => "success",
            "message" => "Instructor account created successfully!",
            "instructor_id" => $instructorId,
            "instructor_name" => $firstName . " " . $lastName
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Database error: " . $stmt->error]);
    }
    $stmt->close();

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields"
    ]);
}

$conn->close();
?>
