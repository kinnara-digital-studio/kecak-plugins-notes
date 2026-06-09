package com.kinnara.kecakplugins.notes.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notes {
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String id;
    private final String recordId;
    private final String username;
    private final String displayName;
    private final String note;
    private final Date date;
    private final NoteType type;

    public Notes(String id, String recordId, String username, String displayName, String note, Date date, NoteType type) {
        this.id = id;
        this.recordId = recordId;
        this.username = username;
        this.displayName = displayName;
        this.note = note;
        this.date = date;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNote() {
        return note;
    }

    public Date getDate() {
        return date;
    }

    public NoteType getType() {
        return type;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("record_id", recordId);
        jsonObject.put("username", username);
        jsonObject.put("name", displayName);
        jsonObject.put("date", DATE_FORMAT.format(date));
        jsonObject.put("notes", note);
        jsonObject.put("type", type.name());
        return jsonObject;
    }
}
