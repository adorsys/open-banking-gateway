function callAccountImport(fireflyTokenInputId, bankIdInputId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;

    fetch(`${apiUrl}/${bankId}/export-accounts`, {method: 'POST',  headers: {'FIREFLY-TOKEN': fireFlyToken}})
        .then((response) => response.json())
        .catch((err) => {console.error(`Failed fetching: ${err}`)});
}