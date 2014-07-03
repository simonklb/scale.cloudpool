package com.elastisys.scale.cloudadapters.commons.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elastisys.scale.cloudadapers.api.CloudAdapterException;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.AlertSettings;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.BootTimeLivenessCheck;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.LivenessConfig;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.MailServerSettings;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.RunTimeLivenessCheck;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.ScaleDownConfig;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.ScaleUpConfig;
import com.elastisys.scale.cloudadapters.commons.adapter.BaseCloudAdapterConfig.ScalingGroupConfig;
import com.elastisys.scale.cloudadapters.commons.scaledown.VictimSelectionPolicy;
import com.elastisys.scale.commons.json.JsonUtils;
import com.elastisys.scale.commons.net.smtp.ClientAuthentication;
import com.google.gson.JsonObject;

/**
 * Tests validation of {@link BaseCloudAdapterConfig}s.
 *
 * 
 *
 */
public class TestBaseCloudAdapterConfigValidation {

	@Test
	public void minimalConfig() throws CloudAdapterException {
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, null, null).validate();
	}

	@Test
	public void withAlertConfig() throws CloudAdapterException {
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	@Test
	public void withAlertConfigWithDefaultSeverityFilter()
			throws CloudAdapterException {
		// no explicity severity filter => default severity filter
		String defaultSeverity = null;
		AlertSettings alertSettings = new AlertSettings("subject",
				Arrays.asList("recipient@dest.com"), "sender@source.com",
				defaultSeverity, mailServer());

		BaseCloudAdapterConfig config = new BaseCloudAdapterConfig(
				scalingGroupConfig(), scaleUpConfig(), scaleDownConfig(), null,
				alertSettings, null);
		assertThat(config.getAlerts().getSeverityFilter(),
				is(AlertSettings.DEFAULT_SEVERITY_FILTER));
		config.validate();
	}

	// illegal config: invalid severity filter
	@Test(expected = CloudAdapterException.class)
	public void withIllegalAlertsSeverityFilter() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts, "severityFilter", "**");
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /scalingGroup
	@Test(expected = CloudAdapterException.class)
	public void missingScalingGroup() throws CloudAdapterException {
		new BaseCloudAdapterConfig(null, scaleUpConfig(), scaleDownConfig(),
				null, alertConfig(), null).validate();
	}

	// illegal config: missing /scalingGroup/name
	@Test(expected = CloudAdapterException.class)
	public void missingScalingGroupName() throws CloudAdapterException {
		ScalingGroupConfig scalingGroupConfig = scalingGroupConfig();
		setPrivateField(scalingGroupConfig, "name", null);
		new BaseCloudAdapterConfig(scalingGroupConfig, scaleUpConfig(),
				scaleDownConfig(), null, null, null).validate();
	}

	// illegal config: missing /scalingGroup/config
	@Test(expected = CloudAdapterException.class)
	public void missingApplicationLaunchConfig() throws CloudAdapterException {
		ScalingGroupConfig scalingGroupConfig = scalingGroupConfig();
		setPrivateField(scalingGroupConfig, "config", null);
		new BaseCloudAdapterConfig(scalingGroupConfig, scaleUpConfig(),
				scaleDownConfig(), null, null, null).validate();
	}

	// illegal config: missing /scaleUpConfig
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfig() throws CloudAdapterException {
		new BaseCloudAdapterConfig(scalingGroupConfig(), null,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /scaleUpConfig/size
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfigSize() throws CloudAdapterException {
		ScaleUpConfig scaleUpConfig = scaleUpConfig();
		setPrivateField(scaleUpConfig, "size", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /scaleUpConfig/image
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfigImage() throws CloudAdapterException {
		ScaleUpConfig scaleUpConfig = scaleUpConfig();
		setPrivateField(scaleUpConfig, "image", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /scaleUpConfig/keyPair
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfigKeyPair() throws CloudAdapterException {
		ScaleUpConfig scaleUpConfig = scaleUpConfig();
		setPrivateField(scaleUpConfig, "keyPair", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /scaleUpConfig/securityGroups
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfigSecurityGroups()
			throws CloudAdapterException {
		ScaleUpConfig scaleUpConfig = scaleUpConfig();
		setPrivateField(scaleUpConfig, "securityGroups", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /scaleUpConfig/bootScript
	@Test(expected = CloudAdapterException.class)
	public void missingScaleUpConfigBootScript() throws CloudAdapterException {
		ScaleUpConfig scaleUpConfig = scaleUpConfig();
		setPrivateField(scaleUpConfig, "bootScript", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig,
				scaleDownConfig(), null, alertConfig(), null).validate();
	}

	// illegal config: missing /liveness/loginUser
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessLoginUser() throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness, "loginUser", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/loginKey
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessLoginKey() throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness, "loginKey", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/bootTimeCheck
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessBootTimeCheck() throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness, "bootTimeCheck", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/bootTimeCheck/command
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessBootTimeCheckCommand()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getBootTimeCheck(), "command", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/bootTimeCheck/retryDelay
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessBootTimeCheckRetryDelay()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getBootTimeCheck(), "retryDelay", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/bootTimeCheck/maxRetries
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessBootTimeCheckMaxRetries()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getBootTimeCheck(), "maxRetries", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/runTimeCheck
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessRunTimeCheck() throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness, "runTimeCheck", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/runTimeCheck/command
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessRunTimeCheckCommand()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getRunTimeCheck(), "command", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/runTimeCheck/period
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessRunTimeCheckPeriod()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getRunTimeCheck(), "period", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/runTimeCheck/maxRetries
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessRunTimeCheckMaxRetries()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getRunTimeCheck(), "maxRetries", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /liveness/runTimeCheck/retryDelay
	@Test(expected = CloudAdapterException.class)
	public void missingLivenessRunTimeCheckRetryDelay()
			throws CloudAdapterException {
		LivenessConfig liveness = liveness();
		setPrivateField(liveness.getRunTimeCheck(), "retryDelay", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), liveness, null, null).validate();
	}

	// illegal config: missing /alerts/subject
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsSubject() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts, "subject", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/recipients
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsRecipients() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts, "recipients", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/sender
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsSender() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts, "sender", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/mailServer
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsMailServer() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts, "mailServer", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/mailServer/smtpHost
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsMailServerSmtpHost() throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		setPrivateField(alerts.getMailServer(), "smtpHost", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/mailServer/authentication/userName
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsMailServerAuthenticationUsername()
			throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		ClientAuthentication authentication = alerts.getMailServer()
				.getAuthentication();
		setPrivateField(authentication, "userName", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	// illegal config: missing /alerts/mailServer/authentication/password
	@Test(expected = CloudAdapterException.class)
	public void missingAlertsMailServerAuthenticationPassword()
			throws CloudAdapterException {
		AlertSettings alerts = alertConfig();
		ClientAuthentication authentication = alerts.getMailServer()
				.getAuthentication();
		setPrivateField(authentication, "password", null);
		new BaseCloudAdapterConfig(scalingGroupConfig(), scaleUpConfig(),
				scaleDownConfig(), null, alerts, null).validate();
	}

	private AlertSettings alertConfig() {
		return new AlertSettings("subject",
				Arrays.asList("recipient@dest.com"), "sender@source.com",
				"ERROR|FATAL", mailServer());
	}

	private MailServerSettings mailServer() {
		return new MailServerSettings("smtpHost", 587, smtpAuth(), true);
	}

	private ClientAuthentication smtpAuth() {
		return new ClientAuthentication("userName", "password");
	}

	private LivenessConfig liveness() {
		return new LivenessConfig(22, "loginUser",
				"src/test/resources/security/clientkey.pem", bootTimeCheck(),
				runTimeCheck());
	}

	private RunTimeLivenessCheck runTimeCheck() {
		return new RunTimeLivenessCheck(
				"service apache2 status | grep running", 60, 10, 30);
	}

	private BootTimeLivenessCheck bootTimeCheck() {
		return new BootTimeLivenessCheck(
				"service apache2 status | grep running", 30, 20);
	}

	private ScalingGroupConfig scalingGroupConfig() {
		return new ScalingGroupConfig("MyScalingGroup",
				cloudCredentialsConfig());
	}

	private ScaleUpConfig scaleUpConfig() {
		List<String> bootScript = Arrays.asList("#!/bin/bash",
				"apt-get update -qy && apt-get isntall apache2 -qy");
		return new ScaleUpConfig("size", "image", "keyPair",
				Arrays.asList("securityGroup"), bootScript);
	}

	private ScaleDownConfig scaleDownConfig() {
		return new ScaleDownConfig(
				VictimSelectionPolicy.CLOSEST_TO_INSTANCE_HOUR, 300);
	}

	private JsonObject cloudCredentialsConfig() {
		return JsonUtils.parseJsonString("{\"userName\": \"johndoe\", "
				+ "\"region\": \"us-east-1\"}");
	}

	/**
	 * Sets a private field on an instance object.
	 *
	 * @param object
	 *            The object.
	 * @param privateFieldName
	 *            The name of the private field to set.
	 * @param valueToSet
	 *            The value to set.
	 * @throws Exception
	 */
	private void setPrivateField(Object object, String privateFieldName,
			Object valueToSet) throws RuntimeException {
		try {
			Field field = object.getClass().getDeclaredField(privateFieldName);
			field.setAccessible(true);
			field.set(object, valueToSet);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"could not set private field '%s' on object: %s",
					privateFieldName, e.getMessage()));
		}
	}
}