<?php 
	session_start();
	require_once('./nusoap/lib/nusoap.php');
	
	$wsdl = "http://localhost:8081/SOAP_Login/RegisterHandler?wsdl";
	
	//Code for Registration
	if(isset($_POST))
	{
		$username = $_POST['login'];
		$mail = $_POST['mail'];
		$password = $_POST['password'];
		
		$param = array('username'=>$username, 'password'=>$password, 'email'=>$mail);
		
		print_r($param);
		    
		//instantiating client with server info
		$client = new nusoap_client($wsdl,'wsdl');

		try{
			$result = $client->call('registerUser', $param);

		}catch (Exception $e){
			echo 'message : '.$e->getMessage();
		}
			
		// if the result is true send to main page else print error
	        if(implode($result) == 'true'){
			echo "<script>alert('Registration Success');</script>";
			header("Location: ./loginpage.php");
		} else {
			echo "<script>alert('Registration Failed!!!');</script>";
		}
	}

?>
