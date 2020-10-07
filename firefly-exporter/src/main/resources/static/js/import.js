var accountClassName = 'opba-account-to-export';

function watchAccountExportJobStatus(statusElement, jobId, apiUrl) {
    const intervalId = setInterval(() => {
        fetch(`${apiUrl}/export-accounts/${jobId}`)
            .then((response) => response.json())
            .then((response) => {
                statusElement.innerText = `Exported ${response.accountsExported} of ${response.numAccountsToExport} accounts, errors ${response.numAccountsErrored}`;
                if (response.completed) {
                    clearInterval(intervalId);
                    statusElement.innerText = statusElement.innerText + '. Done'
                }
            })
    }, 1000);
}

function callAccountExport(fireflyTokenInputId, bankIdInputId, redirectElementId, statusElementId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;

    const statusElement = document.getElementById(statusElementId);
    statusElement.innerText = "Please wait...";
    statusElement.className = 'status';
    fetch(`${apiUrl}/${bankId}/export-accounts`, {method: 'POST', headers: {'FIREFLY-TOKEN': fireFlyToken}})
        .then((response) => {
            if (response.status === 202) {
                const redirectLink = document.getElementById(redirectElementId);
                redirectLink.href = response.headers.get('location');
                redirectLink.className = '';
                redirectLink.className = 'status';
                statusElement.className = 'hidden';
            } else if (response.status === 200) {
                response.text().then(id => watchAccountExportJobStatus(statusElement, id, apiUrl))
            } else {
                statusElement.innerText = "Error";
            }
        })
        .catch((err) => {
            console.error(`Failed fetching: ${err}`)
        });
}

function callExportableAccounts(fireflyTokenInputId, bankIdInputId, redirectElementId, statusElementId, accountExportBlockId, accountListElementId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;
    const accountList = document.getElementById(accountListElementId);
    accountList.innerHTML = '';

    const statusElement = document.getElementById(statusElementId);
    const exportBlock = document.getElementById(accountExportBlockId);
    statusElement.innerText = "Please wait...";
    statusElement.className = 'status';
    exportBlock.className = 'hidden';
    fetch(`${apiUrl}/${bankId}/exportable-accounts`, {headers: {'FIREFLY-TOKEN': fireFlyToken}})
        .then((response) => {
            if (response.status === 202) {
                const redirectLink = document.getElementById(redirectElementId);
                redirectLink.href = response.headers.get('location');
                redirectLink.className = '';
                statusElement.className = 'hidden';
            } else if (response.status === 200) {
                exportBlock.className = 'mt-2';
                accountList.innerHTML = '';
                response.json().then((accounts) => {
                    for (let i = 0; i < accounts.length; ++i) {
                        const elem = document.createElement("li");
                        const checkbox = document.createElement('input');
                        checkbox.setAttribute('id', accounts[i].resourceId);
                        checkbox.type = 'checkbox';
                        checkbox.checked = true;
                        checkbox.className = accountClassName + ' mr-2';
                        const accountId = document.createTextNode(accounts[i].iban);
                        elem.appendChild(checkbox);
                        elem.appendChild(accountId);
                        accountList.appendChild(elem);
                    }
                });
                statusElement.className = 'hidden';
            } else {
                statusElement.innerText = "Error";
            }
        })
        .catch((err) => {
            console.error(`Failed fetching: ${err}`)
        });
}

function watchTransactionExportJobStatus(statusElement, jobId, apiUrl) {
    const intervalId = setInterval(() => {
        fetch(`${apiUrl}/export-transactions/${jobId}`)
            .then((response) => response.json())
            .then((response) => {
                statusElement.innerText = `Exported transactions ${response.numTransactionsExported}, errors ${response.numTransactionsErrored}`
                if (response.completed) {
                    clearInterval(intervalId);
                    statusElement.innerText = statusElement.innerText + '. Done'
                }
            })
    }, 1000);
}

function callTransactionExport(fireflyTokenInputId, bankIdInputId, startDateElementId, endDateElementId, statusElementId, apiUrl) {
    const fireFlyToken = document.getElementById(fireflyTokenInputId).value;
    const bankId = document.getElementById(bankIdInputId).value;
    const start = document.getElementById(startDateElementId).valueAsDate.toISOString().split('T')[0];
    const end = document.getElementById(endDateElementId).valueAsDate.toISOString().split('T')[0];

    const statusElement = document.getElementById(statusElementId);
    statusElement.innerText = "Please wait...";
    statusElement.className = 'status';
    const accounts = document.getElementsByClassName(accountClassName);
    const accountIds = [];
    for (let i = 0; i < accounts.length; ++i) {
        if (!accounts[i].checked) {
            continue;
        }

        accountIds.push(accounts[i].id);
    }

    fetch(`${apiUrl}/${bankId}/${accountIds.join(",")}/export-transactions?dateFrom=${start}&dateTo=${end}`, {
        method: 'POST',
        headers: {'FIREFLY-TOKEN': fireFlyToken}
    })
        .then((response) => {
            if (response.status === 200) {
                response.text().then(id => watchTransactionExportJobStatus(statusElement, id, apiUrl))
            } else {
                statusElement.innerText = "Error";
            }
        })
        .catch((err) => {
            console.error(`Failed fetching: ${err}`)
        });
}
