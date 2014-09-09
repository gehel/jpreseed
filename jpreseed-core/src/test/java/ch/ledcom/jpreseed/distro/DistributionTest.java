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

import ch.ledcom.jpreseed.distro.Distribution;
import ch.ledcom.jpreseed.distro.DistroVersion;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class DistributionTest {

    private Distribution ubuntu;

    @Before
    public void initDistribution() throws URISyntaxException {
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

        ubuntu = new Distribution("Ubuntu", ImmutableList.of(trusty, saucy));
    }

    @Test
    public void gettingVersionByName() throws URISyntaxException {
        DistroVersion trusty = ubuntu.getVersionByName("Trusty Tahr");
        assertThat(ubuntu.toString()).isEqualTo("Ubuntu");
        assertThat(trusty).isNotNull();
        assertThat(trusty.getName()).isEqualTo("Trusty Tahr");
        assertThat(trusty.getShortName()).isEqualTo("trusty");
        assertThat(trusty.getNumber()).isEqualTo("14.04");
        assertThat(trusty.getIsoImageUri()).isEqualTo(new URI("http://test.iso/trusty"));
        assertThat(trusty.getUsbImageUri()).isEqualTo(new URI("http://test.usb/trusty"));
        assertThat(trusty.toString()).isEqualTo("Trusty Tahr");
    }

    @Test
    public void checkVersionList() {
        ImmutableCollection<DistroVersion> versions = ubuntu.getVersions();
        assertThat(versions).extracting("shortName")
                .contains("trusty")
                .contains("saucy");
    }

    @Test
    public void gettingVersionByShortName() throws URISyntaxException {
        DistroVersion trusty = ubuntu.getVersionByShortName("trusty");
        assertThat(trusty).isNotNull();
    }

    @Test
    public void gettingVersionByNumber() throws URISyntaxException {
        DistroVersion trusty = ubuntu.getVersionByNumber("14.04");
        assertThat(trusty).isNotNull();
    }

    @Test
    public void gettingNonExistingVersion() throws URISyntaxException {
        DistroVersion nonExistingVersion = ubuntu.getVersionByName("non-existing");
        assertThat(nonExistingVersion).isNull();
    }
}
