package com.kinnara.kecakplugins.notes;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import com.google.gson.JsonObject;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 * @author AKMAL
 */
public class Notes extends Element implements FormBuilderPaletteElement {
    @Override
    public String getName() {
        return getLabel() + getVersion();
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

    @Override
    public String getLabel() {
        return "Notes";
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(this.getClass().getName(), "/properties/Notes.json");
    }

    @Override
    public String getFormBuilderCategory() {
        return "Kecak";
    }

    @Override
    public int getFormBuilderPosition() {
        return 100;
    }

    @Override
    public String getFormBuilderIcon() {
        return null;
    }

    @Override
    public String getFormBuilderTemplate() {
        return "<label class='label'>Notes</label><input type='text' readonly/>";
    }

    @Override
    public FormRowSet formatData(FormData formData) {

        String id = getPropertyString(FormUtil.PROPERTY_ID);
        String value = formData.getRequestParameter(id);
        String primaryKey = formData.getPrimaryKeyValue();

        FormStoreBinder storeBinder = this.getStoreBinder();
        if (storeBinder != null) {
            FormRowSet formRowSet = new FormRowSet();
            formRowSet.setMultiRow(true);
            if (value != null && !value.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject note = jsonArray.getJSONObject(i);
                        String noteId = note.optString("id");
                        if (noteId == null || noteId.isEmpty()) noteId = UUID.randomUUID().toString();

                        FormRow formRow = new FormRow();
                        formRow.setId(noteId);
                        formRow.put("id", noteId);
                        formRow.put("record_id", primaryKey);
                        formRow.put("username", note.optString("username"));
                        formRow.put("name", note.optString("name"));
                        formRow.put("date", note.optString("date"));
                        formRow.put("notes", note.optString("notes"));
                        formRow.put("type", note.optString("type", "note"));

                        formRowSet.add(formRow);
                    }
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Error parsing multirow: " + e.getMessage());
                }
            }
            return formRowSet;
        } else {
            FormRowSet formRowSet = super.formatData(formData);
            if (formRowSet == null || formRowSet.isEmpty()){
                formRowSet = new FormRowSet();
                formRowSet.add(new FormRow());
            }
            if (value != null) {
                formRowSet.get(0).put(id, value);
            }
            return formRowSet;
        }
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "Notes.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        String value = "";
        formData.addRequestParameterValues("id",
                new String[] { formData.getPrimaryKeyValue() });

        FormLoadBinder loadBinder = this.getLoadBinder();
        if (loadBinder != null) {
            FormRowSet rowSet = loadBinder.load(this,
                    formData.getPrimaryKeyValue(), formData);
            if (rowSet != null && !rowSet.isEmpty()) {
                JSONArray jsonArray = new JSONArray();
                rowSet.forEach(row -> {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject();
                        jsonObject.put("id", row.getId());
                        jsonObject.put("record_id", row.getProperty("record_id"));
                        jsonObject.put("username", row.getProperty("username"));
                        jsonObject.put("name", row.getProperty("name"));
                        jsonObject.put("date", row.getProperty("date"));
                        jsonObject.put("notes", row.getProperty("notes"));
                        jsonObject.put("type", row.getProperty("type"));
                        jsonArray.put(jsonObject);
                    } catch (Exception e){
                        LogUtil.error(getClassName(), e, "error: " + e);
                    }
                });
                value = jsonArray.toString();
                LogUtil.info(getClassName(), value);
            }
            LogUtil.info(getClassName(), value);
        } else {
            value = FormUtil.getElementPropertyValue(this, formData);
        }

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();
        String userName = user.getUsername();
        String name = user.getFirstName() + " " + user.getLastName();

        String readOnly = getPropertyString("readonly");
        String readOnlyAsLabel = getPropertyString("readonlyLabel");
        boolean isReadOnly = "true".equalsIgnoreCase(readOnly) || FormUtil.isReadonly(this, formData);
        boolean isReadOnlyLabel = isReadOnly && "true".equalsIgnoreCase(readOnlyAsLabel);

        boolean isMultirow = this.getLoadBinder() != null;

        dataModel.put("value", (value == null || value.isEmpty()) ? "[]" : value);
        dataModel.put("className", getClassName());
        dataModel.put("userName", userName);
        dataModel.put("name", name);
        dataModel.put("isReadOnly", isReadOnly);
        dataModel.put("isReadOnlyLabel", isReadOnlyLabel);
        dataModel.put("isMultirow", isMultirow);
        dataModel.put("primaryKey", formData.getPrimaryKeyValue());

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }
}
