-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 18, 2025 at 05:16 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ragam`
--

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `id` int(11) NOT NULL,
  `course_title` varchar(255) NOT NULL,
  `course_description` text NOT NULL,
  `video_path` varchar(500) NOT NULL,
  `created_by_email` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`id`, `course_title`, `course_description`, `video_path`, `created_by_email`, `created_at`) VALUES
(1, 'rt', 'rt', 'uploads/videos/1755443552_upload6021166535325024743.mp4', 'instructor@example.com', '2025-08-17 15:12:32');

-- --------------------------------------------------------

--
-- Table structure for table `instructor`
--

CREATE TABLE `instructor` (
  `instructor_id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `gender` enum('Male','Female','Other') NOT NULL,
  `education` varchar(200) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `dob` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `instructor`
--

INSERT INTO `instructor` (`instructor_id`, `first_name`, `last_name`, `email`, `phone`, `gender`, `education`, `password`, `created_at`, `dob`) VALUES
(1, 'Ravi', 'Kumar', 'ravi.kumar@example.com', '9876543210', 'Male', 'M.A. in Classical Music', '$2y$10$7U5MLv3QXBLye3VCjFAjReKverKzL5l1mgsL2/GVlXjK1NEd1IoC2', '2025-08-17 07:44:46', '1995-05-10'),
(2, 'qw', 'er', 'qwer@gmail.com', '9124578360', 'Male', 'phd', '$2y$10$hxTjCE7czDQFugwsSK4ydOXnBLqpS4c35l/3lbLXLYPwzoEFpzUz.', '2025-08-17 07:54:55', '2005-11-11'),
(3, 'jk', 'l', 'jkl@gmail.com', '8796541230', 'Male', 'PhD', '$2y$10$b9kqJ.iyltULLetcOYQKqueRc/WpfMf7HU97yRUo9FGgx70qZj9p.', '2025-08-17 12:23:43', '2004-11-11'),
(4, 'zxc', 'zxc', 'zxc@gmail.com', '9876541230', 'Male', 'phd', '$2y$10$deMHekxL9I9J2y5g3f1/G.Ib8A8WH7t9/8bsp6a.xnnnxbq.Kzu12', '2025-08-17 12:38:57', '2004-11-18'),
(5, 'hg', 'g', 'hgg@gmail.com', '9874563210', 'Male', 'PhD', '$2y$10$ZAXhYamOGnv/WK88tkxZ9udlqAiIrqC4APOHMfXcg0l5RJg0W/XU.', '2025-08-17 13:09:12', '2004-11-30'),
(6, 'sai', 'ganesh', 'saiganesh@gmail.com', '9579616899', 'Male', 'phd', '$2y$10$AWV5fgVDlp2cgReJmFQ.I.y29pmP9bIpmyiAPJ8GnpEXbFw0hvgN6', '2025-08-17 13:16:58', '2004-11-11');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `gender` enum('Male','Female','Other') NOT NULL,
  `dob` date NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `phone_number`, `gender`, `dob`, `password`, `created_at`) VALUES
(1, 'des poi', 'qwer@gmail.com', '9177871389', 'Male', '2005-11-01', '$2y$10$dhJ/olWC5sIM9Xkhlxe31.IuVLHB.RJawF5LT6ulUyuLNs7nsjkU.', '2025-08-09 16:40:46'),
(2, 'Rupak varma', 'sairupak63@gmail.com', '9187871389', 'Male', '2005-11-01', '$2y$10$XIb7.4rXgRbSyleR3Ck8V.MsxjRh8Bka8nyGNoYlD7rC2cjsxb5HK', '2025-08-09 16:44:26'),
(3, 'sai ganesh', 'ganesh@gmail.com', '1234567890', 'Male', '2003-09-08', '$2y$10$qM91n64Et.cITvy4pHTuK.y9nYXyZOcXOGR3.rnSFM521VnsaXspW', '2025-08-17 04:04:07'),
(4, 'po iu', 'poiu@gmail.com', '1234567892', 'Male', '2004-11-01', '$2y$10$ecqEIXHUlCNa6W4HjichB.B3u.RTTUkdAzya5DFCqRE.xaNryJ8mS', '2025-08-17 06:06:38'),
(5, 'dsaf q', 'dsafq@gmail.com', '9876543218', 'Male', '2000-12-31', '$2y$10$T/SzdWTrvoEaXoxYJ9gypO/VlueWtReMrwWvwx4tTG0vcaN29tKvG', '2025-08-17 06:14:50'),
(6, 'lk jh', 'lkjh@gmail.com', '8796541230', 'Male', '2004-07-07', '$2y$10$Cr6KGt7EKOjEzPdb7zqbe.wQ1iA4vKY8iMyX8ftRd9jkK2gIkDmSK', '2025-08-17 06:30:39'),
(7, 'mn bb', 'mnbv@gmail.com', '7894561230', 'Male', '2004-11-01', '$2y$10$M/U4D.lJYfSYvf/GXH857O8SAMoGXch.zn9r.CJ63C.y3Y.srwvNq', '2025-08-17 06:36:27'),
(8, 'as df', 'asdf@gmail.com', '9876543120', 'Male', '2003-11-08', '$2y$10$acTvHRX7cUytzEgINIyG6.xgVdbU.FpG6qjKTNxiYsVL8C.nllK0W', '2025-08-17 13:18:23');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `instructor`
--
ALTER TABLE `instructor`
  ADD PRIMARY KEY (`instructor_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `phone` (`phone`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `course`
--
ALTER TABLE `course`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `instructor`
--
ALTER TABLE `instructor`
  MODIFY `instructor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
