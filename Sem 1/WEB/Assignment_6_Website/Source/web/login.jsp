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
    <body style="background-color: #9eabe4; background-image: linear-gradient(320deg, #9eabe4 65%, #77eed8 0%);">
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
          <button class="btn btn-info" onclick="location.href='register.jsp'">Sign Up</button>
        </div>
      </div>
    </nav>
        <!--NAVBAR END-->

    <div class="container-fluid main">
      <center>
          <div class="txtbox" style="margin-top: 20vh;">           
            <form action="Login" method="post">
            <!-- <fieldset> -->
              <legend>Login Here</legend>

               <label class="form-text">Email:</label>
               <input class="form-control" type="email" placeholder="Username" onblur="changeBorder('uname');" required name="uname" id="uname"/><br>
               
               <label>Password:</label>
               <input class="form-control" type="password" placeholder="Password"  required name="pass" id="pass"/><br>
              
               <input type="submit" onclick="return validate();" class="logbtn" value="Log In"/>
               <input type="reset"  class="resetbtn" value="Reset"/>
            
            <!-- </fieldset> -->
            </form> 
          </div>
      </center>  
    </div>
    
    <script type="text/javascript">
      function changeBorder(arg)
      {
        document.getElementById(arg).style.border = "1px solid green";
      }
      //FUNCTION TO VALIDATE INPUTS ON SUBMIT
      function validate(){
        var login = document.getElementById("uname").value;
        var pas = document.getElementById("pass").value;

        //no field should be blank
        if(login.length==0 || pas.length==0)
        {
          alert("All fields required");
          document.getElementById("uname").style.border="1px solid red";
          document.getElementById("pass").style.border="1px solid red";
          document.getElementById("uname").focus();
          return false;
        } 

        //validate pass
        if(pas.length<8){
          alert("Incorrect Password!");   
          document.getElementById("pass").style.border="1px solid red";
          document.getElementById("pass").focus();
          return false;
        }
      }
    </script>

    </body>
</html>
