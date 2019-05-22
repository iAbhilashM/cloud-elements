package com.file.api.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.file.api.model.ListFilesResponseModel;
import com.file.api.properties.APIProperties;

public class APIRequest {
	
	APIProperties properties;
	
	public APIRequest(APIProperties apiProperties) {
		properties = apiProperties;
	}

	public ArrayList<ListFilesResponseModel> listFiles(String path) {

		ArrayList<ListFilesResponseModel> responses = null;

		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			URIBuilder builder = new URIBuilder(properties.getListFilesURL());
			builder.addParameter("path", path);
			HttpGet httpGet = new HttpGet(builder.build());
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, properties.getAuthentication());

			HttpResponse response = httpclient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity responseEntity = response.getEntity();

			String responseString = EntityUtils.toString(responseEntity, "UTF-8");

			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

			responses = mapper.readValue(responseString, new TypeReference<List<ListFilesResponseModel>>() {
			});

			System.out.println("List Files :[" + statusCode + "] " + responseString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responses;
	}
	
	public int fileUpload(MultipartFile file) {

		InputStream is = null;
		int statusCode = 0;
		try {
			is = file.getInputStream();
			HttpClient httpclient = HttpClientBuilder.create().build();
			URIBuilder builder = new URIBuilder(properties.getFilesURL());
			builder.addParameter("path", "/" + file.getOriginalFilename());
			HttpPost httppost = new HttpPost(builder.build());
			httppost.setHeader(HttpHeaders.AUTHORIZATION, properties.getAuthentication());

			MultipartEntity entity = new MultipartEntity();
			entity.addPart("file", new InputStreamBody(is, file.getOriginalFilename()));
			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity, "UTF-8");

			System.out.println("[" + statusCode + "] " + responseString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return statusCode;
	}
	
	public Resource fileDownload(String fileName) {
		InputStreamResource inputStreamResource = null;
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			URIBuilder builder = new URIBuilder(properties.getFilesURL());
			builder.addParameter("path", "/" + fileName);
			HttpGet httpGet = new HttpGet(builder.build());
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, properties.getAuthentication());

			HttpResponse response = httpclient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity responseEntity = response.getEntity();

			inputStreamResource = new InputStreamResource(responseEntity.getContent());

			System.out.println("Download : [" + statusCode + "] ");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputStreamResource;
	}
	
	public void fileDelete(String fileName) {
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			URIBuilder builder;

			builder = new URIBuilder(properties.getFilesURL());

			builder.addParameter("path", "/" + fileName);
			HttpDelete httpDelete = new HttpDelete(builder.build());
			httpDelete.setHeader(HttpHeaders.AUTHORIZATION, properties.getAuthentication());

			HttpResponse response = httpclient.execute(httpDelete);

			int statusCode = response.getStatusLine().getStatusCode();

			System.out.println("Delete : [" + statusCode + "] ");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
