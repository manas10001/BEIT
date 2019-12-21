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
        	padding-top: 2vh;
        }
        
		.about-title {}
		.about-title h1 {color: #535353; font-size:45px;}
		.about-title span {color: #AF0808; font-size:45px;font-weight:700;}
		.about-title h3 {color: #535353; font-size:23px;margin-bottom:24px;}
		.about-title p {color: #000;line-height: 1.8;margin: 0 0 15px;}
		.about-paddingB {padding-bottom: 12px;}
		img{
            margin-top: 2vh;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.7);
            height:25vh;
            width:20vw;
            float: right; 
          }
          .active{
            font-weight: 700;
            /*box-shadow: 0 0 10px rgba(0,0,0,1);*/
          }
         </style>
    </head>
    <body style="background-color: #9eabe4; background-image: linear-gradient(15deg, #9eabe4 74%, #77eed8 0%);">
    
        <!--NAVBAR START-->

      <nav class="navbar navbar-expand-md navbar-light bg-light fixed-top">
        <div class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <div onclick="location.href='dashboard.jsp'">
                <h5>Travel Life</h5>
              </div>
                </li>
            </ul>
        </div>
        <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <a class="nav-link" href="dashboard.jsp">Posts</a>
                </li>
                <!-- <li class="nav-item">
                    <a class="nav-link" href="addPost.jsp">New Post</a>
                </li> -->
                <li class="nav-item">
                    <a class="nav-link active" href="aboutUs.jsp">About us</a>
                </li>
                <li class="nav-item">
                    <button class="btn btn-danger" onclick="location.href='index.jsp'">Log Out</button>
                </li>
            </ul>
        </div>
      </nav>
        <!--NAVBAR END-->
<br><br>
	<div class="container-fluid main">
      <center>
          <div class="txtbox text-center">           
            <legend>About Travel Life</legend>
            <div class="about-section" style="padding-left:2.5vw;">
                <div class="container">
                    <div class="row">
                            <div class="">
                                    <div class="about-title clearfix">
                                            <h1><span>Travel Life</span></h1>
                                            <h3>Created by: Manas Patil </h3>
                                            <p class="about-paddingB">
                                                    At Travel Life we believe in enjoying every aspect of our journeys.<br>
                                                    After all, Our life is also a journey filled with obstracles and barriers.<br>
                                                    As Someone has already said: <br><i><b>STOP WORRYING ABOUT THE POTHOLES IN THE ROAD AND ENJOY THE JOURNEY</b></i>
                                            </p>
                                    </div>	
              			</div>
             		</div>
          		</div>
          	</div>	
          </div>
      </center>  <br><br>
    </div>
    </body>
</html>