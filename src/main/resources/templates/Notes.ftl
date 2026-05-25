<div class="form-cell" ${elementMetaData!}>
    <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}</span></label>

    <#if error??>
        <span class="form-error-message">${error}</span>
    </#if>

    <div class="form-cell-value notes-element-wrapper">
        <input type='hidden' name='${elementParamName!}' id='${elementParamName!}' value="">

        <#if isReadOnlyLabel!false>
            <div id="note-list-${elementParamName!}" class="note-list-container"></div>
        <#else>
            <div class="editor-row-container">
                <div class="notes-editor-wrapper">
                    <div id="toolbar-container-${elementParamName!}" class="notes-toolbar">
                        <span class="ql-formats">
                            <select class="ql-font"></select>
                            <select class="ql-header">
                                <option value="1">Heading 1</option>
                                <option value="2">Heading 2</option>
                                <option selected>Normal</option>
                            </select>
                        </span>
                        <span class="ql-formats">
                            <button class="ql-bold"></button>
                            <button class="ql-italic"></button>
                            <button class="ql-underline"></button>
                            <button class="ql-strike"></button>
                        </span>
                        <span class="ql-formats">
                            <select class="ql-color"></select>
                            <select class="ql-background"></select>
                        </span>
                        <span class="ql-formats">
                            <button class="ql-list" value="ordered"></button>
                            <button class="ql-list" value="bullet"></button>
                            <button class="ql-indent" value="-1"></button>
                            <button class="ql-indent" value="+1"></button>
                        </span>
                        <#--  <span class="ql-formats">
                            <button class="ql-direction" value="rtl"></button>
                            <select class="ql-align"></select>
                        </span>  -->
                        <span class="ql-formats">
                            <button class="ql-link"></button>
                            <button class="ql-image"></button>
                        </span>
                        <span class="ql-formats">
                            <button class="ql-clean"></button>
                        </span>
                    </div>
                    <div id="notes-editor-${elementParamName!}" class="notes-editor-area"></div>
                </div>
                <#if !(isReadOnly!false)>
                    <button type="button" id="btn-add-${elementParamName!}" class="save-note-btn" title="Save Note">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                            <polyline points="17 21 17 13 7 13 7 21"></polyline>
                            <polyline points="7 3 7 8 15 8"></polyline>
                        </svg>
                    </button>
                </#if>
            </div>
            <div id="note-list-${elementParamName!}" class="note-list-container"></div>
        </#if>
    </div>

    <style>
        .form-cell:has(.notes-element-wrapper) {
            overflow: visible !important;
        }
        .editor-row-container {
            display: flex;
            align-items: flex-end;
            gap: 10px;
            width: 100%;
        }
        .notes-editor-wrapper {
            flex: 1;
            border: 1px solid #ccc;
            border-radius: 6px;
            overflow: hidden;
            background: #fff;
            transition: border-color 0.2s;
        }
        .notes-editor-wrapper:focus-within {
            border-color: #80bdff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.15);
        }
        .notes-editor-wrapper .notes-toolbar.ql-toolbar {
            border: none !important;
            border-bottom: 1px solid #e0e0e0 !important;
            background: #fafafa;
            padding: 6px 8px !important;
        }
        .notes-editor-wrapper .notes-editor-area.ql-container {
            border: none !important;
            min-height: 120px;
            font-size: 14px;
        }
        .notes-editor-wrapper .notes-editor-area .ql-editor {
            min-height: 120px;
            padding: 10px 12px;
        }
        .notes-editor-wrapper .notes-editor-area .ql-editor.ql-blank::before {
            color: #aaa;
            font-style: italic;
        }
        .notes-element-wrapper .ql-toolbar {
            position: relative;
            z-index: 100 !important;
        }
        .notes-element-wrapper .ql-picker-options {
            z-index: 9999 !important;
        }
        .notes-element-wrapper .ql-tooltip {
            z-index: 9999 !important;
            left: 0 !important;
            right: auto !important;
        }
        .notes-element-wrapper .ql-expanded .ql-picker-options {
            overflow: visible !important;
        }

        .save-note-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 42px;
            height: 42px;
            min-width: 42px;
            color: #111 !important;
            background: #fff !important;
            border: 1px solid #111;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.2s, box-shadow 0.2s;
            margin-bottom: 2px;
        }
        .save-note-btn:hover {
            background: #006ad8 !important;
            box-shadow: 0 2px 6px rgba(0,132,255,0.3);
        }
        .save-note-btn:active {
            background: #0056b3 !important;
        }

        .note-list-container {
            display: flex;
            flex-direction: column;
            width: 100%;
            max-width: 100%;
            background: #fff !important;
            gap: 4px;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #ccc;
            margin-top: 10px;
            box-sizing: border-box;
        }
        .note-list-container:empty {
            display: none;
        }

        .note-bubble {
            display: flex;
            flex-direction: column;
            max-width: 100%;
            margin: 4px 0;
            align-self: flex-start;
            align-items: flex-start;
        }
        .bubble-content {
            position: relative;
            padding: 0 14px 10px 14px;
            max-width: 100%;
            word-wrap: break-word;
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
            font-size: 13px;
            line-height: 1.45;
            border-radius: 8px !important;
        }
        .bubble-mine .bubble-content {
            background: #0d6efd !important;
            color: #fff !important;
            border: 1px solid #e0e0e0;
        }
        .bubble-others .bubble-content {
            background: #eee !important;
            color: #111 !important;
            border: 1px solid #e0e0e0;
        }
        .bubble-header {
            margin: 0 -14px 8px -14px;
            padding: 8px 14px;
            border-radius: 8px 8px 0 0;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 6px;
        }
        .bubble-mine .bubble-header .bubble-name {
            color: #fff;
        }
        .bubble-mine .bubble-header .bubble-date {
            color: #e0e0e0;
        }
        .bubble-others .bubble-header .bubble-name {
            color: #111;
        }
        .bubble-others .bubble-header .bubble-date {
            color: #888;
        }
        .bubble-name {
            font-size: 12px;
            font-weight: 600;
        }
        .bubble-date {
            font-size: 11px;
        }
        .bubble-content img {
            max-width: 100%;
            height: auto;
            border-radius: 6px;
        }
        .bubble-body p {
            margin: 0 0 4px 0;
        }
        .bubble-body p:last-child {
            margin-bottom: 0;
        }

        .date-separator {
            text-align: center;
            color: #999;
            font-size: 11px;
            position: relative;
            margin: 10px 0;
            padding: 0 12px;
        }
        .date-separator::before,
        .date-separator::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 35%;
            height: 1px;
            background: #d0d0d0;
        }
        .date-separator::before { left: 0; }
        .date-separator::after  { right: 0; }


    </style>
