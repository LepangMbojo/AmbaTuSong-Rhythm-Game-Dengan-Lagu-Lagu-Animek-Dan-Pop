-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 02, 2025 at 03:46 PM
-- Server version: 8.0.30
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rhythm_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `scores`
--

CREATE TABLE `scores` (
  `id` int NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `song_title` varchar(100) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `combo` int DEFAULT NULL,
  `grade` char(1) DEFAULT NULL,
  `date_played` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `scores`
--

INSERT INTO `scores` (`id`, `username`, `song_title`, `score`, `combo`, `grade`, `date_played`) VALUES
(1, '1', 'FLOW - Sign (TV Size) (Syadow-) [Easy]', 50100, 195, 'S', '2025-12-02 14:20:45'),
(2, '1', 'Tatsuya Kitani - Where Our Blue Is (TV Size) (keksikosu) [Sava\'s Easy]', 41900, 12, 'S', '2025-12-02 14:22:49'),
(3, '1', 'FLOW - Sign (TV Size) (Syadow-) [Easy]', 50100, 22, 'S', '2025-12-02 14:26:34'),
(4, '1', 'Tatsuya Kitani - Where Our Blue Is (TV Size) (keksikosu) [Sava\'s Easy]', 42500, 49, 'S', '2025-12-02 14:28:36'),
(5, '1', 'Tatsuya Kitani - Where Our Blue Is (TV Size) (keksikosu) [Sava\'s Easy]', 15200, 12, 'S', '2025-12-02 14:30:15'),
(6, 'majdix', 'FLOW - Sign (TV Size) (Syadow-) [Easy]', 46300, 160, 'A', '2025-12-02 15:44:23');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`) VALUES
(1, '1', '1'),
(2, 'majdix', '123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `scores`
--
ALTER TABLE `scores`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `scores`
--
ALTER TABLE `scores`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
