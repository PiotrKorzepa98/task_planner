package com.example.spring_boot_task_planner.interceptor;

import com.example.spring_boot_task_planner.entity.User;
import com.example.spring_boot_task_planner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BannedUserInterceptor implements HandlerInterceptor {

  private final UserService userService;

  public BannedUserInterceptor(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean preHandle(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull Object handler)
      throws Exception {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {

      String email = auth.getName();
      User user = userService.findByEmail(email).orElse(null);

      if (user != null && user.isBanned()) {
        response.sendRedirect("/");
        return false;
      }
    }
    return true;
  }
}
