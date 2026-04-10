package org.xpen.hello.dl.basic;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.repository.Artifact;
import ai.djl.repository.MRL;
import ai.djl.repository.zoo.ModelZoo;

public class ListModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListModels.class);

    public static void main(String[] args) throws Exception {
        boolean withArtifacts = true;
        Map<Application, List<MRL>> models = ModelZoo.listModels();
        for (Map.Entry<Application, List<MRL>> entry : models.entrySet()) {
            String appName = entry.getKey().toString();
            for (MRL mrl : entry.getValue()) {
                if (withArtifacts) {
                    for (Artifact artifact : mrl.listArtifacts()) {
                    	LOGGER.info("{} djl://{}", appName, artifact);
                    }
                } else {
                	LOGGER.info("{} {}", appName, mrl);
                }
            }
        }
    }
}
