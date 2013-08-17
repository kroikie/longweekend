package publicholidays;

import entity.DateEntry;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;

/**
 *
 * @author Jonathan
 */
public class HolidaysFrom extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        DateEntry start = new DateEntry(request.getParameter("startDate")),
                end = new DateEntry(yearEnd());
        Database database = new Database();
        List<DateEntry> list = database.getHolidays();
        List<DateEntry> holidaysFrom = new ArrayList<DateEntry>();
        try {
            for(DateEntry d: list){
                if(d.compareTo(start) >= 0 && d.compareTo(end) <= 0){
                   holidaysFrom.add(d);
                }
            }
            JSONArray jsonArray = new JSONArray(holidaysFrom);
            jsonArray.write(out);
        } finally {            
            out.close();
        }
    }
    
    private String yearEnd() {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        return String.format("%d-12-31", calendar.get(GregorianCalendar.YEAR));
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
