<?php 
	session_start();
	require_once('dbconn.php');

	//Code for adding experience
	if(isset($_POST))
	{
		//<!--category profile jobTitle company application-process interview-details prep-tips-->
		
		$username = $_SESSION['username'];
		$company = $_POST['company'];
		$title = $_POST['job'];
		$type = $_POST['type'];
		$field = $_POST['field'];
		$application = $_POST['application'];
		$interview = $_POST['interview_details'];
		$prepTips = $_POST['prepTips'];
		

		$msg = mysqli_query($con,"insert into interview_experience (field, type, job_title, company, application_process, interview_details, prep_tips, added_by) values('$field','$type','$title','$company','$application','$interview','$prepTips','$username')");
		
		if($msg)
		{
			echo "<script>alert('Insertion Success');</script>";
			header("Location: ./addExperience.php");
		} else {
			echo "<script>alert('Interview Exp adding Failed!!!');</script>";
		}
	}

?>
