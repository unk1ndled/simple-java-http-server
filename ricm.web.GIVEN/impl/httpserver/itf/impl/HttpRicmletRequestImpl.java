package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {
    private Map<String, String> parameters;
    private Map<String, String> cookies;
    private static final Map<String, Session> sessions = new HashMap<>();
    private static final Map<String, HttpRicmlet> ricmletInstances = new HashMap<>();

    
    public static  Map<String, Session> getSessions(){
    	return sessions;
    }
    
    
    public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
        super(hs, method, ressname, br);
        this.parameters = new HashMap<>();
        this.cookies = new HashMap<>();

        
        parseQueryParameters();
        parseCookies(br);
    }
    
    private void initSession() {
        String id = generateUniqueId();
        cookies.put("session-id", id);
        sessions.put(id, new Session(id));
    }

    private String generateUniqueId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (sessions.containsKey(id));
        return id;
    }

    
    
    private void parseQueryParameters() {
        if (m_ressname.contains("?")) {
            String[] parts = m_ressname.split("\\?", 2);
            m_ressname = parts[0];
            String query = parts[1];

            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
            
        }
    }

    private void parseCookies(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Cookie:")) {
                String cookieHeader = line.substring(7).trim();
                for (String cookie : cookieHeader.split(";")) {
                    String[] keyValue = cookie.trim().split("=");
                    if (keyValue.length == 2) {
                        cookies.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        if (!cookies.containsKey("session-id")) {
        	initSession();
        }
    }
    
    @Override
    public void process(HttpResponse resp) throws Exception {
        HttpRicmletResponseImpl ricmletResponse = new HttpRicmletResponseImpl(resp);
        String ricmletClass = m_ressname.replace("/", ".");
        String className = ricmletClass.split("ricmlets.", 2)[1];

        try {
            HttpRicmlet ricmlet = ricmletInstances.computeIfAbsent(className, clsName -> {
                try {
                    Class<?> cls = Class.forName(clsName);
                    System.out.println("Class Loaded: " + cls.getName());
                    return (HttpRicmlet) cls.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Error loading Ricmlet class: " + e);
                    return null;
                }
            });
            
            if (ricmlet != null) {
                ricmlet.doGet(this, ricmletResponse);
            } else {
                resp.setReplyError(500, "Failed to load Ricmlet");
            }
        } catch (Exception e) {
            System.out.println(e);
            resp.setReplyError(500, "Internal Server Error");
        }
    }
    
    @Override
    public HttpSession getSession() {
        String sessionId = cookies.get("session-id");
        Session session = sessions.get(sessionId);
        
        if (session == null || session.isExpired()) {
            sessions.remove(sessionId); 
            initSession();
            System.out.println("restored");
            return getSession(); 
        }
        
        session.refresh(); 
        return session;    
    }

    @Override
    public String getArg(String name) {
        return parameters.get(name);
    }

    @Override
    public String getCookie(String name) {
        return cookies.get(name);
    }
}
