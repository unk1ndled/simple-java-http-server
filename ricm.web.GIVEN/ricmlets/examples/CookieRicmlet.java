package examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class CookieRicmlet implements httpserver.itf.HttpRicmlet{
	boolean f = true;

	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {

		
		String myFirstCookie = req.getCookie("MyFirstCookie");

		resp.setReplyOk();
		resp.setContentType("text/html");
		
		if (myFirstCookie == null) {
			resp.setCookie("MyFirstCookie", "1");
//			System.out.println("resetting");

		}
		else {
//			System.out.println("incrementing");

			int n =  Integer.valueOf(myFirstCookie);
				// modify the cookie's value each time the ricmlet is invoked
				resp.setCookie("MyFirstCookie", new Integer(n+1).toString());
		}
	
		
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> MyFirstCookie " + req.getCookie("MyFirstCookie") + "<br>");
		ps.println("</H4></BODY></HTML>");
		ps.println();
	}
}
   