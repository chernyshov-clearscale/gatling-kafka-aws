#!/bin/bash
#function help_text {
#    cat <<EOF
#    Usage: $0 [ -p|--profile PROFILE ] [ -r|--report-bucket REPORT_BUCKET ] [-h]
#
#
#        PROFILE         (optional) The profile to use from ~/.aws/credentials.
#        REPORT_BUCKET   (required) name of the S3 bucket to upload the reports to. Must be in same AWS account as profile.
#                                   It must be provided.
#EOF
#    exit 1
#}
#
#while [ $# -gt 0 ]; do
#    arg=$1
#    case $arg in
#        -h|--help)
#            help_text
#        ;;
#        -p|--profile)
#            export AWS_DEFAULT_PROFILE="$2"
#            shift; shift
#        ;;
#        -r|--report-bucket)
#            REPORT_BUCKET="$2"
#            shift; shift
#        ;;
#        *)
#            echo "ERROR: Unrecognised option: ${arg}"
#            help_text
#            exit 1
#        ;;
#    esac
#done
#
#if [ -z "$REPORT_BUCKET" ]
#    then
#        echo "Report bucket required. Please make sure its empty."
#        help_text
#        exit 1
#fi
#
### Clean reports
#rm -rf target/gatling/*

#echo $AWS_CONTAINER_CREDENTIALS_RELATIVE_URI
#CREDS_JSON=`curl 169.254.170.2$AWS_CONTAINER_CREDENTIALS_RELATIVE_URI`
#export AWS_ACCESS_KEY_ID=`echo $CREDS_JSON | jq -r .AccessKeyId`
#export AWS_SECRET_ACCESS_KEY=`echo $CREDS_JSON | jq -r .SecretAccessKey`
#export AWS_SESSION_TOKEN=`echo $CREDS_JSON | jq -r .Token`
#echo $AWS_ACCESS_KEY_ID
#echo $AWS_SECRET_ACCESS_KEY
#echo $AWS_SESSION_TOKEN

# Running performance test
# TODO -D<variable_name> could be used to pass parameters
mvn -X gatling:test -Dgatling.simulationClass=nl.codecontrol.gatling.simulations.UbxMessageKafkaSimulation -Drate="$1" -Dperiod="$2" -Dtopic="$3"

##Upload reports
#for _dir in target/gatling/*/
#do
#   aws s3 cp ${_dir}simulation.log s3://${REPORT_BUCKET}/logs/$HOSTNAME-simulation.log
#done
