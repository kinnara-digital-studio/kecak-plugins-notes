package com.kinnara.kecakplugins.notes.form;

import com.kinnara.kecakplugins.notes.common.NoteType;
import com.kinnara.kecakplugins.notes.model.Notes;
import com.kinnara.kecakplugins.notes.common.NotesStoreBinder;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.lib.HiddenField;
import org.joget.apps.form.lib.SelectBox;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

public class HistoryNotesStoreBinder extends FormBinder implements NotesStoreBinder {

    private final static String LABEL = "History Notes Store Binder";

    private static final Set<String> SYSTEM_FIELDS = Set.of(
            FormUtil.PROPERTY_ID,
            FormUtil.PROPERTY_DATE_CREATED,
            FormUtil.PROPERTY_CREATED_BY,
            FormUtil.PROPERTY_CREATED_BY_NAME,
            FormUtil.PROPERTY_DATE_MODIFIED,
            FormUtil.PROPERTY_MODIFIED_BY,
            FormUtil.PROPERTY_MODIFIED_BY_NAME,
            FormUtil.PROPERTY_DELETED,
            FormUtil.PROPERTY_ORG_ID
    );

    protected FormRow fromNote(Notes note) {
        return new FormRow() {{
            setId(note.getId());
            setProperty(getFieldRecordId(), note.getRecordId());
            setProperty(getFieldNotes(), note.getNote());
            setProperty(getFieldType(), note.getType().name());
            setCreatedBy(note.getUsername());
            setCreatedByName(note.getDisplayName());
            setDateCreated(note.getDate());
            setOrgId(note.getOrgId());
        }};
    }

    @Override
    public List<Notes> storeNotes(Element element, List<Notes> notes, FormData formData) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        User user = workflowUserManager.getCurrentUser();
        String username = user.getUsername();
        String fullName = user.getFirstName() + " " + user.getLastName();

        String notesFormDefId = getPropertyString("notesFormDefId");
        Form notesForm = generateForm(notesFormDefId);

        if (notesForm == null || notesFormDefId.isEmpty()){
            return Collections.emptyList();
        }

        Form f = FormUtil.findRootForm(element);
        FormRowSet dataBefore = formData.getLoadBinderData(f);
        FormRow rowBefore = dataBefore != null && !dataBefore.isEmpty() ? dataBefore.get(0) : new FormRow();

        boolean isNewRecord = dataBefore == null || dataBefore.isEmpty();
        StringBuilder messageBuilder = new StringBuilder();
        //LogUtil.info(getClassName(), "form properties: " + f.getProperties().toString());

        if (isNewRecord) {
            String formName = f.getPropertyString("name");
            messageBuilder.append("<p>Created new <b>")
                    .append(formName.isEmpty() ? "Record" : formName)
                    .append("</b></p>");
        } else {
            Set<String> fieldIds = rowBefore.stringPropertyNames();
            for (String fieldId : fieldIds) {
                if (SYSTEM_FIELDS.contains(fieldId)) continue;

                Element targetElement = FormUtil.findElement(fieldId, f, formData, true);
                if (targetElement == null) continue;
                if (targetElement instanceof HiddenField) continue;
                if (!isVisible(targetElement, formData)) continue;
                if (isReadonly(targetElement, formData)) continue;

                String oldValue = Optional.ofNullable(rowBefore.getProperty(fieldId)).orElse("").trim();
                String newValue = Optional.ofNullable(FormUtil.getRequestParameter(targetElement, formData)).orElse("").trim();

                String fieldLabel = targetElement.getPropertyString(FormUtil.PROPERTY_LABEL);
                if (fieldLabel == null || fieldLabel.trim().isEmpty()) {
                    fieldLabel = fieldId;
                }

                String oldLabel = oldValue;
                String newLabel = newValue;
                if (targetElement instanceof SelectBox) {
                    Collection<FormRow> optionMap = FormUtil.getElementPropertyOptionsMap(targetElement, formData);
                    oldLabel = resolveOptionLabel(oldValue, optionMap);
                    newLabel = resolveOptionLabel(newValue, optionMap);
                }

                if (!Objects.equals(oldValue, newValue)) {
                    messageBuilder
                        .append("<p>Changed <b><i>").append(fieldLabel)
                        .append("</i></b> from <b><s>").append(oldLabel.isEmpty() ? "Empty" : oldLabel)
                        .append("</s></b> to <b>").append(newLabel.isEmpty() ? "Empty" : newLabel)
                        .append("</b></p>");
                }
            }
        }

        if (messageBuilder.length() > 0) {
            Notes note = new Notes(
                    UUID.randomUUID().toString(),
                    formData.getPrimaryKeyValue(),
                    username,
                    fullName,
                    messageBuilder.toString(),
                    new Date(),
                    NoteType.LOG,
                    "*");
            notes.add(note);
        }

        FormRowSet storeRowSet = Optional.of(notes)
                .stream()
                .flatMap(Collection::stream)
                .map(this::fromNote)
                .collect(Collectors.toCollection(FormRowSet::new));

        storeRowSet.setMultiRow(true);

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        try {
            formDataDao.saveOrUpdate(notesForm, storeRowSet);
        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "save failed, error: " + e);
        }
        return notes;
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
        return AppUtil.readPluginResource(getClass().getName(), "/properties/HistoryStoreNotesBinder.json");
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

    private String resolveOptionLabel(String value, Collection<FormRow> optionMap) {
        if (value == null || value.isEmpty() || optionMap == null || optionMap.isEmpty()) {
            return value != null ? value : "";
        }

        // Build lookup map: value -> label
        Map<String, String> lookup = new LinkedHashMap<>();
        for (FormRow row : optionMap) {
            String optValue = row.getProperty(FormUtil.PROPERTY_VALUE);
            String optLabel = row.getProperty(FormUtil.PROPERTY_LABEL);
            if (optValue != null) {
                lookup.put(optValue, optLabel != null ? optLabel : optValue);
            }
        }

        // Handle multi-value (delimiter ";")
        String[] values = value.split(";");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String v = values[i].trim();
            if (i > 0) result.append("; ");
            result.append(lookup.getOrDefault(v, v));
        }
        return result.toString();
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

    /**
     * Check if current element is hidden by the Section
     *
     * @param element
     * @param formData
     * @return
     */
    public boolean isVisible(Element element, FormData formData) {
        if(element == null) return false;

        if(!element.isAuthorize(formData)) return false;

        if(element instanceof Section) {
            Section section = (Section) element;
            return section.isMatch(formData);
        }
        return isVisible(element.getParent(), formData);
    }

    public boolean isReadonly(Element element, FormData formData) {
        if (element == null) return false;
        if (FormUtil.isReadonly(element, formData)) {
            return true;
        }
        return isReadonly(element.getParent(), formData);
    }
}
