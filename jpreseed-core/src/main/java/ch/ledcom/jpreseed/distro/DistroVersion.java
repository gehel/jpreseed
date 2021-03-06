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
package ch.ledcom.jpreseed.distro;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
@ThreadSafe
public class DistroVersion {

    private final String name;
    private final String shortName;
    private final String number;
    private final URI isoImageUri;
    private final URI usbImageUri;

    public DistroVersion(String name, String shortName, String number, URI isoImageUri, URI usbImageUri) {
        this.name = checkNotNull(name, "Version name cannot be null");
        this.shortName = checkNotNull(shortName, "Short name cannot be null");
        this.number = checkNotNull(number, "Version number cannot be null");
        this.isoImageUri = checkNotNull(isoImageUri, "ISO image URI cannot be null");
        this.usbImageUri = checkNotNull(usbImageUri, "USB image URI cannot be null");
    }

    public final String getName() {
        return name;
    }

    public final String getShortName() {
        return shortName;
    }

    public final String getNumber() {
        return number;
    }

    public final URI getIsoImageUri() {
        return isoImageUri;
    }

    public final URI getUsbImageUri() {
        return usbImageUri;
    }

    @Override
    public final String toString() {
        return name;
    }
}
