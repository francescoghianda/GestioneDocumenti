package it.polimi.gd.controllers;

import it.polimi.gd.Application;
import it.polimi.gd.beans.Document;
import it.polimi.gd.dao.DocumentDao;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/document")
public class DocumentDetailsController extends HttpServlet
{
    private DocumentDao documentDao;

    public DocumentDetailsController()
    {
        super();
    }

    @Override
    public void init()
    {
        documentDao = new DocumentDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            int documentId = Integer.parseInt(req.getParameter("doc"));
            Optional<Document> document = documentDao.findDocumentById(documentId);

            if(!document.isPresent())
            {
                resp.sendError(404, "Document not found!");
                return;
            }

            WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
            webContext.setVariable("doc", document.get());
            webContext.setVariable("version", Application.getVersion());
            Application.getTemplateEngine().process("document-details", webContext, resp.getWriter());
        }
        catch (NumberFormatException e)
        {
            resp.sendError(400, e.getLocalizedMessage());
        }
        catch (SQLException e)
        {
            resp.sendError(500, e.getSQLState());
        }
    }
}
