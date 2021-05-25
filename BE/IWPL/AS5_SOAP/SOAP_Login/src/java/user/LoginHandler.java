/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author manas
 */
@WebService
public class LoginHandler {
    @WebMethod
    public boolean validateLogin(String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_system","root","");
			
			Statement stmt = con.createStatement();
                        String sql = "select * from users where username='"+username+"' and password='"+password+"'";
                        ResultSet rs = stmt.executeQuery(sql);
			System.out.println(username+" "+password);
			if(rs.next()) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
