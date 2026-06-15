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
            background: var(--notes-primary-color, #0d6efd) !important;
            color: var(--notes-primary-text, #fff) !important;
            border: 1px solid rgba(0,0,0,0.1);
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
            padding-right: 10px;
        }
        .bubble-mine .bubble-header .bubble-name {
            color: var(--notes-primary-text, #fff);
        }
        .bubble-mine .bubble-header .bubble-date {
            color: var(--notes-primary-text, #e0e0e0);
            opacity: 0.75;
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
        .bubble-body {
            color: inherit;
        }
        .bubble-body p {
            margin: 0 0 4px 0;
            color: inherit;
        }
        .bubble-body a {
            color: inherit;
            text-decoration: underline;
        }
        .bubble-mine .bubble-body,
        .bubble-mine .bubble-body p,
        .bubble-mine .bubble-body span,
        .bubble-mine .bubble-body li,
        .bubble-mine .bubble-body a,
        .bubble-mine .bubble-body strong,
        .bubble-mine .bubble-body em,
        .bubble-mine .bubble-body u {
            color: var(--notes-primary-text, #fff) !important;
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

        .bubble-body-row {
            display: flex;
            flex-direction: row;
            align-items: flex-start;
            gap: 4px;
        }
        .bubble-arrow-menu {
            position: absolute;
            right: 8px;
            opacity: 0;
            transition: opacity 0.2s;
            flex-shrink: 0;
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

        @keyframes spin-clock {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        .icon-pending {
            display: inline-block;
            vertical-align: middle;
            margin-left: 6px;
            animation: spin-clock 2s linear infinite;
        }
        .bubble-mine .icon-pending {
            color: var(--notes-primary-text, #fff);
        }

    </style>
</div>

<link rel="stylesheet" href="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.snow.css" />
<script src="${request.contextPath}/plugin/${className}/node_modules/quill/dist/quill.js"></script>

<script type="text/javascript">
    (function(){
        // === Detect Kecak/Joget primary color from themed elements ===
        function detectPrimaryColor() {
            // List of selectors commonly themed by Kecak/Joget with primary color
            var selectors = [
                '#sidebar',
                '.sidebar',
                '.page-header',
                '.navbar',
                '.navbar-header',
                'nav.navbar',
                '.navbar-default .navbar-brand',
                '#header',
                '.header',
                '.main-header',
                '.nav-header',
                '.sidebar-nav',
                '.left-sidebar',
                '#left-panel',
                '.topbar',
                '.top-bar',
                '.btn-primary'
            ];
            for (var i = 0; i < selectors.length; i++) {
                var el = document.querySelector(selectors[i]);
                if (el) {
                    var bg = window.getComputedStyle(el).backgroundColor;
                    // Skip transparent, white, or near-white backgrounds
                    if (bg && bg !== 'rgba(0, 0, 0, 0)' && bg !== 'transparent' && bg !== 'rgb(255, 255, 255)' && bg !== 'rgb(248, 249, 250)' && bg !== 'rgb(245, 245, 245)') {
                        return bg;
                    }
                }
            }
            return null;
        }

        // Calculate contrasting text color (white or dark) based on background luminance
        function getContrastText(rgbStr) {
            var match = rgbStr.match(/\d+/g);
            if (!match || match.length < 3) return '#fff';
            var r = parseInt(match[0]), g = parseInt(match[1]), b = parseInt(match[2]);
            // Relative luminance formula (sRGB)
            var luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
            return luminance > 0.5 ? '#111' : '#fff';
        }

        function applyPrimaryColor() {
            var color = detectPrimaryColor();
            if (color) {
                var textColor = getContrastText(color);
                var wrappers = document.querySelectorAll('.notes-element-wrapper');
                wrappers.forEach(function(w) {
                    w.style.setProperty('--notes-primary-color', color);
                    w.style.setProperty('--notes-primary-text', textColor);
                });
            }
        }

        // Run detection after a short delay to ensure theme styles are loaded
        if (document.readyState === 'complete') {
            applyPrimaryColor();
        } else {
            window.addEventListener('load', applyPrimaryColor);
        }
        // Also try immediately in case elements are already rendered
        setTimeout(applyPrimaryColor, 100);
        setTimeout(applyPrimaryColor, 500);
        // === End primary color detection ===

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

                    var uiNote = {
                        id: '',
                        tempId: 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
                        name: displayName,
                        username: userName,
                        dateLabel: 'Today',
                        dateStr: new Date().toLocaleString(),
                        notes: html,
                        type: 'note'
                    };
                    jsonNotes.push(uiNote);

                    renderNotes(jsonNotes);
                    updatePayload();
                    quill.setContents([]);
                });
            }

            var alertMessage = '${(alertMessage!"")?js_string}';

            if (alertMessage !== '') {
                document.addEventListener('click', function(e) {
                    var target = e.target;
                    if (target &&
                        (target.classList.contains('waves-button-input') ||
                         (target.type === 'submit' && target.id === 'submit'))) {

                        var text = quill.getText().trim();

                        if (text !== "" && quill.getLength() > 1) {
                            e.preventDefault();
                            e.stopImmediatePropagation();
                            alert(alertMessage);
                            return false;
                        }
                    }
                }, true);
            }
        }

        renderNotes(jsonNotes);
        setTimeout(scrollFormToTop, 50);

        function updatePayload() {
            var payloadValue = "";
            if (!isMultirow) {
                payloadValue = JSON.stringify(jsonNotes);
                var cleaned = jsonNotes.map(function(n) {
                    var copy = Object.assign({}, n);
                    if (copy.tempId && (!copy.id || copy.id === '')) {
                        copy.id = copy.tempId;
                    }
                    delete copy.tempId;
                    return copy;
                });
                payloadValue = JSON.stringify(cleaned);
            } else {
                var pending = jsonNotes.filter(function(n) {
                    return !n.id || n.id === '';
                }).map(function(n) {
                    return {
                        notes: n.notes,
                        type: n.type || 'note'
                    };
                });
                payloadValue = JSON.stringify(pending);
            }

            var allInputs = document.querySelectorAll('input[type="hidden"][name="' + paramName + '"]');
            allInputs.forEach(function(inp) {
                inp.value = payloadValue;
            });
            console.log('[Notes DEBUG] Payload Updated:', payloadValue);
        }

        function renderNotes(notes) {
            var container = document.getElementById('note-list-' + paramName);
            container.innerHTML = '';

            var lastDate = null;

            notes.slice().reverse().forEach(function(note) {
                var item = document.createElement('div');
                
                var dateStr = note.dateStr || '';
                var dateLabel = note.dateLabel || '';
                var isSaved;
                if (isMultirow) {
                    isSaved = note.id && note.id !== '';
                } else {
                    isSaved = !note.tempId;
                    if (dateStr) {
                        var d = new Date(dateStr);
                        if (!isNaN(d.getTime())) {
                            dateLabel = getDateLabel(d.toDateString());
                        }
                    }
                }

                var noteDate = dateLabel;

                if (noteDate !== lastDate && noteDate !== '') {
                    lastDate = noteDate;
                    var separator = document.createElement('div');
                    separator.className = 'date-separator';
                    separator.textContent = noteDate;
                    container.appendChild(separator);
                }

                var dropdownHtml = '';
                if(note.username === userName && !isSaved){
                    dropdownHtml =
                        '<div class="bubble-arrow-menu">' +
                            '<span class="bubble-arrow">&#9662;</span>' +
                            '<div class="bubble-dropdown" style="display:none">' +
                                '<div class="bubble-dropdown-item" data-action="edit">Edit</div>' +
                                '<div class="bubble-dropdown-item" data-action="delete">Delete</div>' +
                            '</div>' +
                        '</div>'
                }

                //var bubbleState = isSaved ? ' bubble-saved' : ' bubble-unsaved';

                item.innerHTML =
                    '<div class="note-bubble ' + (note.username === userName ? 'bubble-mine' : 'bubble-others') + '">' +
                        '<div class="bubble-content">' +
                            '<div class="bubble-header">' +
                                '<span class="bubble-name">' + note.name + '</span>' +
                                (isSaved ? '<span class="bubble-date">' + dateStr + '</span>' : '') +
                                (!isSaved ?
                                    '<svg class="icon-pending" title="Unsaved" xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">' +
                                        '<circle cx="12" cy="12" r="10"></circle>' +
                                        '<polyline points="12 6 12 12 16 14"></polyline>' +
                                    '</svg>'
                                : '') +
                            '</div>' +
                            '<div class="bubble-body-row">' +
                                dropdownHtml +
                                '<div class="bubble-body">' + note.notes + '</div>' +
                            '</div>' +
                        '</div>' +
                        '<div class="bubble-edit-container" style="display:none">' +
                            '<div class="bubble-edit-editor"></div>' +
                            '<button type="button" class="bubble-save-btn">Save</button>' +
                            '<button type="button" class="bubble-cancel-btn">Cancel</button>' +
                        '</div>' +
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
                        if (action === 'delete') {
                            if (!confirm('Delete this note?')) return;
                            var index = jsonNotes.findIndex(function(n) {
                                return n.tempId === note.tempId;
                            });

                            if (index !== -1) {
                                jsonNotes.splice(index, 1);
                            }

                            updatePayload();
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
                                    return n.tempId === note.tempId;
                                });
                                if (index !== -1) {
                                    jsonNotes[index].notes = editQuill.root.innerHTML;
                                }
                                updatePayload();
                                renderNotes(jsonNotes);
                            });

                            item.querySelector('.bubble-cancel-btn').addEventListener('click', function() {
                                content.style.display = 'block';
                                editContainer.style.display = 'none';
                            });
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
            var d = new Date(dateStr);
            if (isNaN(d.getTime())) return '';

            var today = new Date().toDateString();
            var yesterday = new Date(Date.now() - 86400000).toDateString();

            var targetDateStr = d.toDateString();

            if (targetDateStr === today) return 'Today';
            if (targetDateStr === yesterday) return 'Yesterday';

            return d.toLocaleDateString('id-ID', {
                day: 'numeric', month: 'long', year: 'numeric'
            });
        }

        function isWithin30Minutes(date1, date2) {
            let diffInMs = Math.abs(date1 - date2);
            return diffInMs < 30 * 60 * 1000;
        }

        function scrollFormToTop() {
            var formCell = document.getElementById('note-list-' + paramName);
            if (formCell) {
                formCell.closest('.form-cell').scrollIntoView({ block: 'start', behavior: 'instant' });
            } else {
                window.scrollTo(0, 0);
            }
        }

    })();
</script>
