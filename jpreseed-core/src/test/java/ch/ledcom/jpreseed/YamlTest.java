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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class YamlTest {

    @Test
    public void test() throws URISyntaxException {
        DistroVersion trusty = new DistroVersion(
                "Trusty Tahr",
                "trusty",
                "14.04",
                new URI("http://test.iso/trusty"),
                new URI("http://test.usb/trusty"));
        DistroVersion saucy = new DistroVersion(
                "Saucy Salamander",
                "saucy",
                "13.10",
                new URI("http://test.iso/saucy"),
                new URI("http://test.usb/saucy"));

        Distribution ubuntu = new Distribution("Ubuntu", ImmutableSet.of(trusty, saucy));

        DistroVersion wheezy = new DistroVersion(
                "Wheezy",
                "wheezy",
                "7",
                new URI("http://test.iso/wheezy"),
                new URI("http://test.usb/wheezy"));

        Distribution debian = new Distribution("Debian", ImmutableSet.of(wheezy));

        Set<Distribution> distributions = ImmutableSet.of(ubuntu, debian);

        DumperOptions options = new DumperOptions();
        options.setAllowReadOnlyProperties(true);
        System.out.println(new Yaml(options).dump(distributions));
    }

}
