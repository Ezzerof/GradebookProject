package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.models.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService service;
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@GetMapping(value = "/delete/student/{id}")//PathVariable will add id
	public String deleteStudent(@PathVariable int id, Model m) {

		if (!service.checkIfStudentIsNull(id)) {
			return "error";
		}

		service.deleteStudent(id);

		//getting updated list of students
		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@PostMapping(value = "/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
		service.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());

		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);

		return "index";
	}

	@GetMapping("/studentInformation/{id}")
	public String studentInformation(@PathVariable int id, Model m) {
		if (!service.checkIfStudentIsNull(id))
			return "error";
		service.configureStudentInformationModel(id, m);
		return "studentInformation";
	}

	@PostMapping(value = "/grades")
	public String createGrade(@RequestParam("grade") double grade,
							  @RequestParam("gradeType") String gradeType,
							  @RequestParam("studentId") int studentId, Model m) {
		if (!service.checkIfStudentIsNull(studentId))
			return "error";

		boolean success = service.createGrade(grade, studentId, gradeType);

		if (!success)
			return "error";

		service.configureStudentInformationModel(studentId, m);

		return "studentInformation";
	}

	@GetMapping("/grades/{id}/{gradeType}")
	public String deleteGrade(@PathVariable("id") int id,
							  @PathVariable("gradeType") String gradeType, Model m) {

		int studentId = service.deleteGrade(id, gradeType);

		if (studentId == 0) {
			return "error";
		}

		service.configureStudentInformationModel(id, m);

		return "studentInformation";
	}

}
