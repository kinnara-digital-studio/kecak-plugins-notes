package com.kinnara.kecakplugins.notes.model;

import com.kinnara.kecakplugins.notes.form.NotesElement;
import org.joget.apps.form.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface NotesLoadBinder extends FormLoadBinder, FormLoadMultiRowElementBinder {
    @Override
    default FormRowSet load(Element element, String primaryKey, FormData formData) {
        List<Notes> notes = loadNotes(element, primaryKey, formData);
        FormRowSet result = Optional.ofNullable(notes)
                .stream()
                .flatMap(Collection::stream)
                .map(n -> new FormRow() {{
                    setId(n.getId());
                    setDateCreated(n.getDate());
                    setProperty(NotesElement.FIELD_RECORD_ID, n.getRecordId());
                    setProperty(NotesElement.FIELD_NOTE, n.getNote());
                    setProperty(NotesElement.FIELD_USERNAME, n.getUsername());
                    setProperty(NotesElement.FIELD_DISPLAY_NAME, n.getDisplayName());
                    setProperty(NotesElement.FIELD_NOTE_TYPE, n.getType().name());
                }}).collect(Collectors.toCollection(FormRowSet::new));

        result.setMultiRow(true);

        return result;
    }

    List<Notes> loadNotes(Element element, String primaryKey, FormData formData);
}
