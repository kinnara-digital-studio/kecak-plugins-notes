package com.kinnara.kecakplugins.notes;

import java.util.Map;
import java.util.ResourceBundle;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.directory.model.User;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
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
        return AppUtil.readPluginResource(getClassName(), "/properties/Notes.json");
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
        FormRowSet rowSet = super.formatData(formData);
        if (rowSet == null || rowSet.isEmpty()) {
            rowSet = new FormRowSet();
            rowSet.add(new FormRow());
        }

        String id = getPropertyString(FormUtil.PROPERTY_ID);

        String value = formData.getRequestParameter(id);

        if (value != null) {
            rowSet.get(0).put(id, value);
        }

        return rowSet;
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "Notes.ftl";
        return renderTemplate(template, formData, dataModel);
    }


    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        String value = FormUtil.getElementPropertyValue(this, formData);

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();
        String userName = user.getUsername();
        String name = user.getFirstName() + " " + user.getLastName();

        String readOnly = getPropertyString("readonly");
        String readOnlyAsLabel = getPropertyString("readonlyLabel");

        boolean isReadOnly = "true".equalsIgnoreCase(readOnly) || FormUtil.isReadonly(this, formData);

        boolean isReadOnlyLabel = isReadOnly && "true".equalsIgnoreCase(readOnlyAsLabel);

        dataModel.put("value", (value == null || value.isEmpty()) ? "[]" : value);
        dataModel.put("className", getClassName());
        dataModel.put("userName", userName);
        dataModel.put("name", name);
        dataModel.put("isReadOnly", isReadOnly);
        dataModel.put("isReadOnlyLabel", isReadOnlyLabel);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }
}

