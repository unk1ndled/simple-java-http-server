package httpserver.itf.impl;

import java.util.HashMap;
import java.util.Map;

import httpserver.itf.HttpSession;

public class Session implements HttpSession{

	private String id;
	private Map<String,Object> items ;
    private long lastAccessedTime;
    private static final long EXPIRATION_TIME = 1 * 60 * 1000; // une minute

	
	public Session(String SessionId) {
		this.id = SessionId;
        this.lastAccessedTime = System.currentTimeMillis();

		items = new HashMap<String, Object>();
	}

	public String getId() {
		return id;
	}

	@Override
	public Object getValue(String key) {
		return items.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		items.put(key, value);
	}
	
    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessedTime > EXPIRATION_TIME;
    }

    public void refresh() {
        this.lastAccessedTime = System.currentTimeMillis();
    }
	

}
