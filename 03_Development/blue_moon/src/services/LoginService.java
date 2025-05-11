package services;

import dao.UserDAO;
import dto.LoginDTO;
import models.User;
import exception.*;

public class LoginService {
	private final UserDAO userDAO = new UserDAO();

    public User authenticate(LoginDTO dto) throws AuthenticationException {
        User user = userDAO.findByCredentials(dto);

        if (user == null) {
            throw new AuthenticationException("Sai tên đăng nhập hoặc mật khẩu. Hãy kiểm tra lại!");
        }

        return user;
    }
}
