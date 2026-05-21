package com.kinnara.kecakplugins.notes;

import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;

import java.util.ResourceBundle;

public class NotesBinder extends FormBinder implements
        FormLoadElementBinder,
        FormLoadMultiRowElementBinder,
        FormStoreElementBinder,
        FormStoreMultiRowElementBinder {

    private final static String LABEL = "Notes Binder";

    @Override
    public FormRowSet load(Element element, String s, FormData formData) {
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        String notesFormDefId = element.getPropertyString("notesFormDefId");
        Form notesForm = generateForm(notesFormDefId);
        FormRowSet rowSet = formDataDao.find(notesForm, "WHERE c_record_id = ?", new Object[]{s}, "dateCreated", true, null, null);
        if(rowSet != null) {
            rowSet.setMultiRow(true);
        }
        return rowSet;
    }

    @Override
    public FormRowSet store(Element element, FormRowSet formRowSet, FormData formData) {
        LogUtil.info(getClassName(), "NotesBinder.store() called, formRowSet Size: " + formRowSet.size());

        for (int i = 0; i < formRowSet.size(); i++) {
            FormRow row = formRowSet.get(i);
            LogUtil.info(getClassName(), "=== Baris ke-" + (i + 1) + " (ID Note: " + row.getId() + ") ===");

            // Looping untuk membedah semua nama kolom dan value-nya
            for (String columnName : row.stringPropertyNames()) {
                String value = row.getProperty(columnName);
                LogUtil.info(getClassName(), "  -> Kolom [" + columnName + "] = " + value);
            }
        }

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

        String notesFormDefId = element.getPropertyString("notesFormDefId");
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null) {
            LogUtil.warn(getClassName(), "notesForm null! cek notesFormDefId: " + notesFormDefId);
            return formRowSet;
        }

        try {
            formDataDao.saveOrUpdate(notesForm, formRowSet);
            LogUtil.info(getClassName(), "Sukses menyimpan " + formRowSet.size() + " notes ke database.");
        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "save failed, error: " + e);
        }
        return formRowSet;
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
    public String getPropertyOptions() {
        return null;
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

        if (formDefinition == null) {
            LogUtil.warn(getClassName(), "formDef not found: " + formDefId);
            return null;
        }

        return (Form) formService.createElementFromJson(formDefinition.getJson());

    }
}
