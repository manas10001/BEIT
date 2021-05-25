<jsp>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <a href="./index.jsp" class="navitem">Home</a>
                <a href="./interview_tips.jsp" class="navitem active">Interview Tips</a>
                <a href="./categories.jsp" class="navitem">Categories</a>
                <a href="./profiles.jsp" class="navitem">Profiles</a>
                <%
                    String uname = null;
                    try{
                            uname = (String) session.getAttribute("name");
                            if(uname == null){
                            %>
                                <a href="./login.jsp" class="navitem">Login/Register</a>
                            <%
                            }else{
                                uname = "";
                            %>
                                <a href="./logout.jsp" class="navitem">Logout</a>
                            <%}
                    }catch(Exception e){
                        System.out.println(""+e.toString());
                    }
                %>
            </ul>    
        </div>

        <div class="content">
            <div class="card card-xlg">
                <div class="card-body">
                    <div class="card-title" style="text-align: center;">
                        <strong>How to Prepare for an Interview!</strong>
                    </div>
                    <hr>
                    <div class="card-content">
                        <strong>Prepare for an interview:</strong>
                        <ol>
                            <li>Carefully examine the job description</li>
                                <p>
                                    During your prep work, you should use the employer’s posted job description as a guide. The job description is a list of the qualifications, qualities and background the employer is looking for in an ideal candidate. The more you can align yourself with these details, the more the employer will be able to see that you are qualified. The job description may also give you ideas about questions the employer may ask throughout the interview.
                                </p>
                            <li>Consider why you are interviewing and your qualifications</li>
                               <p>
                                    Before your interview, you should have a good understanding of why you want the job and why you’re qualified. You should be prepared to explain your interest in the opportunity and why you’re the best person for the role.
                                </p>
                            <li>Perform research on the company and role</li>
                                <p>
                                    Researching the company you’re applying to is an important part of preparing for an interview. Not only will it help provide context for your interview conversations, but it will also help you when preparing thoughtful questions for your interviewers.
                                    <br>
                                    Researching the company and role as much as possible will give you an edge over the competition. Not only that, but fully preparing for an interview will help you remain calm so that you can be at your best. Here are a few things you should know before you walk into your interview:
                                    <ul>
                                        <li>Research The product or service.</li>
                                        <li>Research the Role</li>
                                        <li>Research the company culture</li>
                                        <li>Read past interview experiences</li>
                                    </ul>
                                     
                                </p>
                            <li>Consider your answers to common interview questions</li>
                                <p>
                                    While you won’t be able to predict every question you’ll be asked in an interview, there are a few common questions you can plan answers for. You might also consider developing an elevator pitch that quickly describes who you are, what you do and what you want.
                                    <br>
                                    There are some jobs that may involve a test or evaluation during the interview process. For example, if you are interviewing for a computer programming, development or analytics role, you might also be asked to write or evaluate lines of code. It might be helpful to consult with colleagues in the industry for examples of tests they’ve been given to prepare. 
                                </p>
                            <li>Practice your speaking voice and body language</li>
                                <p>
                                    It’s important to make a positive and lasting impression during the interview process. You can do this by practicing a confident, strong speaking voice and friendly, open body language. While these might come naturally to you, you might also want to spend time performing them with trusted friends or family or in front of a mirror. Pay special attention to your smile, handshake and stride.
                                </p>
                            <li>Prepare several thoughtful questions for the interviewer(s)</li>
                                <p>
                                    Many employers feel confident about candidates who ask thoughtful questions about the company and the position. You should take time before the interview to prepare several questions for your interviewer(s) that show you’ve researched the company and are well-versed about the position.
                                </p>
                            <li>Sell yourself</li>
                                <p>
                                    One of the biggest challenges in an interview is selling yourself. Most people are uncomfortable with this idea, but presenting yourself accurately and positively doesn't have to feel like a sale. The truth is that you do have professional skills and experiences that may set you apart from other applicants, so it's acceptable and expected for you to acknowledge them to your potential employer.
                                </p>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    </body>
</jsp>