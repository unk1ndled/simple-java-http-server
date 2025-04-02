package httpserver.itf.impl;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl  extends HttpResponseImpl implements HttpRicmletResponse{



	    public HttpRicmletResponseImpl(HttpResponse resp) {
	        super((HttpResponseImpl) resp); // Call HttpResponse constructor
	    }


	    @Override
	    public void setCookie(String name, String value) {
	        // Cookies should be sent in HTTP headers before writing the response body
	        getM_ps().println("Set-Cookie: " + name + "=" + value + "; Path=/");
	    }
}
