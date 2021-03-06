/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.pipeline.state;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import stroom.feed.MetaMap;
import stroom.util.spring.StroomScope;

@Component
@Scope(value = StroomScope.TASK)
public class MetaData {
    private final MetaMap metaMap = new MetaMap();

    public void put(final String key, final String value) {
        metaMap.put(key, value);
    }

    public void putAll(final MetaMap metaMap) {
        this.metaMap.putAll(metaMap);
    }

    public MetaMap getMetaMap() {
        return metaMap;
    }
}
