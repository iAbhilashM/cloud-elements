package com.file.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.file.api.model.HRefModel;
import com.file.api.model.ListFilesResponseModel;
import com.file.api.properties.APIProperties;
import com.file.api.request.APIRequest;

@RestController
@EnableConfigurationProperties(APIProperties.class)
public class APIController {

	@Autowired
	public APIProperties properties;

	@Bean
	public APIRequest request() {
		return new APIRequest(properties);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView upload() {
		return new ModelAndView("upload");
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/files/list", method = RequestMethod.GET)
	public ModelAndView listFiles(Model model) {

		List<ListFilesResponseModel> responses = new ArrayList<>();
		List<HRefModel> uris = new ArrayList<>();
		responses = request().listFiles("/");
		for (ListFilesResponseModel response : responses) {
			HRefModel href = new HRefModel();

			href.setHrefFile(MvcUriComponentsBuilder
					.fromMethodName(APIController.class, "downloadFile", response.getName()).build().toString());

			href.setHrefFileText(response.getName());
			href.setHrefDelete(MvcUriComponentsBuilder
					.fromMethodName(APIController.class, "deleteFile", response.getName()).build().toString());
			uris.add(href);

		}

		model.addAttribute("listOfEntries", uris);
		return new ModelAndView("file_list :: urlFileList", (Map<String, ?>) model);
	}
	
	@PostMapping("/uploadFile")
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		request().fileUpload(file);

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
		Resource resource = request().fileDownload(fileName);
		String contentType = null;
		byte[] bytes = null;
		
		try {
			bytes = StreamUtils.copyToByteArray(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		contentType = new MimetypesFileTypeMap().getContentType(fileName);

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(bytes);
	}

	@RequestMapping(value = "/deleteFile/{fileName:.+}", method = RequestMethod.GET)
	public ModelAndView deleteFile(@PathVariable String fileName) {
		request().fileDelete(fileName);
		return new ModelAndView("redirect:/");
	}

}
