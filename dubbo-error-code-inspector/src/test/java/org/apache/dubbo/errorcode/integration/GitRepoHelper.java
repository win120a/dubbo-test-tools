/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.errorcode.integration;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Helper class about Git Repository.
 */
class GitRepoHelper {

    /**
     * Clone dubbo and dubbo-website into a directory, and reset the repository to a pre-defined commit hash.
     *
     * @param workingPath the directory that both repositories in
     * @throws IOException when directory creation failed
     */
    static void initGitRepo(String workingPath) throws IOException {

        Path workingPathObject = Paths.get(workingPath);

        if (!Files.exists(workingPathObject)) {
            Files.createDirectory(workingPathObject);
        }

        // After this commit, it only has one wrong logger method invocation remaining.
        cloneRepoAndReset("https://github.com/apache/dubbo.git",
                workingPath + File.separator + "dubbo",
                "3c68e9476e1c418f50608897472e1b313eb6bc94");

        // Document missing error codes: 1-23, 1-24, 1-25.
        cloneRepoAndReset("https://github.com/apache/dubbo-website.git",
                workingPath + File.separator + "dubbo-website",
                "5ff7a6ea1299debe6e2a41a09939af26065cc3d0");
    }

    /**
     * Clone remote Git repository and reset the Git repository to a specified commit.
     *
     * @param remote remote Git repository address
     * @param destPath destination path
     * @param targetCommitHash target hash that will reset to
     */
    static void cloneRepoAndReset(String remote, String destPath, String targetCommitHash) {
        CloneCommand cloneCommand = new CloneCommand();

        System.out.println("Cloning " + remote + "...");
        cloneCommand.setDirectory(new File(destPath));
        cloneCommand.setURI(remote);

        try (Git git = cloneCommand.call()) {

            System.out.println("Resetting to commit: " + targetCommitHash + "...");
            git.reset().setRef(targetCommitHash)
                    .setMode(ResetCommand.ResetType.HARD)
                    .call();

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke maven wrapper in root directory to compile dubbo project.
     *
     * @param dubboRepository the location of dubbo repository
     */
    static void buildDubbo(String dubboRepository) {

        ProcessBuilder processBuilder = new ProcessBuilder();

        if (OS.current() == OS.WINDOWS) {
            processBuilder.command().add("cmd.exe");
            processBuilder.command().add("/c");
            processBuilder.command().add("mvnw.cmd");
        } else {
            processBuilder.command().add("./mvnw");
        }

        try {
            processBuilder.command().addAll(
                    Arrays.asList("-DskipTests", "clean", "package", "-T", "3C")
            );

            processBuilder.directory(new File(dubboRepository))
                    .inheritIO()
                    .start()
                    .waitFor();

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
