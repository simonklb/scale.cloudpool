package com.elastisys.scale.cloudpool.aws.autoscaling.lab;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elastisys.scale.cloudpool.api.CloudPool;
import com.elastisys.scale.cloudpool.aws.autoscaling.driver.AwsAsPoolDriver;
import com.elastisys.scale.cloudpool.aws.autoscaling.driver.client.AwsAutoScalingClient;
import com.elastisys.scale.cloudpool.commons.basepool.BaseCloudPool;
import com.elastisys.scale.cloudpool.commons.basepool.StateStorage;
import com.elastisys.scale.cloudpool.commons.util.cli.CloudPoolCommandLineDriver;
import com.elastisys.scale.commons.json.JsonUtils;
import com.google.gson.JsonObject;

/**
 * Lab program that exercises the AWS Auto Scaling Group {@link CloudPool} via
 * commands read from {@code stdin}.
 * <p/>
 * Note that an Auto Scaling group with the specified name (
 * {@value #autoScalingGroup}) must exist in the selected region (
 * {@value #region}) before running the program.The
 * {@code CreateLoadBalancerMain}, {@code CreateLaunchConfigurationMain} and
 * {@code CreateAutoScalingGroupMain} lab programs could get you started.
 *
 */
public class RunPool {
    static Logger logger = LoggerFactory.getLogger(RunPool.class);

    private static final Path configFile = Paths.get(System.getenv("HOME"), ".elastisys", "awsas", "config.json");

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StateStorage stateStorage = StateStorage.builder(new File("target/state")).build();
        CloudPool pool = new BaseCloudPool(stateStorage, new AwsAsPoolDriver(new AwsAutoScalingClient()), executor);

        JsonObject config = JsonUtils.parseJsonFile(configFile.toFile()).getAsJsonObject();
        pool.configure(config);

        new CloudPoolCommandLineDriver(pool).start();

        executor.shutdownNow();
    }
}
