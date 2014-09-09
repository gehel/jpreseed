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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DistroServiceTest {

    private DistroService distroService;

    @Before
    public void initDistributionService() throws IOException, URISyntaxException {
        try (InputStream config = getClass().getResourceAsStream("/distributions.yaml")) {
            distroService = DistroService.create(config);
        }
    }

    @Test
    public void loadingDistributionListFromYaml() throws URISyntaxException {
        Collection<Distribution> distributions = distroService.getDistributions();

        assertThat(distributions).extracting("name")
                .contains("Ubuntu")
                .contains("Debian");

        Distribution ubuntu = distroService.getDistributionByName("Ubuntu");
        assertThat(ubuntu).isNotNull();

        DistroVersion trusty = ubuntu.getVersionByShortName("trusty");
        assertThat(trusty).isNotNull();
        assertThat(trusty.getName()).isEqualTo("Trusty Tahr");
        assertThat(trusty.getShortName()).isEqualTo("trusty");
        assertThat(trusty.getNumber()).isEqualTo("14.04");
        assertThat(trusty.getIsoImageUri()).isEqualTo(new URI("http://archive.ubuntu.com/ubuntu/dists/trusty-updates/main/installer-amd64/current/images/netboot/mini.iso"));
        assertThat(trusty.getUsbImageUri()).isEqualTo(new URI("http://archive.ubuntu.com/ubuntu/dists/trusty-updates/main/installer-amd64/current/images/netboot/boot.img.gz"));
    }

    @Test
    public void flattenedDistributionsAreCorrect() {
        List<DistroAndVersion> flattenedDistros = distroService.getFlattenedVersions();

        assertThat(flattenedDistros).hasSize(4);

        DistroAndVersion trusty = flattenedDistros.get(0);
        assertThat(trusty.getDistribution().getName()).isEqualTo("Ubuntu");
        assertThat(trusty.getVersion().getName()).isEqualTo("Trusty Tahr");
    }

}
