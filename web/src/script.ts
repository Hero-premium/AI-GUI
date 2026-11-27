function clearBox() {
    const inputBox = document.getElementById("inputBox");
    if (inputBox instanceof HTMLTextAreaElement) {
        inputBox.value = "";
    } else {
        console.error("inputBox not found or not a textarea");
    }
}

function showNotification(event: SubmitEvent) {
    event.preventDefault();
    alert((document.getElementById("inputBox") as HTMLTextAreaElement).value);
    clearBox();
}

function attachNotification() {
    const form = document.getElementById("message-form");
    if (form == null) {
        alert("bug :(");
        return;
    }
    form.addEventListener("submit", showNotification);
}

attachNotification();