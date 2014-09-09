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

import ch.ledcom.jpreseed.cli.args.NonExistingPathValidator;
import com.beust.jcommander.ParameterException;
import org.junit.Test;

import static ch.ledcom.jpreseed.cli.TestFiles.HELLO_TXT;
import static ch.ledcom.jpreseed.cli.TestFiles.NON_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class NonExistingPathValidatorTest {

    private NonExistingPathValidator validator = new NonExistingPathValidator();

    @Test
    public void nonExisingPathValidate() {
        validator.validate("name", NON_EXISTING);
    }

    @Test(expected = ParameterException.class)
    public void exisingPathDoesNotValidate() {
        try {
            validator.validate("name", HELLO_TXT);
        } catch (ParameterException pe) {
            assertThat(pe.getMessage())
                    .contains("File <" + HELLO_TXT.toString())
                    .contains("option <" + "name");
            throw pe;
        }
    }

}
