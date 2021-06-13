package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;


/**
 * Information for pagination
 */
@Value
@Builder
public class Paging {

    /**
     * Current page number
     */
    private Integer page;

    /**
     * Current page size (number of entries in this page)
     */
    private Integer perPage;

    /**
     * Total number of pages
     */
    private Integer pageCount;

    /**
     * Total number of entries across all pages
     */
    private Integer totalCount;
}
