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
package ch.ledcom.jpreseed;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import static java.util.Map.Entry;

public class DistroService {

    private final ImmutableMap<String, Distribution> distributionsByName;

    public DistroService(Set<Distribution> distributions) {
        ImmutableMap.Builder<String, Distribution> byNameBuilder = ImmutableMap.builder();
        for (Distribution distro : distributions) {
            byNameBuilder.put(distro.getName(), distro);
        }
        this.distributionsByName = byNameBuilder.build();
    }

    public final Distribution getDistributionByName(String name) {
        return distributionsByName.get(name);
    }

    public final ImmutableCollection<Distribution> getDistributions() {
        return distributionsByName.values();
    }

    public static DistroService create(InputStream configuration) throws URISyntaxException {
        ImmutableSet.Builder<Distribution> distributions = ImmutableSet.builder();

        Map<String, Map> yaml = (Map<String, Map>) new Yaml().load(configuration);

        for (Entry<String, Map> distribution : yaml.entrySet()) {
            distributions.add(new Distribution(distribution.getKey(), extractVersions(distribution.getValue())));
        }

        return new DistroService(distributions.build());
    }

    private static Set<DistroVersion> extractVersions(Map<String, Map<String, String>> versions) throws URISyntaxException {
        ImmutableSet.Builder<DistroVersion> result = ImmutableSet.builder();

        for (Entry<String, Map<String, String>> version : versions.entrySet()) {
            String name = version.getKey();
            Map<String, String> fields = version.getValue();
            String shortName = fields.get("shortName");
            String number = fields.get("number");
            URI isoImageUri = new URI(fields.get("isoImage"));
            URI usbImageUri = new URI(fields.get("usbImage"));
            result.add(new DistroVersion(name, shortName, number, isoImageUri, usbImageUri));
        }

        return result.build();
    }
}
