package httpserver.itf.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	public void process(HttpResponse resp) throws Exception {
		 // Ensure method is GET
        if (!"GET".equalsIgnoreCase(getMethod())) {
            resp.setReplyError(405, "Method Not Allowed");
            return;
        }
        
        // Determine the file path
        String filePath = m_hs.getFolder() + getRessname();
        File file = new File(filePath);

        // Check if the file exists 
        if (!file.exists() || file.isDirectory()) {
            resp.setReplyError(404, "File Not Found");
            return;
        }
        
        // Read the file content
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String contentType = getContentType(filePath);
        
        // Set response headers
        resp.setReplyOk();
        resp.setContentLength((fileContent.length));
        resp.setContentType(contentType);
        
        // Send file content in response
        resp.beginBody().write(fileContent);

	}

}
