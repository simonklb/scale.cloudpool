package com.elastisys.scale.cloudpool.aws.commons.requests.ec2;

import java.util.concurrent.Callable;

import jersey.repackaged.com.google.common.collect.Iterables;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;

/**
 * A {@link Callable} task that, when executed, requests meta data for a
 * particular AWS EC2 {@link SpotInstanceRequest} in a region.
 */
public class GetSpotInstanceRequest extends
		AmazonEc2Request<SpotInstanceRequest> {

	/** The identifier of the requested {@link SpotInstanceRequest}. */
	private final String spotInstanceRequestId;

	/**
	 * Constructs a new {@link GetSpotInstanceRequest} task that will fetch a
	 * particular {@link SpotInstanceRequest} in the region that match any of
	 * the specified filters.
	 *
	 * @param awsCredentials
	 *            The AWS security credentials to the account.
	 * @param region
	 *            The AWS region of interest.
	 * @param spotInstanceRequestId
	 *            The identifier of the requested {@link SpotInstanceRequest}.
	 */
	public GetSpotInstanceRequest(AWSCredentials awsCredentials, String region,
			String spotInstanceRequestId) {
		super(awsCredentials, region);
		this.spotInstanceRequestId = spotInstanceRequestId;
	}

	@Override
	public SpotInstanceRequest call() throws AmazonClientException {
		DescribeSpotInstanceRequestsRequest request = new DescribeSpotInstanceRequestsRequest();
		request.withSpotInstanceRequestIds(this.spotInstanceRequestId);
		DescribeSpotInstanceRequestsResult result = getClient().getApi()
				.describeSpotInstanceRequests(request);
		return Iterables.getOnlyElement(result.getSpotInstanceRequests());
	}
}