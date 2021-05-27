<html>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <?php 
                session_start();
                if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./home.php" class="navitem">Home</a>                	
                <?}else{?>
                	<a href="./index.php" class="navitem">Home</a>
                <?}?>
                <a href="./interview_tips.php" class="navitem">Interview Tips</a>
                <a href="./categories.php" class="navitem">Categories</a>
                <a href="./profiles.php" class="navitem">Profiles</a>
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
                	<a href="./addExperience.php" class="navitem active">Add Experience</a>                	
			<a href="./logout.php" class="navitem">Logout</a>                	
                <?}else{?>
                	<a href="./loginpage.php" class="navitem">Login/Register</a>
                <?}?>
            </ul>    
        </div>

        <div class="content">
            <div class="card card-md">
                <div class="card-body">
                    <div class="card-title">
                        <strong>Add Interview Details</strong>
                    </div>
                    <!--category profile jobTitle company application-process interview-details prep-tips-->
                    
                    <div class="forms">
                        <form action="addExp.php" method="POST">
                            <fieldset id="fs1">
                                <legend>Interview Details</legend>
                            <label>Company Name:*</label><br>
                            <input type="text" id="company" name="company" autocomplete="off" onkeyup="liveValidateBlank('company'); liveValidateStr('company');" placeholder="Company Name">
                                
                            <label>Job Title:*</label><br>
                            <input type="text" id="job" name="job" onkeyup="liveValidateBlank('job'); liveValidateStr('job');" placeholder="Job Title">

				<label>Job Type:*</label><br>

				<select name="type" id="type">
				  <option value="jb">JOB</option>
				  <option value="is">Internship</option>
				  <option value="pt">Part Time Job</option>
				</select>
				
				<label>Field:*</label><br>

				<select name="field" id="field">
				  <option value="cs">Computer Science</option>
				  <option value="mk">Marketing</option>
				  <option value="fi">Finance</option>
				</select>

                            <label>Application Process:*</label><br>
                            <textarea rows="5" id="application" name="application"  placeholder="Application Process"></textarea>
                            
                            <label>Interview Details:*</label><br>
                            <textarea rows="5" id="interview_details" name="interview_details"  placeholder="Interview Details"></textarea>
                            
                            <label>Preperation Tips:*</label><br>
                            <textarea rows="5" id="prepTips" name="prepTips"  placeholder="Preperation Tips"></textarea>
                            
                             </fieldset>
                         <br><br>
                         <input type="submit" class="logbtn"  value="Submit">
                       </form>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="./validator.js"></script>
    </body>
</html>
