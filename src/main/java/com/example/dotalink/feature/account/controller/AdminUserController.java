package com.example.dotalink.feature.account.controller;

import com.example.dotalink.feature.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "Admin", description = "Administrative pages")
public class AdminUserController {

    private final UserService adminUserService;

    public AdminUserController(UserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/admin/users")
    @Operation(summary = "Open admin users page", description = "Returns paginated HTML list of all users for administrators")
    public String users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        model.addAttribute("usersPage", adminUserService.getUsersPage(pageable));
        return "admin/users";
    }
}
