<?php
	$con_err='CANT CONNECT TO DATABASE.';
	$mysql_db="login_system";
	$con=mysqli_connect('localhost','root',''); 
	if(!$con){
		die($con_err.' DATABASE DOWN.');
	}
	if(!@mysqli_select_db($con,$mysql_db)){
		die(' DATABASE DOWN.');
	}
?>
