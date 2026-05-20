package com.kinnara.kecakplugins.notes;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;

import java.util.ResourceBundle;

public class NotesBinder extends FormBinder implements
        FormLoadElementBinder,
        FormStoreElementBinder {

    private final static String LABEL = "Notes Binder";

    @Override
    public FormRowSet load(Element element, String s, FormData formData) {
        LogUtil.info(getClassName(), "=== NotesBinder.load() dipanggil ===");
        //LogUtil.info(getClassName(), "primaryKey: " + s);
        //LogUtil.info(getClassName(), "fieldId: " + element.getPropertyString(FormUtil.PROPERTY_ID));
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

        Form form = FormUtil.findRootForm(element);

        //LogUtil.info(getClassName(), "form: " + (form != null ? form.getPropertyString("id") : "NULL"));

        FormRow formRow = formDataDao.load(form, s);

        LogUtil.info(getClassName(), "formRow loaded: " + (formRow != null ? "YES" : "NULL"));

        if(formRow == null){
            formRow = new FormRow();
        }

        String value = formRow.getProperty(element.getPropertyString(FormUtil.PROPERTY_ID));
        LogUtil.info(getClassName(), "value dari DB: " + value);

        FormRowSet formRowSet = new FormRowSet();

        formRowSet.add(formRow);
        return formRowSet;
    }

    @Override
    public FormRowSet store(Element element, FormRowSet formRowSet, FormData formData) {
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

        //LogUtil.info(getClassName(), "=== NotesBinder.store() dipanggil ===");

        String fieldId = element.getPropertyString(FormUtil.PROPERTY_ID);
        //LogUtil.info(getClassName(), "fieldId: " + fieldId);
        Form form = FormUtil.findRootForm(element);

        String updatedJson = formRowSet.get(0).getProperty(fieldId);
        //LogUtil.info(getClassName(), "updatedJson: " + updatedJson);
        String primaryKey = formData.getPrimaryKeyValue();
        //LogUtil.info(getClassName(), "primaryKey: " + primaryKey);
        FormRow formRow = formDataDao.load(form, primaryKey);

        if(formRow == null){
            formRow = new FormRow();
            //LogUtil.info(getClassName(), "formRow Null");
        }

        formRow.put(fieldId, updatedJson);

        FormRowSet rowSet = new FormRowSet();
        rowSet.add(formRow);
        formDataDao.saveOrUpdate(form, rowSet);

        //LogUtil.info(getClassName(), "=== store() selesai ===");

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
}
