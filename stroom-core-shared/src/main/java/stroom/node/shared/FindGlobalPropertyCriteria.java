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

package stroom.node.shared;

import stroom.entity.shared.FindNamedEntityCriteria;
import stroom.entity.shared.StringCriteria;

public class FindGlobalPropertyCriteria extends FindNamedEntityCriteria {
    private static final long serialVersionUID = 1451984883275627717L;
    private boolean addDefault = true;

    public static FindGlobalPropertyCriteria create(final String name) {
        FindGlobalPropertyCriteria criteria = new FindGlobalPropertyCriteria();
        criteria.setName(new StringCriteria(name, null));
        return criteria;

    }

    public boolean isAddDefault() {
        return addDefault;
    }

    public void setAddDefault(boolean addDefault) {
        this.addDefault = addDefault;
    }

}
