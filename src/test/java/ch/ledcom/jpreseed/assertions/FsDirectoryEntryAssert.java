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

import de.waldheinz.fs.FsDirectoryEntry;
import org.assertj.core.api.AbstractAssert;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FsDirectoryEntryAssert extends AbstractAssert<FsDirectoryEntryAssert, FsDirectoryEntry> {

    private static final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    protected FsDirectoryEntryAssert(FsDirectoryEntry actual) {
        super(actual, FsDirectoryEntryAssert.class);
    }

    public static FsDirectoryEntryAssert assertThat(FsDirectoryEntry actual) {
        return new FsDirectoryEntryAssert(actual);
    }

    public final void hasBeenModifiedAt(Date modificationDate, long precision) throws IOException {
        isNotNull();

        if (Math.abs(actual.getLastModified() - modificationDate.getTime()) >= precision) {
            String lastModified = dateFormat.format(new Date(actual.getLastModified()));
            String modificationDateStr = dateFormat.format(modificationDate);
            failWithMessage("Entry <%s> has not been modified at <%s>. Last modification was on <%s>",
                    actual.getName(), modificationDateStr, lastModified);
        }
    }
}
