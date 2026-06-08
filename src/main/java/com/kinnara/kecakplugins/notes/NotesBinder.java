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
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/NotesBinder.json");
    }

    @Override
    public FormRowSet load(Element element, String s, FormData formData) {
        String notesFormDefId = getPropertyString("notesFormDefId");
        //LogUtil.info(getClassName(), "form id: " + notesFormDefId);
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null || notesFormDefId.isEmpty()){
            FormRowSet emptyRowSet = new FormRowSet();
            emptyRowSet.setMultiRow(true);
            return emptyRowSet;
        }

        String recordId = getPropertyString("fieldRecordId");

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rowSet = formDataDao.find(notesForm, "WHERE c_" + recordId + " = ?", new Object[]{s}, "dateCreated", false, null, null);

        if(rowSet != null) {
            rowSet.setMultiRow(true);
        }
        return rowSet;
    }

    @Override
    public FormRowSet store(Element element, FormRowSet formRowSet, FormData formData) {
        LogUtil.info(getClassName(), "NotesBinder.store() called, formRowSet Size: " + formRowSet.size());
        String notesFormDefId = getPropertyString("notesFormDefId");
        //LogUtil.info(getClassName(), "form id: " + notesFormDefId);
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null || notesFormDefId.isEmpty()){
            return formRowSet;
        }

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
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
}
