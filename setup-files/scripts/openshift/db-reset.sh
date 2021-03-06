#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ ${TARGET} == "production" ]]; then PROJECT="production"; fi
if [[ ${TARGET} == "demo" ]]; then PROJECT="demo"; fi
if [[ ${TARGET} == "uat" ]]; then PROJECT="uat"; fi
if [[ ${TARGET} == "sysint" ]]; then PROJECT="sysint"; fi
if [[ ${TARGET} == "perf" ]]; then PROJECT="perf"; fi

if [[ (${TARGET} == "local") ]]; then HOST=ifs-local; else HOST=prod.ifs-test-clusters.com; fi

if [ -z "$bamboo_openshift_svc_account_token" ]; then  SVC_ACCOUNT_TOKEN=$(oc whoami -t); else SVC_ACCOUNT_TOKEN=${bamboo_openshift_svc_account_token}; fi

SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"
REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN};

ROUTE_DOMAIN=apps.$HOST
REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

echo "Resetting the $PROJECT Openshift project"

function dbReset() {
    until oc create -f os-files-tmp/db-reset/66-dbreset.yml ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete -f os-files-tmp/db-reset/66-dbreset.yml ${SVC_ACCOUNT_CLAUSE}
      sleep 10
    done

    oc rsh ${SVC_ACCOUNT_CLAUSE} $(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep -m 1 data-service | awk '{ print $1 }') /bin/bash -c 'cd /mnt/ifs_storage && ls | grep -v .trashcan | xargs rm -rf'
}

. $(dirname $0)/deploy-functions.sh

# Entry point
cleanUp
cloneConfig
tailorAppInstance
injectDBVariables
injectLDAPVariables
injectFlywayVariables

if [[ (${TARGET} != "local") ]]
then
    useContainerRegistry
    pushDBResetImages
fi

dbReset

echo Waiting for container to start
until [ "$(oc get po dbreset ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ] && [ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Running' ]
do
  echo -n .
  sleep 5
done

oc logs -f dbreset ${SVC_ACCOUNT_CLAUSE}

echo Waiting for container to terminate before checking its status
sleep 5

if [[ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" != "Succeeded" ]]; then exit -1; fi

exit 0
