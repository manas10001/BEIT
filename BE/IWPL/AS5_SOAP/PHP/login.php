<?php
	require_once('./nusoap/lib/nusoap.php');
	
	session_start();
	
	$wsdl = "http://localhost:8081/SOAP_Login/LoginHandler?wsdl";
	
	if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {
		echo "<script>alert('Already logged in');</script>";
		header("Location: ./home.php");
	}else{
		if(isset($_POST) && !empty($_POST))
		{
		    $name=$_POST['login'];
		    $pass=$_POST['password'];

		    $param = array('username'=>$name,'password'=>$pass);
		    
		    print_r($param);
		    
		    //instantiating client with server info
		    $client = new nusoap_client($wsdl,'wsdl');

		    try{
		    	$result = $client->call('validateLogin', $param);
		    			    	print_r($result);
		    }catch (Exception $e){
		    	echo 'message : '.$e->getMessage();
		    }
			
		    // if the result is true send to main page else print error
		    if(implode($result) == 'true'){
			$_SESSION['username']=$name;
			header("Location: ./home.php");
		    }else{
			echo "<script>alert('Invalid credentials');</script>";
//			header("Location: ./loginpage.php");
		    }

		}
	}
?>
