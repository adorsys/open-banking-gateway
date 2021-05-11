package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;


@Data
public class URILog implements NotSensitiveData {

    private String scheme;
    private String fragment;
    private String authority;
    private String userInfo;
    private String host;
    private int port;
    private String path;
    private String query;
    private String schemeSpecificPart;
    private int hash;
    private String decodedUserInfo;
    private String decodedAuthority;
    private String decodedPath;
    private String decodedQuery;
    private String decodedFragment;
    private String decodedSchemeSpecificPart;
    private String string;

    @Override
    public String getNotSensitiveData() {
        return "URILog("
                + "urlHash=" + getHash()
                + ")";
    }
}
