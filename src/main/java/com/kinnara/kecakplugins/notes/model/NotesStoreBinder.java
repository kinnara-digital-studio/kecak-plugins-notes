package com.kinnara.kecakplugins.notes.model;

import com.kinnara.kecakplugins.notes.form.NotesElement;
import org.joget.apps.form.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface NotesStoreBinder extends FormStoreBinder, FormStoreMultiRowElementBinder {
    @Override
    default FormRowSet store(Element element, FormRowSet rowSet, FormData formData){
        List<Notes> notes = Optional.ofNullable(rowSet)
                .stream()
                .flatMap(Collection::stream)
                .map(r -> new Notes(
                        r.getId(),
                        r.getProperty(NotesElement.FIELD_RECORD_ID),
                        r.getProperty(NotesElement.FIELD_USERNAME),
                        r.getProperty(NotesElement.FIELD_DISPLAY_NAME),
                        r.getProperty(NotesElement.FIELD_NOTE),
                        r.getDateCreated(),
                        NoteType.valueOf(r.getProperty(NotesElement.FIELD_NOTE_TYPE)),
                        Integer.parseInt(r.getProperty(NotesElement.FIELD_ORDER))
                ))
                .collect(Collectors.toList());

        FormRowSet result = Optional.ofNullable(storeNotes(element, notes, formData))
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
                }})
                .collect(Collectors.toCollection(FormRowSet::new));

        result.setMultiRow(true);

        return result;
    }

    List<Notes> storeNotes(Element element, List<Notes> notes, FormData formData);
}
