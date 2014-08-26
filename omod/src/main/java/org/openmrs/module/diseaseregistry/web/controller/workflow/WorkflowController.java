/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.diseaseregistry.web.controller.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptWord;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.diseaseregistry.api.DiseaseRegistryService;
import org.openmrs.module.diseaseregistry.api.model.DRConcept;
import org.openmrs.module.diseaseregistry.api.model.DRProgram;
import org.openmrs.module.diseaseregistry.api.model.DRWorkflow;
import org.openmrs.module.diseaseregistry.web.controller.common.ConceptWordEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The main controller.
 */
@Controller
public class WorkflowController {

	protected final Log log = LogFactory.getLog(getClass());

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class,
				new CustomBooleanEditor("true", "false", true));
		binder.registerCustomEditor(org.openmrs.Concept.class,
				new ConceptWordEditor());
	}

	@ModelAttribute("programs")
	public List<DRProgram> getPrograms() {
		
		return new ArrayList<DRProgram>(Context.getService(DiseaseRegistryService.class).getPrograms(DiseaseRegistryService.NOT_INCLUDE_RETIRED));
	}

	@RequestMapping(value = "/module/diseaseregistry/workflow.list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		
		DiseaseRegistryService drs = Context.getService(DiseaseRegistryService.class);
		DRProgram program = drs.getProgram(1);
		DRWorkflow workflow = new DRWorkflow();
		workflow.setProgram(program);
		workflow.setName("testing mapping");
		workflow.setDescription("description");
		workflow = drs.saveWorkflow(workflow);	
		
		DRConcept concept = new DRConcept();
		concept.setWorkflow(workflow);		
		concept.setConcept(Context.getConceptService().getConcept(4210));
		drs.saveConcept(concept);
		
		return "/module/diseaseregistry/workflow/workflowList";
	}

	@RequestMapping(value = "/module/diseaseregistry/workflow.form", method = RequestMethod.GET)
	public String form(@ModelAttribute("workflow") ProgramWorkflow workflow,
			BindingResult bindingResult, ModelMap model,
			@RequestParam(value = "id", required = false) Integer id) {

		model.addAttribute("user", Context.getAuthenticatedUser());
		return "/module/diseaseregistry/workflow/workflowForm";
	}

	@RequestMapping(value = "/module/diseaseregistry/workflow.form", method = RequestMethod.POST)
	public String form(@ModelAttribute("workflow") ProgramWorkflow workflow,
			BindingResult bindingResult, ModelMap model) {

		ProgramWorkflowService ps = Context.getProgramWorkflowService();
		ps.createWorkflow(workflow);
		model.addAttribute("user", Context.getAuthenticatedUser());
		return "redirect:/module/diseaseregistry/workflow.list";
	}
}
