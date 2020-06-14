package it.polimi.gd.controllers;

import it.polimi.gd.Application;
import it.polimi.gd.beans.Directory;
import it.polimi.gd.beans.Document;
import it.polimi.gd.dao.DirectoryDao;
import it.polimi.gd.dao.DocumentDao;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/documents")
public class DocumentController extends HttpServlet
{

    private DocumentDao documentDao;
    private DirectoryDao directoryDao;

    public DocumentController()
    {
        super();
    }

    @Override
    public void init()
    {
        documentDao = new DocumentDao();
        directoryDao = new DirectoryDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            int directoryId = Integer.parseInt(req.getParameter("dir"));

            Optional<Directory> directory = directoryDao.findDirectoryById(directoryId);

            if(!directory.isPresent())
            {
                resp.sendError(404, "Directory not found.");
                return;
            }

            List<Document> documents = documentDao.findDocumentsByParentId(directoryId);

            WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());

            webContext.setVariable("version", Application.getVersion());
            webContext.setVariable("dir", directory.get());
            webContext.setVariable("documents", documents);

            Application.getTemplateEngine().process("documents", webContext, resp.getWriter());
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
