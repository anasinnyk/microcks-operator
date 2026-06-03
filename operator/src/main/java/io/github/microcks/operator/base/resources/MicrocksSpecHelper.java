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

import io.github.microcks.operator.api.base.v1alpha1.MicrocksServiceSpec;

import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for MicrocksSpec.
 * @author laurent
 */
public class MicrocksSpecHelper {

   /** Get a JBoss logging logger. */
   private static final Logger logger = Logger.getLogger(MicrocksSpecHelper.class);

   private MicrocksSpecHelper() {
      // Private constructor for utility class.
   }

   /**
    * Extracts and returns the major version of the Microcks application
    * by parsing the tag of the Microcks container image.
    * If the tag cannot be parsed, or if the version is set to "nightly",
    * a default major version of 1 is returned.
    *
    * @param spec the {@code MicrocksSpec} object containing the configuration and details
    *             of the Microcks instance, including the image tag and version.
    * @return the major version as an integer extracted from the image tag,
    *         or the default value of 1 if parsing is unsuccessful.
    */
   public static int getMicrocksMajorVersion(io.github.microcks.operator.api.base.v1alpha1.MicrocksSpec spec) {
      if ("nightly".equals(spec.getVersion())) {
         return 1;
      }

      // Inspect version in details.
      int[] parts = getVersionParts(spec.getVersion());
      if (parts.length > 0) {
         return parts[0];
      }

      // Inspect the version of Microcks in image tag.
      MicrocksServiceSpec microcksSpec = spec.getMicrocks();
      parts = getVersionParts(microcksSpec.getImage().getTag());
      if (parts.length > 0) {
         return parts[0];
      }
      return 1;
   }

   /**
    * Extracts and returns the minor version of the Microcks application
    * by parsing the tag of the Microcks container image. If the tag cannot
    * be parsed or does not contain a valid minor version, and the version
    * is set to "nightly", a default value of 14 is returned. Otherwise,
    * a fallback value of 1 is returned.
    *
    * @param spec the {@code MicrocksSpec} object containing the configuration and details
    *             of the Microcks instance, including the image tag and version.
    * @return the minor version as an integer extracted from the image tag,
    *         or the default value of 14 if the version is "nightly", or 1 otherwise.
    */
   public static int getMicrocksMinorVersion(io.github.microcks.operator.api.base.v1alpha1.MicrocksSpec spec) {
      if ("nightly".equals(spec.getVersion())) {
         return 15;
      }
      // Inspect version in details.
      int[] parts = getVersionParts(spec.getVersion());
      if (parts.length > 1) {
         return parts[1];
      }

      // Inspect the version of Microcks in image tag.
      MicrocksServiceSpec microcksSpec = spec.getMicrocks();
      parts = getVersionParts(microcksSpec.getImage().getTag());
      if (parts.length > 1) {
         return parts[1];
      }
      return 14;
   }

   private static int[] getVersionParts(String version) {
      String[] parts = version.split("\\.");
      int[] versionParts = new int[parts.length];
      try {
         for (int i = 0; i < parts.length; i++) {
            versionParts[i] = Integer.parseInt(parts[i]);
         }
      } catch (Exception e) {
         logger.warnf("Cannot parse Microcks version '%s'", version);
      }
      return versionParts;
   }
}
