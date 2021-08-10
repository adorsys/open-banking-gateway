def data = [
        "EINKUENFTE;EINKOMMEN;GEHALT	INCOME; INCOME; SALARY	RFN LIKE '%LOHN%GEHALT%' OR VWZ LIKE '%LOHN%GEHALT%'",
        "EINKUENFTE;EINKOMMEN;GEHALT	INCOME; INCOME; SALARY	VWZ LIKE '%AUSHILF%LOHN%'",
]

data.forEach {
    def split = it.split("\\t").toList()
    def eng = split[1]
    def expr = split[2]
    def cats = eng.split(";").toList()

    expr = expr.replaceAll('%', '.*')
            .replaceAll('\\bRFN\\b', 'referenceName')
            .replaceAll('\\bVWZ\\b', 'purpose')
            .replaceAll('\\bLIKE\\b', 'matches')
            .replaceAll('\\bOR\\b', '||')
    def pattern =
    """
    rule "$it"
        no-loop
        when
           \$t: AnalyzeableTransaction($expr)
        then
           \$t.category = "${cats[0]?.trim()}";
           \$t.subCategory = "${cats[1]?.trim()}";
           \$t.specification = "${cats[2]?.trim()}";

           update(\$t)
    end
    """

    println(pattern)
}