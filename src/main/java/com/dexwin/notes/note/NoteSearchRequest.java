package com.dexwin.notes.note;

public class NoteSearchRequest {

    private String query;
    private String tags;
    private String sortBy = "updatedAt";
    private String direction = "DESC";
    private int page = 0;
    private int size = 10;

    public String getQuery() { return query; }

    public void setQuery(String query) { this.query = query; }

    public String getTags() { return tags; }

    public void setTags(String tags) { this.tags = tags; }

    public String getSortBy() { return sortBy; }

    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getDirection() { return direction; }

    public void setDirection(String direction) { this.direction = direction; }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }
}
