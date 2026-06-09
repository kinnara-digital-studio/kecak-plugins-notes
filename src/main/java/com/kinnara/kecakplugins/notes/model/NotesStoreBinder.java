package com.kinnara.kecakplugins.notes.model;

import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormStoreMultiRowElementBinder;

public interface NotesStoreBinder extends FormStoreMultiRowElementBinder {
    FormRow fromNote(Notes note);
}
