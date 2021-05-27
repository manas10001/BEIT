-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 27, 2021 at 08:51 PM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.4.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `login_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `interview_experience`
--

CREATE TABLE `interview_experience` (
  `id` int(11) NOT NULL,
  `field` varchar(10) NOT NULL,
  `type` varchar(10) NOT NULL,
  `job_title` varchar(10) NOT NULL,
  `company` varchar(20) NOT NULL,
  `posted_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `application_process` varchar(1024) NOT NULL,
  `interview_details` varchar(1024) NOT NULL,
  `prep_tips` varchar(1024) NOT NULL,
  `added_by` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `interview_experience`
--

INSERT INTO `interview_experience` (`id`, `field`, `type`, `job_title`, `company`, `posted_on`, `application_process`, `interview_details`, `prep_tips`, `added_by`) VALUES
(1, 'cs', 'jb', 'sde', 'oracle', '2021-05-27 17:45:09', 'careers site', 'accha tha', 'padhle bhai', 'manas');

-- --------------------------------------------------------

--
-- Table structure for table `otp`
--

CREATE TABLE `otp` (
  `email` varchar(50) NOT NULL,
  `otp` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `email`, `password`) VALUES
('iwpl', 'iwpl@gmai.com', 'iwpl1234'),
('test', 'test123@gmail.com', 'test1234');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `interview_experience`
--
ALTER TABLE `interview_experience`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `otp`
--
ALTER TABLE `otp`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `interview_experience`
--
ALTER TABLE `interview_experience`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
