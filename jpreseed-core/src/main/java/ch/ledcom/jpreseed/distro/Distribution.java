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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
@ThreadSafe
public class Distribution {

    private final String name;
    private final ImmutableList<DistroVersion> versions;
    private final ImmutableMap<String, DistroVersion> versionsByName;
    private final ImmutableMap<String, DistroVersion> versionsByShortName;
    private final ImmutableMap<String, DistroVersion> versionsByNumber;

    public Distribution(String name, List<DistroVersion> versions) {
        this.name = checkNotNull(name, "Distribution name cannot be null");
        ImmutableMap.Builder<String, DistroVersion> byNameBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, DistroVersion> byShortNameBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, DistroVersion> byNumberBuilder = ImmutableMap.builder();

        for (DistroVersion version : versions) {
            byNameBuilder.put(version.getName(), version);
            byShortNameBuilder.put(version.getShortName(), version);
            byNumberBuilder.put(version.getNumber(), version);
        }

        this.versions = ImmutableList.copyOf(versions);
        this.versionsByName = byNameBuilder.build();
        this.versionsByShortName = byShortNameBuilder.build();
        this.versionsByNumber = byNumberBuilder.build();
    }

    public final String getName() {
        return name;
    }

    public final ImmutableList<DistroVersion> getVersions() {
        return versions;
    }

    public final DistroVersion getVersionByName(String name) {
        return versionsByName.get(name);
    }

    public final DistroVersion getVersionByShortName(String shortName) {
        return versionsByShortName.get(shortName);
    }

    public final DistroVersion getVersionByNumber(String number) {
        return versionsByNumber.get(number);
    }

    @Override
    public final String toString() {
        return name;
    }
}
