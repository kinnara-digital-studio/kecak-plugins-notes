<div class="form-cell" ${elementMetaData!}>
    <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}</span></label>

    <#if error??>
        <span class="form-error-message">${error}</span>
    </#if>

    <div class="form-cell-value notes-element-wrapper">
        <input type='hidden' name='${elementParamName!}' id='${elementParamName!}' value="" >

        <#if isReadOnlyLabel!false>
            <div id="note-list-${elementParamName!}" class="note-list-container"></div>
        <#else>
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
                    <span class="ql-formats">
                        <button class="ql-direction" value="rtl"></button>
                        <select class="ql-align"></select>
                    </span>
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
                <button type="button" id="btn-add-${elementParamName!}" class="add-note-btn">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                         style="vertical-align: middle; margin-right: 4px;">
                        <path d="M22 2L11 13"/>
                        <path d="M22 2L15 22L11 13L2 9L22 2Z"/>
                    </svg>
                    Add Note
                </button>
            </#if>
            <div id="note-list-${elementParamName!}" class="note-list-container"></div>
        </#if>
    </div>

    <style>
        .form-cell:has(.notes-element-wrapper) {
            overflow: visible !important;
        }
        .notes-editor-wrapper {
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

        .add-note-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-top: 10px;
            padding: 7px 18px;
            font-size: 13px;
            font-weight: 500;
            color: #fff !important;
            background: #0084ff !important;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.2s, box-shadow 0.2s;
        }
        .add-note-btn:hover {
            background: #006ad8 !important;
            box-shadow: 0 2px 6px rgba(0,132,255,0.3);
        }
        .add-note-btn:active {
            background: #0056b3 !important;
        }

        .note-list-container {
            display: flex;
            flex-direction: column;
            width: 100%;
            max-width: 100%;
            background: #f0f6fb !important;
            gap: 4px;
            padding: 12px;
            border-radius: 8px;
            margin-top: 10px;
            max-height: 420px;
            overflow-y: auto;
            box-sizing: border-box;
        }
        .note-list-container:empty {
            display: none;
        }
        .note-list-container::-webkit-scrollbar {
            width: 6px;
        }
        .note-list-container::-webkit-scrollbar-track {
            background: transparent;
        }
        .note-list-container::-webkit-scrollbar-thumb {
            background: #c0c0c0;
            border-radius: 3px;
        }

        .note-bubble {
            display: flex;
            flex-direction: column;
            max-width: 100%;
            margin: 4px 0;
        }
        .bubble-right {
            align-self: flex-end;
            align-items: flex-end;
        }
        .bubble-left {
            align-self: flex-start;
            align-items: flex-start;
        }
        .bubble-content {
            position: relative;
            padding: 8px 12px;
            max-width: 100%;
            word-wrap: break-word;
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
            font-size: 13px;
            line-height: 1.45;
        }
        .bubble-right .bubble-content {
            background: #dcf8c6 !important;
            color: #111 !important;
            border-radius: 14px 4px 14px 14px !important;
        }
        .bubble-left .bubble-content {
            background: #ffffff !important;
            color: #111 !important;
            border-radius: 4px 14px 14px 14px !important;
        }
        .bubble-content img {
            max-width: 100%;
            height: auto;
            border-radius: 6px;
        }
        .bubble-content p {
            margin: 0 0 4px 0;
        }
        .bubble-content p:last-child {
            margin-bottom: 0;
        }
        .bubble-name {
            font-size: 11px;
            color: #777;
            margin-bottom: 2px;
            font-weight: 500;
        }
        .bubble-date {
            font-size: 10px;
            color: #aaa;
            margin-top: 2px;
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

        .bubble-arrow-menu {
            position: absolute;
            top: 4px;
            right: 4px;
            opacity: 0;
            transition: opacity 0.2s;
        }
        .bubble-content:hover .bubble-arrow-menu {
            opacity: 1;
        }
        .bubble-arrow {
            cursor: pointer;
            font-size: 14px;
            color: #555;
            padding: 2px 5px;
            border-radius: 4px;
            background: rgba(255,255,255,0.75);
            line-height: 1;
        }
        .bubble-arrow:hover {
            background: rgba(0,0,0,0.08);
        }
        .bubble-dropdown {
            position: absolute;
            right: 0;
            top: 100%;
            min-width: 110px;
            background: #fff;
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.12);
            z-index: 9999;
            overflow: hidden;
        }
        .bubble-dropdown-item {
            padding: 7px 14px;
            cursor: pointer;
            font-size: 12px;
            color: #333;
            transition: background 0.15s;
        }
        .bubble-dropdown-item:hover {
            background: #f0f4f8;
        }

        .bubble-edit-container {
            margin-top: 4px;
        }
        .bubble-edit-container .ql-container {
            border-radius: 6px;
            font-size: 13px;
        }
        .bubble-save-btn,
        .bubble-cancel-btn {
            margin-top: 4px;
            margin-right: 4px;
            padding: 4px 12px;
            font-size: 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .bubble-save-btn {
            background: #0084ff;
            color: #fff;
        }
        .bubble-save-btn:hover {
            background: #006ad8;
        }
        .bubble-cancel-btn {
            background: #e0e0e0;
            color: #333;
        }
        .bubble-cancel-btn:hover {
            background: #cfcfcf;
        }
    </style>
</div>

<link rel="stylesheet" href="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.snow.css" />
<script src="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.js"></script>

<script type="text/javascript">
    (function(){
        var paramName = '${elementParamName!}'

        var userName = '${userName}';
        var displayName = '${name}';

        var jsonNotes = ${value!'[]'};
        if (!Array.isArray(jsonNotes)) jsonNotes = [];

        var editor = document.getElementById('notes-editor-' + paramName);

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
                    if (text === ""){
                        alert("note can't be empty");
                        return;
                    };
                    jsonNotes.push({
                        id: crypto.randomUUID(),
                        username: userName,
                        name: displayName,
                        date: new Date().toISOString(),
                        notes: html
                    });
                    renderNotes(jsonNotes);
                    document.getElementById(paramName).value = JSON.stringify(jsonNotes);
                    quill.setContents([]);

                    document.querySelectorAll('.bubble-dropdown').forEach(function(d) {
                        d.style.display = 'none';
                    });
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

                var dropdownHtml = '';
                if(note.username === userName){
                    dropdownHtml =
                        '<div class="bubble-arrow-menu">' +
                            '<span class="bubble-arrow">&#9662;</span>' +
                            '<div class="bubble-dropdown" style="display:none">' +
                                '<div class="bubble-dropdown-item" data-action="edit" data-id="' + note.id + '">Edit</div>' +
                                '<div class="bubble-dropdown-item" data-action="delete" data-id="' + note.id + '">Delete</div>' +
                            '</div>' +
                        '</div>'
                }

                item.innerHTML =
                    '<div class="note-bubble ' + (note.username === userName ? 'bubble-right' : 'bubble-left') + '">' +
                        '<small class="bubble-name">' + note.name + '</small>' +
                        '<div class="bubble-content">'
                            + note.notes + dropdownHtml +
                        '</div>' +
                        '<div class="bubble-edit-container" style="display:none">' +
                            '<div class="bubble-edit-editor"></div>' +
                            '<button type="button" class="bubble-save-btn">Save</button>' +
                            '<button type="button" class="bubble-cancel-btn">Cancel</button>' +
                        '</div>' +
                        '<small class="bubble-date">' + date + '</small>' +
                    '</div>';
                container.appendChild(item);

                var dBtn = item.querySelector('.bubble-arrow');
                var bDropdown = item.querySelector('.bubble-dropdown');

                if (dBtn) {
                    dBtn.addEventListener('click', function(e) {
                        e.stopPropagation();
                        bDropdown.style.display = bDropdown.style.display === 'none' ? 'block' : 'none';
                    });
                }

                var dropdown = item.querySelector('.bubble-dropdown');

                if(dropdown) {
                    dropdown.addEventListener('click', function(e) {
                        var action = e.target.dataset.action;
                        var id = e.target.dataset.id;
                        var now = new Date();
                        if(isWithin30Minutes(now, new Date(note.date))) {
                            if (action === 'delete') {
                                if (!confirm('Hapus note ini?')) return;
                                var index = jsonNotes.findIndex(function(n) {
                                    return n.id === id;
                                });

                                if (index !== -1) {
                                    jsonNotes.splice(index, 1);
                                }

                                document.getElementById(paramName).value = JSON.stringify(jsonNotes);
                                renderNotes(jsonNotes);
                            }
                            if (action === 'edit') {
                                var content = item.querySelector('.bubble-content');
                                var editContainer = item.querySelector('.bubble-edit-container');
                                var editEditor = item.querySelector('.bubble-edit-editor');

                                var editQuill = new Quill(editEditor, {
                                    theme: 'snow',
                                    modules: { toolbar: false }
                                });

                                editQuill.root.innerHTML = note.notes;

                                content.style.display = 'none';
                                editContainer.style.display = 'block';

                                item.querySelector('.bubble-save-btn').addEventListener('click', function() {
                                    var index = jsonNotes.findIndex(function(n) {
                                        return n.id === note.id;
                                    });
                                    if (index !== -1) {
                                        jsonNotes[index].notes = editQuill.root.innerHTML;
                                    }
                                    document.getElementById(paramName).value = JSON.stringify(jsonNotes);
                                    renderNotes(jsonNotes);
                                });

                                item.querySelector('.bubble-cancel-btn').addEventListener('click', function() {
                                    content.style.display = 'block';
                                    editContainer.style.display = 'none';
                                });
                            }
                        } else {
                            alert('Cannot edit or delete after 30 minutes!');
                        }
                    });
                }

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
