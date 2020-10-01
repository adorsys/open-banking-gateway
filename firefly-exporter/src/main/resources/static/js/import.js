function watchAccountExportJobStatus(statusElement, jobId, apiUrl) {
    const intervalId = setInterval(() => {
        fetch(`${apiUrl}/export-accounts/${jobId}`)
            .then((response) => response.json())
            .then((response) => {
                if (response.completed) {
                    clearInterval(intervalId);
                }
                statusElement.innerText = `Exported ${response.accountsExported} of ${response.numAccountsToExport} accounts, errors ${response.numAccountsErrored}`
            })
    }, 1000);
}

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
            } else if (response.status === 200) {
                response.text().then(id => watchAccountExportJobStatus(statusElement, id, apiUrl))
            } else {
                statusElement.innerText = "Error";
            }
        })
        .catch((err) => {console.error(`Failed fetching: ${err}`)});
}

function callExportableAccounts(fireflyTokenInputId, bankIdInputId, redirectElementId, statusElementId, accountListElementId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;
    const accountList = document.getElementById(accountListElementId);
    accountList.innerHTML = ''

    const statusElement = document.getElementById(statusElementId);
    statusElement.innerText = "Please wait..."
    statusElement.className = '';
    fetch(`${apiUrl}/${bankId}/exportable-accounts`, {headers: {'FIREFLY-TOKEN': fireFlyToken}})
        .then((response) => {
            if (response.status === 202) {
                const redirectLink = document.getElementById(redirectElementId);
                redirectLink.href = response.headers.get('location');
                redirectLink.className = '';
                statusElement.className = 'hidden';
            } else if (response.status === 200) {
                response.json().then((accounts) => {
                    for (var i = 0; i < accounts.length; ++i) {
                        const elem = document.createElement("li");
                        elem.setAttribute('id', accounts[i].resourceId);
                        const accountElem = document.createElement('input');
                        accountElem.type = 'checkbox';
                        const linkText = document.createTextNode(accounts[i].iban);
                        accountElem.appendChild(linkText);
                        elem.appendChild(accountElem);
                        accountList.appendChild(elem);
                    }
                });
            } else {
                statusElement.innerText = "Error";
            }
        })
        .catch((err) => {console.error(`Failed fetching: ${err}`)});
}