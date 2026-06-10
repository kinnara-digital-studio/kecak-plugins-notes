package com.kinnara.kecakplugins.notes.form;

import com.kinnara.kecakplugins.notes.model.NoteType;
import com.kinnara.kecakplugins.notes.model.Notes;
import com.kinnara.kecakplugins.notes.model.NotesLoadBinder;
import com.kinnara.kecakplugins.notes.model.NotesStoreBinder;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;

import java.util.Locale;
import java.util.ResourceBundle;

public class NotesBinder extends FormBinder implements NotesLoadBinder, NotesStoreBinder {

    private final static String LABEL = "Notes Binder";

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/NotesBinder.json", null, false, "");
    }

    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        String notesFormDefId = getPropertyString("notesFormDefId");
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null || notesFormDefId.isEmpty()){
            return rowSet;
        }

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        try {
            formDataDao.saveOrUpdate(notesForm, rowSet);
        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "save failed, error: " + e);
        }

        return rowSet;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public String getVersion() {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        ResourceBundle resourceBundle = pluginManager.getPluginMessageBundle(getClassName(), "/messages/BuildNumber");
        String buildNumber = resourceBundle.getString("buildNumber");
        return buildNumber;
    }

    @Override
    public String getDescription() {
        return getClass().getPackage().getImplementationTitle();
    }

    private Form generateForm(String formDefId) {
        if (formDefId == null || formDefId.isEmpty()) return null;

        AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
        FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");
        FormDefinitionDao formDefinitionDao = (FormDefinitionDao) AppUtil.getApplicationContext().getBean("formDefinitionDao");

        FormDefinition formDefinition = formDefinitionDao.loadById(formDefId, appDefinition);

        if(formDefinition == null) {
            LogUtil.warn(getClassName(), "formDef not found: " + formDefId);
            return null;
        }

        return (Form) formService.createElementFromJson(formDefinition.getJson());
    }

    public String getFieldNotes() {
        return getPropertyString("fieldNotes");
    }

    public String getFieldRecordId() {
        return getPropertyString("fieldRecordId");
    }

    public String getFieldType() {
        return getPropertyString("fieldType");
    }

    @Override
    public FormRowSet load(Element element, String primaryKey, FormData formData) {
        String notesFormDefId = getPropertyString("notesFormDefId");
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null || notesFormDefId.isEmpty()){
            return new FormRowSet() {{
                setMultiRow(true);
            }};
        }

        String recordId = getFieldRecordId();

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rowSet = formDataDao.find(notesForm, "WHERE c_" + recordId + " = ?", new Object[]{primaryKey}, "dateCreated", false, null, null);
        return rowSet;
    }

    @Override
    public Notes toNote(FormRow row) {
        String recordId = getFieldRecordId();
        String notes = getFieldNotes();
        String type = getFieldType();

        String noteType = row.getProperty(type);
        return new Notes(
                row.getId(),
                row.getProperty(recordId),
                row.getCreatedBy(),
                row.getCreatedByName(),
                row.getProperty(notes),
                row.getDateCreated(),
                noteType.isEmpty() ? NoteType.NOTE : NoteType.valueOf(noteType.toUpperCase(Locale.ROOT)),
                0
        );
    }

    @Override
    public FormRow fromNote(Notes note) {
        return new FormRow() {{
            setId(note.getId());
            setProperty(getFieldRecordId(), note.getRecordId());
            setProperty(getFieldNotes(), note.getNote());
            setProperty(getFieldType(), note.getType().name());
            setCreatedBy(note.getUsername());
            setCreatedByName(note.getDisplayName());
            setDateCreated(note.getDate());
        }};
    }
}
