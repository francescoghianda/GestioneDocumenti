package it.polimi.gd.controllers;

import it.polimi.gd.Application;

import it.polimi.gd.beans.Directory;
import it.polimi.gd.beans.Document;
import it.polimi.gd.dao.DirectoryDao;
import it.polimi.gd.dao.DocumentDao;
import it.polimi.utils.file.DirectoriesTree;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@WebServlet("")
public class HomeController extends HttpServlet
{
    private DirectoryDao directoryDao;
    private DocumentDao documentDao;

    public HomeController()
    {
        super();
    }

    @Override
    public void init() throws ServletException
    {
        directoryDao = new DirectoryDao();
        documentDao = new DocumentDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());

        try
        {
            int documentId = Integer.parseInt(Objects.toString(req.getParameter("move-doc"), "0"));
            boolean moveDocument = documentId != 0;

            Optional<Document> document = moveDocument ? documentDao.findDocumentById(documentId) : Optional.empty();

            if(moveDocument && !document.isPresent())
            {
                resp.sendError(400, "Document not found!");
                return;
            }

            List<Directory> directories = directoryDao.findAll();

            DirectoriesTree directoriesTree = DirectoriesTree.build(directories);

            webContext.setVariable("version", Application.getVersion());
            webContext.setVariable("tree", directoriesTree);
            webContext.setVariable("moveDocument", moveDocument);

            if(moveDocument)
            {
                webContext.setVariable("doc", document.orElse(null));
                webContext.setVariable("subDirectory", directoriesTree.findFirst(dir -> dir.getId() == document.get().getParentId()).get().getValue());
            }

            Application.getTemplateEngine().process("home", webContext, resp.getWriter());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            resp.sendError(500, "SQL error!");
        }
    }
}
