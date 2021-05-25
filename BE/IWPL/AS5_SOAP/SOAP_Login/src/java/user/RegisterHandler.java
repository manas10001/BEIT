/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author manas
 */
@WebService
public class RegisterHandler {
    
    @WebMethod
    public boolean registerUser(String username, String password, String email){
        try {
            //connecting to databse
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_system","root",""); 
            
            Statement stmt = con.createStatement();
            String sql = "insert into users values('"+username+"','"+email+"','"+password+"')";
            stmt.executeUpdate(sql);
            return true;
            
        } catch (Exception ex) {
            Logger.getLogger(RegisterHandler.class.getName()).log(Level.SEVERE, null, ex);
            
        }
            
        return false;  
            
    }
}
