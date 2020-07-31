# What is this

This is the default implementation of HBCI protocol.

# hbci Sparkasse strange behavior with payments
Second payment transaction during a day with the same amount, debtor, creditor and purpose fails with errors:
```
HBCI error code: 9050:Die Nachricht enthält Fehler.
HBCI error code: 9010:Der Auftrag wurde nicht ausgeführt.
HBCI error code: 9390:Auftrag wegen Doppeleinreichung abgelehnt.
HBCI error code: 9010:Der Auftrag wurde nicht ausgeführt.
HBCI error code: 9390:Auftrag wegen Doppeleinreichung abgelehnt.
```
Seems like Sparkasse hbci server implementation calculate uniqueness by these fields and doesn't use `endToEndId` field.
As temporary workaround in bankProfile added `uniquePaymentPurpose` flag which adds in the end of purpose current date. 