package dao.user;

import dto.login.*;
import utils.enums.Role;
import dao.PostgreSQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class UserDAOPostgreSQL implements UserDAO {
	private Connection conn;

	public UserDAOPostgreSQL() throws SQLException {
	    this.conn = PostgreSQLConnection.getInstance().getConnection();
	}
	
	@Override 
	public LoginResponseDTO findByCredentials(LoginRequestDTO requestDTO) {
		String sql = "SELECT * FROM users WHERE login_name = ? AND password = ? AND role = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, requestDTO.getLoginName());
	        stmt.setString(2, requestDTO.getPassword());
	        stmt.setString(3, requestDTO.getRole().name());
	        
	        ResultSet rs = stmt.executeQuery();
	        
	        while(rs.next()) {
	        	LoginResponseDTO response = new LoginResponseDTO();
                response.setUserName(rs.getString("user_name"));
                response.setRole(Role.valueOf(rs.getString("role")));
                return response;
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
