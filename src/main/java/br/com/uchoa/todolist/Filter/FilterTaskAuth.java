package br.com.uchoa.todolist.Filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.uchoa.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if (!servletPath.startsWith("/tasks/")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = request.getHeader("Authorization");
        
        
        var user_password = token.substring("Basic".length()).trim();
        var user_password_decoded = new String(java.util.Base64.getDecoder().decode(user_password));
        String[] credentials = user_password_decoded.split(":");
        String username = credentials[0];
        String password = credentials[1];


        var users = userRepository.findByUsername(username);
        if (users.size() == 0) {
            System.out.println("Usuario não encontrado");
            response.setStatus(401);
            return;
        }else{
            //validar se a senha está correta
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), users.get(0).getPassword());
            if (!result.verified) {
                System.out.println("Senha incorreta");
                response.setStatus(401);
                return;
            }

            request.setAttribute("userId", users.get(0).getId());
            filterChain.doFilter(request, response);
        }

    }

    
}
