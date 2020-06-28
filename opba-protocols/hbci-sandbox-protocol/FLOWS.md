# Typical request-response sequences for different HBCI banks:

## SPARDA

1. DialogInitAnon-Idn
1. CustomMsgRes-BPD

2. DialogEnd
2. CustomMsgRes-c0100 -> Dialog ID assigned (and kept)

3. Synch-pin-12456
3. SynchRes-3920 -> Provided Bank Data

4. DialogEnd
4. CustomMsgRes

5. Synch-pin12456 & TAN2Step6
5. SynchRes-3920

6. DialogEnd
6. CustomMsgRes

7. DialogInit-pin12456 & TAN2Step6
7. CustomMsgRes-c0030 (security clearance) & GVRes.TAN2StepRes6.challenge

8. CustomMsg-TAN2Step6 & tan12456
8. CustomMsgRes-UPD(account numbers)

9. CustomMsg-pin-tan-KUmsZeit5 KTV-number (5578896155)
9. CustomMsgRes - GVRes.KUmsZeitRes5.booked

10. DialogEnd
10. CustomMsgRes



## ING-ACCOUNTS-TRANSACTION

1. DialogInitAnon
1. CustomMsgRes - 9400 (Der anonyme Dialog wird nicht unterstützt)

2. DialogEnd
2. CustomMsgRes

3. Synch-pin12345
3. SynchRes-BPD-Dialogid-First

4. DialogEnd
4. CustomMsgRes

5. Synch-pin12345
5. SynchRes-3920 (Zugelassene Ein- und Zwei-Schritt-Verfahren für den Benutzer)

6. DialogEnd
6. CustomMsgRes

7. DialogInit
7. CustomMsgRes-Dialogid-Second-3050. UPD nicht mehr aktuell. Aktuelle Version folgt + KInfo_3 (accounts)

8. CustomMsg-SEPAInfo1
8. CustomMsgRes-SEPAInfoRes1

9. DialogEnd
9. CustomMsgRes

10. Synch
10. SynchRes-DialogId-Third

11. DialogEnd
11. CustomMsgRes

12. DialogInit
12. CustomMsgRes-UPD

13. CustomMsg - KUmsZeit5
13. CustomMsgRes - KUmsZeitRes5 (transactions)

14. DialogEnd
14. CustomMsgRes


## SPARKASSE-ACCOUNTS

1. DialogInitAnon
1. CustomMsgRes - BPD + First dialog id

2. DialogEnd
2. CustomMsgRes

3. Synch + pin-123456 -> Dialog id 1
3. SynchRes - 3920 "Zugelassene Zwei-Schritt-Verfahren für den Benutzer.

4. DialogEnd
4. CustomMsgRes

5. DialogInit + pin+TAN2Step6
5. CustomMsgRes + TAN2StepRes6 (3920 Zugelassene Zwei-Schritt-Verfahren für den Benutzer.) -> Dialog id 2

6. CustomMsg + TANMediaList4
6. CustomMsgRes + TANMediaListRes4

7. DialogEnd
7. CustomMsgRes

8. Synch + pin + TAN2Step6
8. SynchRes + TAN2StepRes6 -> Dialog id 3

9. DialogEnd
9. CustomMsgRes

10. DialogInit + TAN2Step6
10. CustomMsgRes + UPD + KInfo -> Dialog-id-4

11. CustomMsg + KUmsZeitCamt1 + iban
11. CustomMsgRes + '"Bitte geben Sie die pushTAN ein.'

12. CustomMsg + TAN2Step6 + pin + tan
12. CustomMsgRes - Die eingegebene TAN ist falsch. (MBV07390100162)


