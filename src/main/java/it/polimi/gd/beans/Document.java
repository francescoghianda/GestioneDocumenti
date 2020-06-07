package it.polimi.gd.beans;

import java.sql.Date;
import java.sql.Timestamp;

public class Document
{
    private int id;
    private String name;
    private Date creationDate;
    private String summary;
    private String type;
    private int parentId;

    public Document() {}

    public Document(int id, String name, Timestamp creationDate, String summary, String type, int parentId)
    {
        this.id = id;
        this.name = name;
        this.creationDate = new Date(creationDate.getTime());
        this.summary = summary;
        this.type = type;
        this.parentId = parentId;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }
}
