package com.kinnara.kecakplugins.notes.form;

import com.kinnara.kecakplugins.notes.model.NoteType;
import com.kinnara.kecakplugins.notes.model.Notes;
import com.kinnara.kecakplugins.notes.model.NotesLoadBinder;
import com.kinnara.kecakplugins.notes.model.NotesStoreBinder;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.directory.model.User;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author AKMAL
 */
public class NotesElement extends Element implements FormBuilderPaletteElement {
    @Override
    public String getName() {
        return getLabel();
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
        Object[] args = new Object[]{
                FormLoadMultiRowElementBinder.class.getName(),
                NotesBinder.class.getName(),
                FormStoreMultiRowElementBinder.class.getName(),
                NotesBinder.class.getName()
        };
        return AppUtil.readPluginResource(this.getClass().getName(), "/properties/NotesElement.json", args, true, "");
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
        String username = user.getUsername();
        String fullName = user.getFirstName() + " " + user.getLastName();

        LogUtil.info(getClassName(), "value" +
                " di format data: " + value);

        FormStoreBinder storeBinder = this.getStoreBinder();
        FormRowSet formRowSet = new FormRowSet();
        formRowSet.setMultiRow(true);

        if (storeBinder instanceof NotesStoreBinder) {
            NotesStoreBinder binder = (NotesStoreBinder) storeBinder;

            if (value != null && !value.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    long baseTimestamp = System.currentTimeMillis();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonNote = jsonArray.getJSONObject(i);

                        String noteType = jsonNote.optString("type", NoteType.NOTE.name()).toUpperCase(Locale.ROOT);
                        Date uniqueDate = new Date(baseTimestamp + i);
                        Notes note = new Notes(
                                UUID.randomUUID().toString(),
                                primaryKey,
                                username,
                                fullName,
                                jsonNote.optString("notes"),
                                uniqueDate,
                                noteType.isEmpty() ? NoteType.NOTE : NoteType.valueOf(noteType),
                                0);

                        formRowSet.add(binder.fromNote(note));
                    }
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Error parsing multirow: " + e.getMessage());
                }
            }
        } else {
            formRowSet = super.formatData(formData);
            if (formRowSet == null || formRowSet.isEmpty()) {
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
        String template = "NotesElement.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();

        String value = "";
        formData.addRequestParameterValues("id",
                new String[]{formData.getPrimaryKeyValue()});
        FormLoadBinder loadBinder = this.getLoadBinder();

        if (loadBinder instanceof NotesLoadBinder) {
            JSONArray jsonArray = new JSONArray();

            NotesLoadBinder binder = (NotesLoadBinder) loadBinder;
            FormRowSet noteList = formData.getLoadBinderData(this);
//            binder.load(this, formData.getPrimaryKeyValue(), formData);

            Optional.ofNullable(noteList)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(binder::toNote)
                    .forEach(note -> {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", note.getId());
                            jsonObject.put("username", note.getUsername());
                            jsonObject.put("name", note.getDisplayName());
                            Date dateCreated = note.getDate();
                            if (dateCreated != null) {
                                if (user != null && user.getTimeZone() != null && !user.getTimeZone().trim().isEmpty()) {
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
                            } else {
                                jsonObject.put("dateStr", "");
                                jsonObject.put("dateLabel", "");
                            }

                            jsonObject.put("notes", note.getNote());
                            jsonObject.put("type", note.getType().name());

                            jsonArray.put(jsonObject);

                        } catch (JSONException e) {
                            LogUtil.error(getClassName(), e, "error: " + e.getMessage());
                        }
                    });

            value = jsonArray.toString();
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
        dataModel.put("alertMessage", getPropertyString("alertMessage"));

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }
}