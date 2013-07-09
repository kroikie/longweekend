package publicholidays;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class SpecialHttpServletRequest extends HttpServletRequestWrapper{
    
    
    private final Map<String, String[]> parameters;
    
    public SpecialHttpServletRequest(final HttpServletRequest request) {
        super(request);
        parameters = new TreeMap<String, String[]>();
        parameters.putAll(request.getParameterMap());
    }

    @Override
    public String getParameter(final String name) {
        String[] strings = getParameterMap().get(name);
        if (strings != null) {
            return strings[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(final String name) {
        return getParameterMap().get(name);
    }
    
    public void addParameter(String name, String... value ) {
           parameters.put(name, value);
   }
}
