package it.polimi.gd.dao;

import it.polimi.gd.beans.Document;
import it.polimi.gd.log.Log;
import it.polimi.utils.sql.ConnectionPool;
import it.polimi.utils.sql.PooledConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DocumentDao
{
    private final ConnectionPool connectionPool;
    private final SimpleDateFormat dateFormat;

    public DocumentDao()
    {
        connectionPool = ConnectionPool.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private Document metadataFromResultSet(ResultSet resultSet) throws SQLException
    {
        return new Document(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("creation_date"),
                resultSet.getString("summary"),
                resultSet.getString("type"),
                resultSet.getInt("parent"));
    }

    public Optional<Document> findDocumentById(int documentId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "SELECT id, name, creation_date, parent, summary, type FROM document WHERE id = ?"))
        {
            statement.setInt(1, documentId);

            try(ResultSet resultSet = statement.executeQuery())
            {
                if(!resultSet.next())return Optional.empty();
                return Optional.of(metadataFromResultSet(resultSet));
            }
        }
    }

    public List<Document> findDocumentsByParentId(int parentId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "SELECT id, name, creation_date, parent, summary, type FROM document doc WHERE doc.parent = ?"))
        {
            statement.setInt(1, parentId);

            try(ResultSet resultSet = statement.executeQuery())
            {
                List<Document> metadataList = new ArrayList<>();
                while (resultSet.next())
                    metadataList.add(metadataFromResultSet(resultSet));
                return metadataList;
            }
        }
    }

    public boolean moveDocument(int documentId, int destinationDirectoryId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "UPDATE document SET parent = ? WHERE id = ?"))
        {
            statement.setInt(1, destinationDirectoryId);
            statement.setInt(2, documentId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean exists(String documentName, int parentId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "SELECT id FROM document doc WHERE doc.name = ? AND doc.parent = ?"))
        {
            statement.setString(1, documentName);
            statement.setInt(2, parentId);

            try(ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }

    public int createDocument(String documentName, String summary, String documentType, int parentId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "INSERT INTO document (name, creation_date, summary, type, parent) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {

            String date = dateFormat.format(new Date());

            Log.info(date);

            statement.setString(1, documentName);
            statement.setString(2, date);
            statement.setString(3, summary);
            statement.setString(4, documentType);
            statement.setInt(5, parentId);

            connection.getConnection().setAutoCommit(false);

            if(statement.executeUpdate() == 0)return -1;

            try(ResultSet resultSet = statement.getGeneratedKeys())
            {
                if(!resultSet.next())
                {
                    connection.getConnection().rollback();
                    connection.getConnection().setAutoCommit(true);
                    return -1;
                }
                connection.getConnection().setAutoCommit(true);
                return resultSet.getInt(1);
            }
        }

    }

    public boolean deleteDocument(int documentId) throws SQLException
    {
        try(PooledConnection connection = connectionPool.getConnection();
            PreparedStatement statement = connection.getConnection().prepareStatement(
                    "DELETE FROM document doc WHERE doc.id = ?"))
        {
            statement.setInt(1, documentId);
            return statement.executeUpdate() == 1;
        }
    }

}
