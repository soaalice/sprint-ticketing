package com.itu16.spring_ticketing.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu16.spring_ticketing.models.Admin;
import com.itu16.spring_ticketing.models.Utilisateur;
import com.itu16.spring_ticketing.repositories.AdminRepository;
import com.itu16.spring_ticketing.repositories.UtilisateurRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private AdminRepository adminRepo;

    @GetMapping("/login")
    public String loginForm(
                @ModelAttribute("error") String error,
                Model model) {

        if (error != null && !error.isEmpty()) {
            model.addAttribute("errorMessage", error);
        }

        return "/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Optional<Utilisateur> utilisateurOpt = utilisateurRepo.findByUserName(username);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            if (user.getPwd().equals(password)) {
                authenticateUser(username, "ROLE_USER", request);
                return "redirect:/user/home";
            } else {
                redirectAttributes.addFlashAttribute("error", "Mot de passe incorrect.");
                redirectAttributes.addFlashAttribute("type", "user");
                return "redirect:/login";
            }
        }

        Optional<Admin> adminOpt = adminRepo.findByUserName(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPwd().equals(password)) {
                authenticateUser(username, "ROLE_ADMIN", request);
                return "redirect:/admin/home";
            } else {
                redirectAttributes.addFlashAttribute("error", "Mot de passe incorrect.");
                redirectAttributes.addFlashAttribute("type", "admin");
                return "redirect:/login";
            }
        }

        redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé.");
        return "redirect:/login";
    }


    private void authenticateUser(String username, String role, HttpServletRequest request) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // Définir l’authentification dans le contexte Spring
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // Créer la session HTTP et stocker l’authentification dedans
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    }

    @GetMapping("/user/home")
    public String userHome(Model model) {
        model.addAttribute("userName", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/index";
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("userName", SecurityContextHolder.getContext().getAuthentication().getName());
        return "admin/index";
    }

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}

