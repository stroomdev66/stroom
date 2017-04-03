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

package stroom.query.api;

import stroom.query.api.Format.Type;

public final class FieldBuilder {
    private String name;
    private String expression;
    private Sort sort;
    private Filter filter;
    private Format format;
    private Integer group;

    public FieldBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public FieldBuilder expression(final String expression) {
        this.expression = expression;
        return this;
    }

    public FieldBuilder sort(final Sort sort) {
        this.sort = sort;
        return this;
    }

    public FieldBuilder filter(final Filter filter) {
        this.filter = filter;
        return this;
    }

    public FieldBuilder format(final Format format) {
        this.format = format;
        return this;
    }

    public FieldBuilder format(final Type type) {
        this.format = new Format(type);
        return this;
    }

    public FieldBuilder group(final Integer group) {
        this.group = group;
        return this;
    }

    public Field build() {
        return new Field(name, expression, sort, filter, format, group);
    }
}