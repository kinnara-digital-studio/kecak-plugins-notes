package com.kinnara.kecakplugins.notes.model;

import org.joget.apps.form.model.*;

public interface NotesLoadBinder extends FormLoadBinder, FormLoadMultiRowElementBinder {
    Notes toNote(FormRow row);
}
