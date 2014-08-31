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
package ch.ledcom.jpreseed.cli;

import com.beust.jcommander.ParameterException;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static ch.ledcom.jpreseed.TestFiles.HELLO_TXT;
import static ch.ledcom.jpreseed.TestFiles.NON_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class URIConverterTest {

    private URIConverter converter = new URIConverter("optionName");

    @Test
    public void convertUrl() throws URISyntaxException {
        assertThat(converter.convert("http://example.net/")).isEqualTo(new URI("http://example.net/"));
    }

    @Test(expected = ParameterException.class)
    public void invalidUriThrowsException() {
        try {
            converter.convert("^invalid^");
        } catch (ParameterException pe) {
            assertThat(pe.getMessage()).contains("^invalid^");
            throw pe;
        }
    }

}