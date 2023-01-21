package homework_9;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    private IWebApplication application;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        application = JavaxServletWebApplication.buildApplication(getServletContext());

        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");

        StringBuilder timezone = new StringBuilder(parsTimeParam(req));

        if(req.getParameterMap().containsKey("timezone")){

            resp.addCookie(new Cookie("lastTimezone",parsTimeParam(req)));

        } else {
            Cookie[] cookies =req.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {
                    timezone.delete(0,timezone.length());
                    timezone.append(cookie.getValue());
                }
            }
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of(
                        "dateAndTime",
                        getTime(timezone.toString())
                )
        );

        engine.process("time",simpleContext,resp.getWriter());
        resp.getWriter().close();
    }
    private String parsTimeParam(HttpServletRequest request){
        if(request.getParameterMap().containsKey("timezone")){
            return request.getParameter("timezone");
        }
        return "UTC";
    }

    public String getTime(String timezone){
        String b =
                ZonedDateTime.now(ZoneId.of(timezone))
                        .format(DateTimeFormatter
                                .ofPattern("dd-MM-yyyy HH:mm:ss z"));
        return b;
    }
}