</div>

<link rel="stylesheet" href="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.snow.css" />
<script src="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.js"></script>

<script type="text/javascript">
    (function(){
        var paramName = '${elementParamName!}'

        var userName = '${userName}';
        var displayName = '${name}';

        var isMultirow = ${isMultirow?c};
        var primaryKey = '${primaryKey!}';

        var jsonNotes = ${value!'[]'};
        if (!Array.isArray(jsonNotes)) jsonNotes = [];

        var newNotes = [];
        var editor = document.getElementById('notes-editor-' + paramName);

        var hiddenInput = document.getElementById(paramName);
        var allHiddenInputs = document.querySelectorAll('input[type="hidden"][name="' + paramName + '"]');

        //console.log('[Notes DEBUG] jsonNotes.length:', jsonNotes.length);
        //console.log('[Notes DEBUG] hiddenInput found:', !!hiddenInput);
        //console.log('[Notes DEBUG] Total hidden inputs with same name:', allHiddenInputs.length);

        if (editor) {
            const quill = new Quill('#notes-editor-' + paramName, {
                readOnly: ${(isReadOnly!false)?c},
                modules: {
                    toolbar: '#toolbar-container-' + paramName
                },
                placeholder: 'Write a note...',
                theme: 'snow'
            });

            var buttonAdd = document.getElementById('btn-add-' + paramName);

            if(buttonAdd) {
                buttonAdd.addEventListener('click', function() {
                    var html = quill.root.innerHTML;
                    var text = quill.getText().trim();

                    if (text === "" && quill.getLength() <= 1) {
                        alert("note can't be empty");
                        return;
                    }
                    var newNote = {
                        username: userName,
                        name: displayName,
                        date: new Date().toISOString(),
                        notes: html,
                        type: 'note'
                    };
                    if (isMultirow) {
                        newNote.record_id = primaryKey;
                    }
                    //console.log('[Notes DEBUG] newNote:', JSON.stringify(newNote));

                    jsonNotes.push(newNote);
                    //console.log('[Notes DEBUG] Add clicked! jsonNotes.length after push:', jsonNotes.length);
                    //console.log('[Notes DEBUG] jsonNotes:', JSON.stringify(jsonNotes));

                    var targetInput = document.getElementById(paramName);
                    //console.log('[Notes DEBUG] targetInput found:', !!targetInput);

                    targetInput.value = JSON.stringify(jsonNotes);
                    //console.log('[Notes DEBUG] Value SET. Length of value string:', targetInput.value.length);

                    var allInputs = document.querySelectorAll('input[type="hidden"][name="' + paramName + '"]');
                    //console.log('[Notes DEBUG] Updating ALL hidden inputs, count:', allInputs.length);
                    allInputs.forEach(function(inp) {
                        inp.value = JSON.stringify(jsonNotes);
                    });

                    renderNotes(jsonNotes);
                    quill.setContents([]);
                });
            }
        }

        renderNotes(jsonNotes);

        function renderNotes(notes) {
            var container = document.getElementById('note-list-' + paramName);
            container.innerHTML = '';

            var lastDate = null;

            notes.slice().reverse().forEach(function(note) {
                var item = document.createElement('div');
                var noteDate = new Date(note.date).toDateString();
                var date = new Date(note.date).toLocaleString('id-ID');
                if (noteDate !== lastDate) {
                    lastDate = noteDate;
                    var separator = document.createElement('div');
                    separator.className = 'date-separator';
                    separator.textContent = getDateLabel(noteDate);
                    container.appendChild(separator);
                }

                item.innerHTML =
                    '<div class="note-bubble ' + (note.username === userName ? 'bubble-mine' : 'bubble-others') + '">' +
                        '<div class="bubble-content">' +
                            '<div class="bubble-header">' +
                                '<span class="bubble-name">' + note.name + '</span>' +
                                '<span class="bubble-date">' + date + '</span>' +
                            '</div>' +
                            '<div class="bubble-body">' + note.notes + '</div>' +
                        '</div>' +
                    '</div>';
                container.appendChild(item);


                item.querySelectorAll('img').forEach(function(img) {
                    img.style.cursor = 'pointer';
                    img.addEventListener('click', function() {
                        var overlay = document.createElement('div');
                        overlay.style.cssText =
                            'position:fixed;top:0;left:0;width:100%;height:100%;' +
                            'background:rgba(0,0,0,0.8);z-index:99999;' +
                            'display:flex;align-items:center;justify-content:center;cursor:pointer';

                        var bigImg = document.createElement('img');
                        bigImg.src = img.src;
                        bigImg.style.maxWidth = '90%';
                        bigImg.style.maxHeight = '90%';

                        overlay.appendChild(bigImg);
                        overlay.addEventListener('click', function() {
                            document.body.removeChild(overlay);
                        });
                        document.body.appendChild(overlay);
                    });
                });
            });
        }

        function getDateLabel(dateStr) {
            var today = new Date().toDateString();
            var yesterday = new Date(Date.now() - 86400000).toDateString();

            if (dateStr === today) return 'Today';
            if (dateStr === yesterday) return 'Yesterday';

            return new Date(dateStr).toLocaleDateString('id-ID', {
                day: 'numeric', month: 'long', year: 'numeric'
            });
        }

        function isWithin30Minutes(date1, date2) {
            let diffInMs = Math.abs(date1 - date2);
            return diffInMs < 30 * 60 * 1000;
        }

    })();
</script>
