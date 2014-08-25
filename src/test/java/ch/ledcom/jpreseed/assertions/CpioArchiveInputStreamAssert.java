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
package ch.ledcom.jpreseed.assertions;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.assertj.core.api.AbstractAssert;

import java.io.IOException;

public class CpioArchiveInputStreamAssert extends AbstractAssert<CpioArchiveInputStreamAssert, CpioArchiveInputStream> {
    protected CpioArchiveInputStreamAssert(CpioArchiveInputStream actual) {
        super(actual, CpioArchiveInputStreamAssert.class);
    }

    public static CpioArchiveInputStreamAssert assertThat(CpioArchiveInputStream actual) {
        return new CpioArchiveInputStreamAssert(actual);
    }

    public void hasEntry(String name) throws IOException {
        isNotNull();

        CpioArchiveEntry cpioEntry;
        while ((cpioEntry = actual.getNextCPIOEntry()) != null) {
            if (cpioEntry.getName().equals(name)) {
                return;
            }
        }

        failWithMessage("Expected entry %s was not found in archive", name);
    }
}
