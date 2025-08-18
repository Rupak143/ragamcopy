<?php
header('Content-Type: application/json');
require "conn.php";



// ✅ Check request method
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // Check if the fields exist
    $courseTitle = $_POST['course_title'] ?? '';
    $courseDescription = $_POST['course_description'] ?? '';
    $createdBy = $_POST['created_by_email'] ?? '';

    if (empty($courseTitle) || empty($courseDescription) || empty($createdBy)) {
        echo json_encode(["status" => "error", "message" => "Please fill all fields"]);
        exit;
    }

    // ✅ Handle video upload
    if (isset($_FILES['video']) && $_FILES['video']['error'] === 0) {
        $video = $_FILES['video'];
        $uploadDir = 'uploads/videos/'; // Make sure this folder exists & writable
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }

        $videoName = time() . "_" . basename($video['name']);
        $targetPath = $uploadDir . $videoName;

        if (move_uploaded_file($video['tmp_name'], $targetPath)) {
            // ✅ Insert into database
            $stmt = $conn->prepare("INSERT INTO course (course_title, course_description, video_path, created_by_email, created_at) VALUES (?, ?, ?, ?, NOW())");
            $stmt->bind_param("ssss", $courseTitle, $courseDescription, $targetPath, $createdBy);

            if ($stmt->execute()) {
                echo json_encode(["status" => "success", "message" => "Course created successfully"]);
            } else {
                echo json_encode(["status" => "error", "message" => "DB insert failed"]);
            }

            $stmt->close();
        } else {
            echo json_encode(["status" => "error", "message" => "Failed to upload video"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "No video uploaded or upload error"]);
    }

} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
}

$conn->close();
?>
