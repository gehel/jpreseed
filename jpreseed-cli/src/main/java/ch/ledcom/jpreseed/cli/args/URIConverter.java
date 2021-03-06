/**
 * Copyright (C) 2014 LedCom (guillaume.lederrey@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ledcom.jpreseed.cli.args;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

import java.net.URI;
import java.net.URISyntaxException;

public class URIConverter extends BaseConverter<URI> {

    public URIConverter(String optionName) {
        super(optionName);
    }

    @Override
    public final URI convert(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException ignore) {
            throw new ParameterException(getErrorString(value, "a URI"));
        }
    }
}
