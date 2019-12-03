/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.opba.consent.embedded.rest.api.domain.account;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum UsageTypeTO {
    PRIV("PRIV"),
    ORGA("ORGA");

    private static final Map<String, UsageTypeTO> CONTAINER = new HashMap<>();

    static {
        for (UsageTypeTO usageType : values()) {
            CONTAINER.put(usageType.getValue(), usageType);
        }
    }

    private String value;


    UsageTypeTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static Optional<UsageTypeTO> getByValue(String value) {
        return Optional.ofNullable(CONTAINER.get(value));
    }
}
