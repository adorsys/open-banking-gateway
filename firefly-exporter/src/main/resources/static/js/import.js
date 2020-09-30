function callAccountImport(fireflyTokenInputId, bankIdInputId, redirectElementId, statusElementId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;

    const statusElement = document.getElementById(statusElementId);
    statusElement.innerText = "Please wait..."
    statusElement.className = '';
    fetch(`${apiUrl}/${bankId}/export-accounts`, {method: 'POST',  headers: {'FIREFLY-TOKEN': fireFlyToken}})
        .then((response) => {
            if (response.status === 202) {
                const redirectLink = document.getElementById(redirectElementId);
                redirectLink.href = response.headers.get('location');
                redirectLink.className = '';
                statusElement.className = 'hidden';
            }
        })
        .catch((err) => {console.error(`Failed fetching: ${err}`)});
}