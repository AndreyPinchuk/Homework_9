package homework_9;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req,
                            HttpServletResponse res,
                            FilterChain chain) throws IOException, ServletException {

        String timezone = parsTimeParam(req);

        if(isValidTimeZone(timezone)){
            chain.doFilter(req, res);

        } else {
            res.setStatus(400);
            res.setContentType("text/html; charset=utf-8");
            res.getWriter().write("<h1>Invalid timezone</h1>");
            res.getWriter().close();
        }
    }
    private boolean isValidTimeZone(String st){
        String num = st
                .replace("UTC+"," ")
                .replace("UTC-"," ");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <13 ; i++) {
            sb.append(" ").append(i);

            if(num.equals(sb.toString())) return true;

            sb.delete(0,sb.length());
        }
        return false;
    }
    private String parsTimeParam(HttpServletRequest request){
        if(request.getParameterMap().containsKey("timezone")){
            return request.getParameter("timezone");
        }
        return "UTC+0";
    }
}
