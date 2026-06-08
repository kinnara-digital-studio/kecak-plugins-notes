package com.kinnara.kecakplugins.notes;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import com.google.gson.JsonObject;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.TimeZoneUtil;
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

        ApplicationContext appContext = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();
        String userName = user.getUsername();
        String name = user.getFirstName() + " " + user.getLastName();

        LogUtil.info(getClassName(), "value di format data: " + value);

        FormStoreBinder storeBinder = this.getStoreBinder();
        FormRowSet formRowSet = new FormRowSet();
        formRowSet.setMultiRow(true);
        if (storeBinder != null) {
            FormBinder binder = (FormBinder) storeBinder;
            String recordId = binder.getPropertyString("fieldRecordId");
            String notes = binder.getPropertyString("fieldNotes");
            String type = binder.getPropertyString("fieldType");

            if (value != null && !value.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject note = jsonArray.getJSONObject(i);

                        FormRow formRow = new FormRow();
                        formRow.setId(UUID.randomUUID().toString());
                        formRow.put(recordId, primaryKey);

                        formRow.setCreatedBy(userName);
                        formRow.setCreatedByName(name);
                        formRow.setDateCreated(new Date());

                        LogUtil.info(getClassName(), "dateCreated: " + new Date());

                        formRow.put(notes, note.optString("notes"));
                        formRow.put(type, note.optString("type", "note"));

                        formRowSet.add(formRow);
                    }
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Error parsing multirow: " + e.getMessage());
                }
            }
        } else {
            formRowSet = super.formatData(formData);
            if (formRowSet == null || formRowSet.isEmpty()){
                formRowSet = new FormRowSet();
                formRowSet.add(new FormRow());
            }
            if (value != null) {
                formRowSet.get(0).put(id, value);
            }
        }
        return formRowSet;
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "Notes.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();

        String value = "";
        formData.addRequestParameterValues("id",
                new String[] { formData.getPrimaryKeyValue() });
        FormLoadBinder loadBinder = this.getLoadBinder();

        if (loadBinder != null) {
            FormBinder binder = (FormBinder) loadBinder;
            String recordId = binder.getPropertyString("fieldRecordId");
            String notes = binder.getPropertyString("fieldNotes");
            String type = binder.getPropertyString("fieldType");

            FormRowSet rowSet = loadBinder.load(this,
                    formData.getPrimaryKeyValue(), formData);

            if (rowSet != null && !rowSet.isEmpty()) {
                JSONArray jsonArray = new JSONArray();
                rowSet.forEach(row -> {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject();
                        jsonObject.put("id", row.getId());
                        jsonObject.put("recordId", row.getProperty(recordId));
                        jsonObject.put("username", row.getCreatedBy());
                        jsonObject.put("name", row.getCreatedByName());
                        Date dateCreated = row.getDateCreated();
                        if (dateCreated != null) {
                            if (user != null && user.getTimeZone() != null && !user.getTimeZone().trim().isEmpty()){
                                String pattern = "dd/MM/yyyy HH:mm:ss";

                                String date = TimeZoneUtil.convertToTimeZone(
                                        dateCreated,
                                        user.getTimeZone(),
                                        pattern
                                );
                                jsonObject.put("dateStr", date);
                            }
                            SimpleDateFormat labelFormat = new SimpleDateFormat("dd MMMM yyyy");
                            String dateLabel = labelFormat.format(dateCreated);

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dateCreated);
                            Calendar today = Calendar.getInstance();
                            Calendar yesterday = Calendar.getInstance();
                            yesterday.add(Calendar.DATE, -1);

                            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                    cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                                dateLabel = "Today";
                            } else if (cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                                    cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                                dateLabel = "Yesterday";
                            }
                            jsonObject.put("dateLabel", dateLabel);
                            jsonObject.put("date", dateCreated.toInstant().toString());
                        } else {
                            jsonObject.put("dateStr", "");
                            jsonObject.put("dateLabel", "");
                            jsonObject.put("date", "");
                        }
                        jsonObject.put("notes", row.getProperty(notes));
                        jsonObject.put("type", row.getProperty(type));
                        jsonArray.put(jsonObject);
                    } catch (Exception e){
                        LogUtil.error(getClassName(), e, "error: " + e);
                    }
                });
                value = jsonArray.toString();
            }
        } else {
            value = FormUtil.getElementPropertyValue(this, formData);
        }

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