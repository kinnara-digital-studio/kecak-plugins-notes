package com.kinnara.kecakplugins.notes.model;

import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.apps.form.model.FormStoreMultiRowElementBinder;

public interface NotesStoreBinder extends FormStoreBinder, FormStoreMultiRowElementBinder {
    FormRow fromNote(Notes note);
}
