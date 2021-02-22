<?php
	require 'dbconn.php';
	session_start();
	if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {
		echo "<script>alert('Already logged in');</script>";
		header("Location: ./home.php");
	}else{
		if(isset($_POST) && !empty($_POST))
		{
		    $name=$_POST['login'];
		    $pass=$_POST['password'];

		    $query="SELECT * FROM users WHERE username='$name' AND password='$pass'";
		    $result=mysqli_query($con,$query);
		    
		    $cnt=@mysqli_num_rows($result);
		    
		    if($cnt==1){
			echo 'welcome0';
			
			$_SESSION['username']=$name;
			header("Location: ./home.php");
		    }else{
			echo "<script>alert('Invalid credentials');</script>";
			header("Location: ./loginpage.php");
			die();
		    }
		}
	}
?>
