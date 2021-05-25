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
import java.sql.ResultSet;
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
public class LoginHandler extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
            processRequest(request, response);
        
        PrintWriter out = response.getWriter();
        String username = null, password = null;
        
        try{
            //CREATING SESSION
            HttpSession session = request.getSession();
            //connecting to databse
            Class.forName("com.mysql.jdbc.Driver"); 
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_system","root",""); 
            
            username = request.getParameter("login");
            password = request.getParameter("pass");
            
            Statement stmt = con.createStatement();
            String sql = "select * from users where username='"+username+"' and password='"+password+"'";
            ResultSet rs = stmt.executeQuery(sql);
            
            if(rs.next()){
                session.setAttribute("name", username);
                session.setAttribute("email", rs.getString("email"));
                response.sendRedirect("index.jsp");
            }
            else{
                System.out.println("Login failed ");
                response.sendRedirect("login.jsp");
            }
        }catch(Exception e){
            System.out.println("Exception on login: "+e.toString());
        }finally{
            out.close();
        }
    }

}
