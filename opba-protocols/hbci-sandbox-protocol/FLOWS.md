# Typical request-response sequences for different HBCI banks:

## SPARDA
DialogInitAnon-Idn
CustomMsgRes-BPD

DialogEnd
CustomMsgRes-c0100 -> Dialog ID assigned

Synch-pin-12456
SynchRes-3920 -> Provided Bank Data

DialogEnd
CustomMsgRes

Synch-pin12456 & TAN2Step6
SynchRes-3920

DialogEnd
CustomMsgRes

DialogInit-pin12456 & TAN2Step6
CustomMsgRes-c0030 (security clearance) & GVRes.TAN2StepRes6.challenge

CustomMsg-TAN2Step6 & tan12456
CustomMsgRes-UPD(account numbers)

CustomMsg-pin-tan-KUmsZeit5 KTV-number (5578896155)
CustomMsgRes - GVRes.KUmsZeitRes5.booked

DialogEnd
CustomMsgRes



## ING-ACCOUNTS-TRANSACTION

DialogInitAnon
CustomMsgRes - 9400 (Der anonyme Dialog wird nicht unterstützt)

DialogEnd
CustomMsgRes

Synch-pin12345
SynchRes-BPD-Dialogid-First

DialogEnd
CustomMsgRes

Synch-pin12345
SynchRes-3920 (Zugelassene Ein- und Zwei-Schritt-Verfahren für den Benutzer)

DialogEnd
CustomMsgRes

DialogInit
CustomMsgRes-Dialogid-Second-3050. UPD nicht mehr aktuell. Aktuelle Version folgt + KInfo_3 (accounts)

CustomMsg-SEPAInfo1
CustomMsgRes-SEPAInfoRes1

DialogEnd
CustomMsgRes

Synch
SynchRes-DialogId-Third

DialogEnd
CustomMsgRes

DialogInit
CustomMsgRes-UPD

CustomMsg - KUmsZeit5
CustomMsgRes - KUmsZeitRes5 (transactions)

DialogEnd
CustomMsgRes


## SPARKASSE-ACCOUNTS



