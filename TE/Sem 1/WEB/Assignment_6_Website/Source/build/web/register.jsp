<!DOCTYPE html>
<html>
    <head>
        <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
        <link href="css/main.css" rel="stylesheet" />
        <link href="css/form.css" rel="stylesheet" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Blog</title>
        <style type="text/css">
          .main{
            margin-top: 8vh;
           /* border: 1px solid red;*/
          }
        </style>
    </head>
    <body style="background-color: #9eabe4; background-image: linear-gradient(315deg, #9eabe4 64%, #77eed8 0%);">
        <!--NAVBAR START-->
    <nav class="nav navbar bg-light fixed-top">
      <div class="left-part">
        <div class="college-name">
          <div onclick="location.href='index.jsp'">
            <h5>Travel Life</h5>
          </div>
        </div>
      </div>
      <div class="right-part">
        <div class="nav-item">
          <button class="btn btn-info" onclick="location.href='login.jsp'">Sign In</button>
        </div>
      </div>
    </nav>
        <!--NAVBAR END-->

    <div class="container-fluid main">
      <center>
          <div class="txtbox">           
            <form>
            <!-- <fieldset> -->
              <legend>Register Here</legend>

              <label class="form-text">Name:</label>
              <input class="form-control" type="text" placeholder="Username" onKeyup="return liveValidateStr('uname')" required name="uname" id="uname"/><br>
               
              <label>Email:</label>
              <input class="form-control" onkeyup="return liveValidateMail()" type="email" placeholder="Email"  required name="email" id="email"/><br>
               
              <label>Contact No:</label>
              <input class="form-control" onkeyup="return liveValidateMob()" type="mobile" placeholder="Mobile Number" required name="mob" id="mob"/><br>

              <label>Password:</label>
              <input class="form-control" type="password" placeholder="Password" required name="password" id="password"/><br>
              
              <label>Confirm Password:</label>
              <input class="form-control" type="password" placeholder="Confirm Password" required name="repassword" id="repassword"/><br>
              
               <input type="submit" onclick="return validate();" class="logbtn" value="Register"/>
               <input type="reset"  class="resetbtn" value="Reset"/>
            
            <!-- </fieldset> -->
            </form> 
          </div>
      </center>  <br><br>
    </div>
    <script type="text/javascript">
      document.getElementById("mob").maxLength = "10";
    </script>
    <script type="text/javascript" src="validateSignup.js"></script>
    </body>
</html>
