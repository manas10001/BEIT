<?php session_start();
	require_once('dbconn.php');

	//Code for Registration
	if(isset($_POST))
	{
		$username = $_POST['login'];
		$mail = $_POST['mail'];
		$password = $_POST['password'];
		$repass = $_POST['repassword'];
		$msg = mysqli_query($con,"insert into users(username,email,password) values('$username','$mail','$password')");
		
		if($msg)
		{
			echo "<script>alert('Registration Success');</script>";
			header("Location: ./loginpage.php");
		} else {
			echo "<script>alert('Registration Failed!!!');</script>";
		}
	}

?>
