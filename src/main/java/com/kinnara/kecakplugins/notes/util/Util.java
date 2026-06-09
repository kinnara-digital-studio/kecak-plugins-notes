package com.kinnara.kecakplugins.notes.util;

import com.kinnara.kecakplugins.notes.model.Notes;
import org.json.JSONException;
import org.json.JSONObject;

public final class Util {
    public JSONObject toJson(Notes note) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", note.getId());
        jsonObject.put("record_id", note.getRecordId());
        jsonObject.put("username", note.getUsername());
        jsonObject.put("name", note.getDisplayName());
        jsonObject.put("date", note.getDate());
        jsonObject.put("notes", note.getNote());
        jsonObject.put("type", note.getType());
        return jsonObject;
    }
}
