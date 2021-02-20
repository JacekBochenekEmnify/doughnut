package com.odde.doughnut.controllers;

import com.odde.doughnut.models.Note;
import com.odde.doughnut.models.User;
import com.odde.doughnut.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class ReviewController {
    private final UserRepository userRepository;

    public ReviewController(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping("/review")
    public String review(Principal principal, Model model) {
        User user = userRepository.findByExternalIdentifier(principal.getName());
        List<Note> notes = user.getNotesInDescendingOrder();
        model.addAttribute("notes", notes);
        return "review";
    }
}
