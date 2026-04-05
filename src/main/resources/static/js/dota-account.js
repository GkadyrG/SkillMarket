$(function () {
    function payload() {
        return {
            steamId: $("#steamId").val(),
            accountId: $("#accountId").val(),
            profileUrl: $("#profileUrl").val(),
            avatarUrl: $("#avatarUrl").val()
        };
    }

    function showResult(message, ok) {
        const block = $("#ajax-result");
        block.text(message).removeClass("text-success text-danger").addClass(ok ? "text-success" : "text-danger");
    }

    $("#ajax-save-btn").on("click", function () {
        const hasAccount = $(this).data("has-account") === true || $(this).data("has-account") === "true";
        const method = hasAccount ? "PUT" : "POST";

        $.ajax({
            url: "/api/account/dota",
            method: method,
            contentType: "application/json",
            data: JSON.stringify(payload())
        }).done(function () {
            showResult("Saved successfully via AJAX. Refreshing...", true);
            setTimeout(function () { window.location.reload(); }, 600);
        }).fail(function (xhr) {
            const msg = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : "Request failed";
            showResult(msg, false);
        });
    });

    $("#ajax-delete-btn").on("click", function () {
        $.ajax({
            url: "/api/account/dota",
            method: "DELETE"
        }).done(function () {
            showResult("Deleted successfully via AJAX. Refreshing...", true);
            setTimeout(function () { window.location.reload(); }, 600);
        }).fail(function (xhr) {
            const msg = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : "Request failed";
            showResult(msg, false);
        });
    });
});
