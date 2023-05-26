package com.practice.controller;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.practice.entity.Notes;
import com.practice.entity.UserDtls;
import com.practice.repository.NotesRepository;
import com.practice.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotesRepository notesRepository;

	@ModelAttribute
	public void addCommnData(Principal p, Model m) {
		String email = p.getName();
		UserDtls user = userRepository.findByEmail(email);
		m.addAttribute("user", user);
	}

	@GetMapping("/addNotes")
	public String getHome() {

		return "user/add-notes";

	}

	@GetMapping("/viewNotes/{page}")
	public String getViewNotes(@PathVariable int page, Model m, Principal p) {

		String email = p.getName();
		UserDtls user = userRepository.findByEmail(email);
		Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
		Page<Notes> pages = notesRepository.findNotesByUser(user.getId(), pageable);

		m.addAttribute("pageNo", page);
		m.addAttribute("totalPages", pages.getTotalPages());
		m.addAttribute("pages", pages);
		m.addAttribute("totalNotes", pages.getTotalElements());

		return "user/view-notes";

	}

	@GetMapping("/editNotes/{id}")
	public String getEditNotes(@PathVariable int id, Model m) {

		Optional<Notes> notes = notesRepository.findById(id);
		if (notes != null) {
			Notes n = notes.get();
			m.addAttribute("note", n);
		}
		return "user/edit-notes";

	}

	@PostMapping("/updateNotes")
	public String updateNotes(@ModelAttribute Notes notes, HttpSession session, Principal p) {

		String email = p.getName();
		UserDtls user = userRepository.findByEmail(email);
		notes.setUserDtls(user);
		Notes updated = notesRepository.save(notes);
		if (updated != null) {
			session.setAttribute("msg", "Notes Updated Sucessfully");
		} else {
			session.setAttribute("msg", "Something wrong on server");
		}
		return "redirect:/user/viewNotes/0";
	}

	@GetMapping("/deletNotes/{id}")
	public String deletNotes(@PathVariable int id, HttpSession session) {
		Optional<Notes> notes = notesRepository.findById(id);
		if (notes != null) {
			notesRepository.delete(notes.get());
			session.setAttribute("msg", "Notes Deleted Sucessfully");
		} else {
			session.setAttribute("msg", "Something wrong on server");
		}
		return "redirect:/user/viewNotes/0";
	}

	@GetMapping("/viewProfile")
	public String getviewProfile() {

		return "user/view-profile";

	}

	@PostMapping("/saveNotes")
	public String addNotes(@ModelAttribute Notes notes, HttpSession session, Principal p) {

		String email = p.getName();
		UserDtls user = userRepository.findByEmail(email);
		notes.setUserDtls(user);
		Notes n = notesRepository.save(notes);

		if (n != null) {
			session.setAttribute("msg", "Notes Added Sucessfully");
		} else {
			session.setAttribute("msg", "Something wrong on server");
		}
		return "redirect:/user/addNotes";
	}

}
