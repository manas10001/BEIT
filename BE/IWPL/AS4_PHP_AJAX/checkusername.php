<?php
	require 'dbconn.php';
	if(isset($_POST) && !empty($_POST))
	{
	    $name=$_POST['username'];

	    $query="SELECT * FROM users WHERE username='$name'";
	    $result=mysqli_query($con,$query);
	    
	    $cnt=@mysqli_num_rows($result);
	    
	    if($cnt==1){
		echo "invalid";
	    }else{
	    	echo "valid";
	    }
	}
?>
