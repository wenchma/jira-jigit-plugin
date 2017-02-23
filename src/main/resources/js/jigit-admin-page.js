AJS.$(function () {
    AJS.$("#add-jigit-repo-button").click(showAddDialog);
    AJS.$(".edit-jigit-repo-button").click(showEditDialog);
    AJS.$(".add-jigit-repo-branch-button").click(addRepoBranch);
    AJS.$("body").delegate("#change_token", "click", function() {
        var checkbox = AJS.$(this);
        if (checkbox.attr("checked") == 'checked') {
            checkbox.closest("form").find("[name='token']").prop('disabled', false);
        } else {
            checkbox.closest("form").find("[name='token']").prop('disabled', true);
        }
    });
    AJS.$(".remove-jigit-repo-button, .activity-jigit-repo-button").click(function(){
        getActionTriggerElement(this).
            closest("tr").
            find("." + AJS.$(this).attr("data-form")).
            submit();
    });

    function addRepoBranch() {
        var form = AJS.$(this).closest("tr").find("form");
        var data = getFormData(form);

        AJS.$.ajax({
            url: form.attr("action"),
            type : "POST",
            dataType : "json",
            data : data,
            async : false,
            error : function(xhr, ajaxOptions, thrownError) {
                AJS.$("#page-message-container").empty();
                AJS.messages.error("#page-message-container", {
                    closeable: true,
                    body: xhr.statusText + (": "  + xhr.responseText) || ""
                });
            },
            success : function() {
                form.get(0).reset();
                location.reload();
            }
        });
    }

    function showAddDialog() {
        var interior = AJS.$("#add-jigit-repo-container").clone();
        showDialog(interior, "add-jigit-repo-dialog");
    }

    function getActionTriggerElement(action) {
        return AJS.$("[href='#" + AJS.$(action).closest("div").attr("id") + "']");
    }

    function showEditDialog() {
        var interior = AJS.$("#edit-jigit-repo-container").clone();

        getActionTriggerElement(this).
            closest("tr").
            find("[data-val][data-name]").each(function() {
                var element = AJS.$(this);
                interior.find("[name='" + element.attr("data-name") + "']").val(element.attr("data-val"));
            });

        showDialog(interior, "edit-jigit-repo-dialog");
    }

    function showDialog(interior, dialogId) {
        var dialog = new AJS.Dialog({
            width: 500,
            height: 510,
            id: dialogId
        });

        JIRA.bind("Dialog.beforeHide", function(event, dialog, reason){
            return dialog.attr("id") != dialogId || reason != "esc";
        });

        dialog.addHeader(AJS.I18n.getText("jigit.settings.table.columns.jigit.project"));

        dialog.addPanel("Repo", interior, "");
        interior.show();

        dialog.addButton(
            "Ok",
            function () {
                var form = interior.find("form");
                var data = getFormData(form);

                AJS.$.ajax({
                    url: form.attr("action"),
                    type : "POST",
                    dataType : "json",
                    data : data,
                    async : false,
                    error : function(xhr, ajaxOptions, thrownError) {
                        AJS.$("#" + dialogId + " .message-container").empty();
                        AJS.messages.error("#" + dialogId + " .message-container", {
                            closeable: true,
                            body: xhr.statusText + (": "  + xhr.responseText) || ""
                        });
                    },
                    success : function() {
                        dialog.remove();
                        form.get(0).reset();
                        location.reload();
                    }
                });
            },
            "aui-button"
        );

        dialog.addCancel(
            "Cancel",
            function (dialog) {
                dialog.remove();
            }
        );

        dialog.show();
    }

    function getFormData(form) {
        if (!form) {
            return {};
        }
        var data = {};
        form.find(".form-data").each(function() {
            var element = AJS.$(this);
            if (element.is(":checkbox")) {
                data[element.attr("name")] = element.attr("checked") == "checked";
            } else {
                data[element.attr("name")] = element.val();
            }
        });

        return data;
    }
});