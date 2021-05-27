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
                <a href="./profiles.php" class="navitem active">Profiles</a>
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
		if(isset($_GET['type'])){
			$type =  $_GET['type'];
			$query = "select * from interview_experience where type = '$type'";
		}else{
			$field = $_GET['field'];
			$query = "select * from interview_experience where field = '$field'";
			
		}
		
		
		$result=mysqli_query($con,$query);
		
		while($row = @mysqli_fetch_array($result)){
		?>
		<div class="card card-lg">
                  <div class="card-body">
                    <div class="card-title">
                        <strong><? echo "Company Name: ".$row['company']  ?></strong>
                    </div>
                    <div class="card-content" style="font-size:18px">
                    	<? echo "Job Title: ".$row['job_title']  ?><br>
                    	<? echo "Posted On: ".$row['posted_on']  ?><br>
                    </div>
                </div>
                	<a class="link" href="./experience.php?id=<?echo $row['id']?>">Read the interview experiences!</a>
                </div>
		<?}
	}

?>
	</div>
