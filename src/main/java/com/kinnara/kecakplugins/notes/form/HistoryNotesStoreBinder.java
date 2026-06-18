package com.kinnara.kecakplugins.notes.form;

import com.kinnara.kecakplugins.notes.model.NoteType;
import com.kinnara.kecakplugins.notes.model.Notes;
import com.kinnara.kecakplugins.notes.model.NotesStoreBinder;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
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

                String oldValue = rowBefore.getProperty(fieldId);
                String newValue = formData.getRequestParameter(fieldId);

                Element targetElement = FormUtil.findElement(fieldId, f, formData);
                if (targetElement == null) continue;

                String fieldLabel = targetElement.getPropertyString(FormUtil.PROPERTY_LABEL);

                if (newValue != null && !Objects.equals(oldValue, newValue)) {
                    messageBuilder
                        .append("<p>Changed <b><i>").append(fieldLabel.isEmpty() ? fieldId : fieldLabel)
                        .append("</i></b> from <b><s>").append(oldValue)
                        .append("</s></b> to <b><i>").append(newValue)
                        .append("</i></b></p>");
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
                    0);

            notes.add(note);

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
        } else {
            return Collections.emptyList();
        }
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
