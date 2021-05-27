<?php
	session_start();
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
               	<a href="./addExperience.php" class="navitem">Add Experience</a>                	
			<a href="./logout.php" class="navitem">Logout</a>                	
                <?}else{?>
                	<a href="./loginpage.php" class="navitem">Login/Register</a>
                <?}?>
            </ul>    
        </div>

	<div class="content">
<?php
	require 'dbconn.php';

	if(isset($_GET) && !empty($_GET)){
	
		$query = "";
		if(isset($_GET['id'])){
			$id =  $_GET['id'];
			$query = "select * from interview_experience where id = '$id'";
		}else{
			$query = "select * from interview_experience";
			
		}
		
		
		$result=mysqli_query($con,$query);
		
		while($row = @mysqli_fetch_array($result)){
		?>
		<div class="card card-xlg">
                <div class="card-body">
                    <div class="card-title" style="text-align: center; font-size:19px">
			<strong><?echo $row['job_title']?> Interview Experience!</strong>
                    </div>
                    <hr>
                    <div class="card-content" style="font-size:19px">
                        <strong>Job Title:</strong> <?echo $row['job_title']?><br>
                        <strong>Company:</strong> <?echo $row['company']?><br>
                        <strong>Posted On:</strong> <?echo $row['posted_on']?><br>
                        <strong>Application Process:</strong>
                            <p><?echo $row['application_process']?></p>
                        <strong>Interview Process:</strong>
                            <p><?echo $row['interview_details']?></p>
                        <strong>Preperation Tips:</strong>
                            <p><?echo $row['prep_tips']?></p>
                    </div>
                </div>
            </div>
		<?
		}}
		?>
	</div>
    </body>
</html>
