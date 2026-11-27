function showNotification(event: SubmitEvent) {
    event.preventDefault();
    const form = document?.getElementById("message-form");
    const data = new FormData(form as HTMLFormElement);
    const text = data.get("message");
    alert(text);
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