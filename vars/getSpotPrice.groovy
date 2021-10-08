#!/usr/bin/env groovy

/**
 * Query AWS spot instance price via AWS CLI
 */
def call(Map ec2_config) {
    def ec2_region = ec2_config.region ?: 'us-west-1'
    def ec2_az = ec2_config.az ?: 'us-west-1b'
    def ec2_type = ec2_config.type ?: 't3.medium'
    def ec2_os = ec2_config.os ?: 'Windows (Amazon VPC)'

    echo "checking Spot instance price for ${ec2_region} region, ${ec2_az} avavailability-zone, ${ec2_type} machine type, with ${ec2_os} OS"

    def spot_price = sh(script: "aws ec2 describe-spot-price-history \
    --region=${ec2_region} \
    --availability-zone=${ec2_az} \
    --instance-types=${ec2_type} \
    --start-time=\$(date +%s) \
    --product-descriptions='${ec2_os}' \
    --query SpotPriceHistory[0].SpotPrice \
    --output text", returnStdout: true).toString().trim()

    echo "The spot price is: \$${spot_price}"
    return spot_price
}
