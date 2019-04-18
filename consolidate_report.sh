#!/bin/bash
function help_text {
    cat <<EOF
    Usage: $0 [ -p|--profile PROFILE ] [ -r|--report-bucket REPORT_BUCKET ] [-h]
        
        PROFILE         (optional) The profile to use from ~/.aws/credentials.
        REPORT_BUCKET   (required) name of the S3 bucket to upload the reports to. Must be in same AWS account as profile.
                                   It must be provided.
EOF
    exit 1
}

while [ $# -gt 0 ]; do
    arg=$1
    case $arg in
        -h|--help)
            help_text
        ;;
        -p|--profile)
            export AWS_DEFAULT_PROFILE="$2"
            shift; shift
        ;;
        -r|--report-bucket)
            REPORT_BUCKET="$2"
            shift; shift
        ;;
        *)
            echo "ERROR: Unrecognised option: ${arg}"
            help_text
            exit 1
        ;;
    esac
done

if [ -z "$REPORT_BUCKET" ]
then
    echo "Report bucket required."
    help_text
    exit 1
fi

FOLDER="p1simulation-$(date +"%Y-%m-%d_%H:%M:%S")"

rm -r target/gatling/*
## Download all logs for all test gatling clients
aws s3 cp s3://${REPORT_BUCKET}/logs/ target/gatling/results/$FOLDER --recursive

## Consolidate reports from these clients
./gatling/gatling-charts-highcharts-bundle-3.0.3/bin/gatling.sh -ro $(pwd)/target/gatling/results/$FOLDER

## Upload consolidated gatling html reports back to s3
aws s3 cp target/gatling/results/ s3://${REPORT_BUCKET}/gatling/ --recursive

echo https://s3-eu-west-1.amazonaws.com/richard-performance-testing/gatling/$FOLDER/index.html