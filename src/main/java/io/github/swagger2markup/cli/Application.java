/*
 * Copyright 2016 Robert Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.swagger2markup.cli;

import io.airlift.airline.*;
import io.github.swagger2markup.OpenAPI2MarkupConverter;
import io.github.swagger2markup.config.builder.OpenAPI2MarkupConfigBuilder;
import io.github.swagger2markup.utils.URIUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Paths;

@Command(name = "convert", description = "Converts a Swagger JSON or YAML file into Markup documents.")
public class Application implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Inject
    public HelpOption helpOption;

    @Option(name = {"-i", "--swaggerInput"}, required = true, description = "Swagger input. Can either be a URL or a file path.")
    public String swaggerInput;

    @Option(name = {"-d", "--outputDir"}, description = "Output directory. Converts the Swagger specification into multiple files.")
    public String outputDir;

    @Option(name = {"-f", "--outputFile"}, description = "Output file. Converts the Swagger specification into one file.")
    public String outputFile;

    @Option(name = {"-c", "--config"}, description = "Config file.")
    public String configFile;

    public static void main(String[] args) {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("swagger2markup")
                    .withDescription("Converts a Swagger JSON or YAML file into Markup documents")
                    .withDefaultCommand(Help.class)
                    .withCommands(Help.class, Application.class);

        Cli<Runnable> gitParser = builder.build();

        gitParser.parse(args).run();
    }

    public void run() {

        OpenAPI2MarkupConverter.Builder converterBuilder = OpenAPI2MarkupConverter.from(URIUtils.create(swaggerInput));

        if(StringUtils.isNotBlank(outputFile)){
            converterBuilder.build().toFile(Paths.get(outputFile).toAbsolutePath());
        }else if (StringUtils.isNotBlank(outputDir)){
            converterBuilder.build().toFolder(Paths.get(outputDir).toAbsolutePath());
        }else {
            throw new IllegalArgumentException("Either outputFile or outputDir option must be used");
        }
    }

}
