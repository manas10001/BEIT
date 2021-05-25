<?php
	session_start();
	if (isset($_SESSION['username']) && !empty($_SESSION['username'])){
		header("Location: ./home.php");
	}
	require 'dbconn.php';s
?>
<html>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./home.php" class="navitem">Home</a>                	
                <?}else{?>
                	<a href="./index.php" class="navitem">Home</a>
                <?}?>
                <a href="./interview_tips.php" class="navitem">Interview Tips</a>
                <a href="./categories.php" class="navitem">Categories</a>
                <a href="./profiles.php" class="navitem">Profiles</a>
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./logout.php" class="navitem">Logout</a>                	
                <?}else{?>
                	<a href="./loginpage.php" class="navitem active">Login/Register</a>
                <?}?>
            </ul>    
        </div>
        <div class="content">
            <div class="card card-md">
                <div class="card-body">
                    <div class="card-title">
                        <strong>Reset Password</strong>
                    </div>
                    <div class="forms">
                        <form action="#" method="post">
                            <fieldset id="fs1">
                                <legend>Account Details</legend>
                                <label>Email ID:*</label><br>
                                <input type="email" name="email" id="email" value="<?php echo $_GET['email']?>" readonly=true style="background:#F5F5F5">
                                
                                <label>OTP:*</label><br>
                                <input type="text" id="otp" name="otp" onkeyup="liveValidateBlank('otp'); lengthValidate('otp',6);" placeholder="OTP">
                                
                                <label>New Password:*</label><br>
				<input type="password" id="password" name="password" onkeyup="liveValidateBlank('password'); lengthValidate('password',8);" placeholder="New Password">

				<label>Retype Password:*</label><br>
				<input type="password" id="repassword" name="repassword" onkeyup="liveValidateBlank('repassword'); lengthValidate('repassword',8)" placeholder="Retype Password">
                            </fieldset>
                         <br><br>
                         <input type="submit" class="logbtn" value="Change Password">
                       </form>
                    </div>
                </div>
                <a class="link" href="./registerpage.php">Not a member? Register!</a>
            </div>
        </div>
        
        <?php
		if(isset($_POST) && !empty($_POST)){
			
			$email=$_POST['email'];
			
			//get otp from db for email
			
			$query="SELECT otp FROM otp WHERE email='$email'";
			$result=mysqli_query($con,$query);

			$cnt=@mysqli_num_rows($result);

			if($cnt >= 1){
			
				$row = mysqli_fetch_assoc($result);
				$otp = $row['otp'];
				
				//verify user otp input and otp from db
				$uotp = $_POST['otp'];
				
				if($otp === $uotp){
					#validate retyped pass
					$pass = $_POST['password'];
					$repass = $_POST['repassword'];
					
					if($pass === $repass){
						//reset pass redirect to login
						$res = mysqli_query($con,"update users set password='$pass' where email='$email'");
						echo "err: ".mysqli_error($con);
						if($res){
							//delete record from otp
							$del = mysqli_query($con,"delete from otp where email='$email'");
							//redirct 
							header("Location: ./loginpage.php");					
						} else {
							echo "err: ".mysqli_error($con);
							echo "<script>alert('Coudnt update password try again!!!');</script>";
						}
					}else{
						echo "<script>alert('Password and retyped password dont match');</script>";
					}
				}else{
					echo "<script>alert('Invalid OTP!');</script>";
				}
			}else{
				echo "<script>alert('No such user account in system!');</script>";
			}
		}
	?>

        
        <script type="text/javascript" src="./validator.js"></script>
    </body>
</html>
