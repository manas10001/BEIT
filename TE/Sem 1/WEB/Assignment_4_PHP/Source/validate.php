<?php
	//no field should be empty
	if(empty($_POST['email']) || empty($_POST['mob']) || empty($_POST['url']) )
	{
		echo "<script>alert('All fields are required!');</script>";		
	}

	//get required values in variables
	$phno = $_POST['mob'];
	$email = $_POST['email'];
	$url = $_POST['url'];


	//validate email
	if(preg_match('/^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{2})+\.([A-Za-z]{2})$/',$email) )
	{
		echo "Matches .com";
	}
	else if (preg_match('/^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{3})$/' , $email) )
	{
		echo "Matches .co.in";
	}
	else
	{
		echo "<script>alert('Invalid Email');</script>";
		echo "<script>window.location.href = 'signup.php'</script>";
	}	

	//validate phone number
	if(!preg_match('/^\d{10}$/',$phno))
	{
		echo "<script>alert('Invalid mobile number');</script>";	
		echo "<script>window.location.href = 'signup.php'</script>";	
	}


	if(filter_var($url,FILTER_VALIDATE_URL)===false)
	{
		echo "<script>alert('Invalid Social Link');</script>";
		echo "<script>window.location.href = 'signup.php'</script>";	
	}

	echo "<script>alert('All fields Correct!');</script>";	
	echo "<script>window.location.href = 'signup.php'</script>";

?>