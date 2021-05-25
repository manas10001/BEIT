/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author manas
 */
public class RegisterHandler extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
        
        PrintWriter out = response.getWriter();
        String username=null,password=null,repass=null, email=null;
        
        try{
            //CREATING SESSION
            HttpSession session = request.getSession();
            //connecting to databse
            Class.forName("com.mysql.jdbc.Driver"); 
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_system","root",""); 
            
            username = request.getParameter("login");
            password = request.getParameter("pass");
            repass = request.getParameter("repass");
            email = request.getParameter("mail");
            
            //PASSWORD AND RETYPE PASSWORD CHECK
            if(!password.equals(repass))
            {
                out.println("<script>alert('Password and retyped password dont watch!');</script>");
                out.println("<script>window.location.href = 'register.jsp'</script>");
            }
            
            Statement stmt = con.createStatement();
            String sql = "insert into users values('"+username+"','"+email+"','"+password+"')";
            stmt.executeUpdate(sql);
            
            response.sendRedirect("login.jsp");
             
        }catch(Exception e){
            System.out.println("Exception on register: "+e.toString());
        }finally{
            out.close();
        }
    }

}
