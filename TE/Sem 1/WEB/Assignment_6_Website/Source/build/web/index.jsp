<!DOCTYPE html>
<html>
    <head>
        <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
        <link href="css/main.css" rel="stylesheet" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Blog</title>
    </head>
    <body>
        <!--NAVBAR START-->
    <nav class="navbar bg-light fixed-top">
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

        <!-- img -->
    <div class="text-center div1" >
      <div style="padding-top:30vh; color:white">
        <h1>Travel Life</h1> 
        <h3>Discover the best travel stories here</h3>
      </div>
      <div>
        <button class="btn btn-lg" style="width: 130px;background: black;margin-right: 5px;border: 2px solid white; color:white" onclick="location.href='register.jsp'">Join</button>
        <button class="btn btn-lg" style="width: 130px;background: white;margin-left: 5px; color:black;border: 2px solid white;" onclick="location.href='login.jsp'">Log in</button>
      </div>
    </div>
    <!-- <button type="button" onclick="location.href = '';" class="floatbtn" >Down</button> -->
        <!-- abt blg -->
    <div class="text-center div2" >
      <div style="padding-top:13vh;">
        <h1>Travel Life</h1> 
        <h3>
          “TRAVEL IS  FATAL TO PREJUDICE, BIGOTRY, AND NARROW MINDEDNESS.,<br> AND MANY OF OUR PEOPLE NEED IT SORELY ON THESE ACCOUNTS.” ~ MARK TWAIN
        </h3>
      </div>
      
    </div>

      <!-- abt us -->
    <div class="text-center div3" id="abtus">
      <div style="padding-top:10vh;">
        <h1>Our Motive</h1> 
        <h3>For us, travel photography is the most inspiring and exciting form of photography. Every capture is unique,<br> every trip a new experience and this is why we love what we do.<br>Browse through our first-hand travel articles, supported by beautiful photo material.<br> Through our lens you'll only get to see the reality around us: real life - no staging and no pre-arrangements of any kind.
        </h3>
      </div>
    </div>

      <!-- feedblack -->
    <div class="text-center div2" style="padding-top:4vh">
      <h2 class="text-center">CONTACT US</h2>
      <div class="row"> 
        <div class="col-sm-5">
          <h4>
          <p>Contact us and we'll get back to you within 24 hours.</p>
          <p>Katraj, Pune</p>
          <p>+91 8888888888 </p>
          <p>travellife@gmail.com</p>
          </h4>
        </div>
        <div class="col-sm-7 slideanim">
          <form action="#">    
            <div class="row">
              <div class="col-sm-6 form-group">
                <input class="form-control"  id="name" name="name" placeholder="Name" type="text" required>
              </div>
              <div class="col-sm-6 form-group">
                <input class="form-control" id="email" name="email" onkeyup="return liveValidateMail();" placeholder="Email" type="email" required>
              </div>
            </div>
            <textarea class="form-control" id="comments" name="comment" placeholder="Comment" rows="5" required></textarea><br>
            <div class="row">
              <div class="col-sm-12 form-group">
                <button type="submit" class="btn btn-success btn-lg" onclick="return validatefeedback();" type="submit">Send</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div> 

    <script type="text/javascript">
      function validatefeedback(){
        var name = document.getElementById("name").value;
        var email = document.getElementById("email").value;
        var cmnt = document.getElementById("comments").value;

        if(name.length==0)
        {
          alert("All fields required");
          document.getElementById("name").style.border = "1px solid red";
          document.getElementById("name").focus();
          return false;
        }
        else if( email.length==0 )
        { 
          alert("All fields required");
          document.getElementById("email").style.border = "1px solid red";
          document.getElementById("email").focus();
          return false;
        }else if( cmnt.length==0){
          alert("All fields required");
          document.getElementById("comments").style.border = "1px solid red";
          document.getElementById("comments").focus();
          return false;
        }
        alert("Feedback Submitted!");
    }

      //live validate email
      function liveValidateMail(){
        if(document.getElementById("name").value.length>0)
        {
            document.getElementById("name").style.border = "1px solid green";
        }
        var dat = document.getElementById("email").value;
        var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{2})+\.([A-Za-z]{2})$/;
        var reg2 = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{3})$/;
        
        if(reg.test(dat)==true)
          document.getElementById("email").style.border = "1px solid green";
        else if(reg2.test(dat)==true)
          document.getElementById("email").style.border = "1px solid green";
        else
          document.getElementById("email").style.border = "1px solid red";
      }
    </script>

    </body>
</html>
