/*
 * Copyright The Microcks Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.microcks.operator.base.resources;

import io.github.microcks.operator.api.base.v1alpha1.Microcks;
import io.github.microcks.operator.api.base.v1alpha1.MicrocksSpec;
import io.github.microcks.operator.base.MicrocksReconciler;

import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * This is a unit test verifying that the {@code spec.imagePullSecrets} property is propagated into the
 * {@code PodSpec} of the dependent {@link Deployment} resources.
 * @author laurent
 */
class ImagePullSecretsTest {

   private Microcks buildMicrocks(List<LocalObjectReference> imagePullSecrets) throws Exception {
      MicrocksReconciler reconciler = new MicrocksReconciler(new KubernetesClientBuilder().build());
      MicrocksSpec spec = reconciler.loadDefaultMicrocksSpec("1.10.1");
      spec.setImagePullSecrets(imagePullSecrets);

      Microcks microcks = new Microcks();
      microcks.setMetadata(new ObjectMetaBuilder().withName("microcks").withNamespace("test").build());
      microcks.setSpec(spec);
      return microcks;
   }

   @Test
   void testImagePullSecretsArePropagatedToPostmanRuntimeDeployment() throws Exception {
      Microcks microcks = buildMicrocks(List.of(
            new LocalObjectReferenceBuilder().withName("my-private-registry-creds").build()));

      Deployment deployment = new PostmanRuntimeDeploymentDependentResource().desired(microcks, null);

      List<LocalObjectReference> pullSecrets = deployment.getSpec().getTemplate().getSpec().getImagePullSecrets();
      Assertions.assertNotNull(pullSecrets);
      Assertions.assertEquals(1, pullSecrets.size());
      Assertions.assertEquals("my-private-registry-creds", pullSecrets.get(0).getName());
   }

   @Test
   void testImagePullSecretsArePropagatedToMongoDBDeployment() throws Exception {
      Microcks microcks = buildMicrocks(List.of(
            new LocalObjectReferenceBuilder().withName("my-private-registry-creds").build()));

      Deployment deployment = new MongoDBDeploymentDependentResource().desired(microcks, null);

      List<LocalObjectReference> pullSecrets = deployment.getSpec().getTemplate().getSpec().getImagePullSecrets();
      Assertions.assertNotNull(pullSecrets);
      Assertions.assertEquals(1, pullSecrets.size());
      Assertions.assertEquals("my-private-registry-creds", pullSecrets.get(0).getName());
   }

   @Test
   void testNoImagePullSecretsByDefault() throws Exception {
      Microcks microcks = buildMicrocks(null);

      Deployment deployment = new PostmanRuntimeDeploymentDependentResource().desired(microcks, null);

      List<LocalObjectReference> pullSecrets = deployment.getSpec().getTemplate().getSpec().getImagePullSecrets();
      Assertions.assertTrue(pullSecrets == null || pullSecrets.isEmpty());
   }
}
