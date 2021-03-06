#!/bin/bash

DOCKER_NETWORK=java_tests_network_$$
AMQP_HOST=amqp-host
AMQP_CONTAINER_NAME=amqp-host-$$
JAVA_CONTAINER_NAME=java-tests-host-$$
JAVA_HOST=java-tests-host
TMP_DIR=
SUDO=
MRG_VERSIONS="3.2.0 3.0.0 0.34 0.36"
MVN_IMAGE_VERSIONS="3-jdk-7 3-jdk-8"
RESULTS_MSG="RESULTS:\n"

trap "stop_and_remove_left_containers && exit 1" SIGINT SIGTERM

function stop_and_remove_left_containers() {
    for container in $(${SUDO} docker ps -a --filter "status=running" --format "{{.Names}}") ; do
        if [ ${container} == ${AMQP_CONTAINER_NAME} ] || [ ${container} == ${JAVA_CONTAINER_NAME} ] ; then
            echo "Stopping container: ${container}"
            ${SUDO} docker stop ${container}
        fi
    done
    for container in $(${SUDO} docker ps -a --filter "status=exited" --format "{{.Names}}") ; do
        if [ ${container} == ${AMQP_CONTAINER_NAME} ] || [ ${container} == ${JAVA_CONTAINER_NAME} ] ; then
            echo "Removing container: ${container}"
            ${SUDO} docker rm ${container}
        fi
    done
    ${SUDO} docker network rm ${DOCKER_NETWORK}
}

function print_help() {
    local MY_NAME="$(basename $0 .sh)"
    echo "Usage: ${MY_NAME}.sh [OPTION]..."
    echo ""
    echo " optional"
    echo ""
    echo "  --mrg-version=VERSION    Use specific MRG version (e.g. 3.2.0)"
    echo "  --mvn-version=VERSION    Use specific Maven Docker image version (e.g. 3-jdk-8)"
    echo "  --use-sudo               Execute every docker command under sudo"
    echo "  --help, -h, -?           Print this help and exit"
}

function extract_parameter_value_from_string {
    echo "${1#*=}"
    return 0
}

function parse_cmdline_parameters() {
    for i in "$@" ; do
        case $i in
        --mrg-version=*)
            MRG_VERSIONS=$(extract_parameter_value_from_string $1);;
        --mvn-version=*)
            MVN_IMAGE_VERSIONS=$(extract_parameter_value_from_string $1);;
        --use-sudo)
            SUDO="sudo";;
        --help | -h | -?)
            print_help; exit 0;;
        "");;
        *)
            echo "Unknown parameter '$i'";
            exit 2;;
        esac
        shift
    done
}

function startup() {
    TMP_DIR=$(mktemp -d)
}

function create_network() {
    ${SUDO} docker network create --subnet=192.168.0.0/16 --driver bridge ${DOCKER_NETWORK}
}

# param: $1 - image version
function start_qpidd_container() {
    ${SUDO} docker run -d --net=${DOCKER_NETWORK} --name=${AMQP_CONTAINER_NAME} --hostname=${AMQP_HOST} scholzj/java-client-tests:$1
    RESULTS_MSG+="MRG: $1, "
}

# param: $1 - image version
function start_tests_container() {
    local AMQP_CONTAINER_NAME_IP_ADDRESS=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME})
    ${SUDO} docker run -d -it --dns=8.8.8.8 --dns=8.8.4.4 --net=${DOCKER_NETWORK} --add-host ${AMQP_HOST}:${AMQP_CONTAINER_NAME_IP_ADDRESS} --name=${JAVA_CONTAINER_NAME} --hostname=${JAVA_HOST} maven:$1 bash
    RESULTS_MSG+="MAVEN IMAGE: $1, "
}

# get source code into the docker container
function prepare_sources_on_container() {
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "cd && git clone https://github.com/scholzj/amqp-java-client-tests.git java-client-tests"
}

# copy broker's truststore from one container into another
function prepare_truststore_on_container() {
    ${SUDO} docker cp ${AMQP_CONTAINER_NAME}:/var/lib/qpidd/ssl/truststore ${TMP_DIR}/truststore.$$
    ${SUDO} docker cp ${TMP_DIR}/truststore.$$ ${JAVA_CONTAINER_NAME}:/root/java-client-tests/configuration/src/main/resources/${AMQP_HOST}.truststore
}

# modify configuration file used by tests
function prepare_configuration_for_tests() {
    local AMQP_CONTAINER_NAME_IP_ADDRESS=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME})

    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "sed -i \"s/^broker.hostname = .*$/broker.hostname = ${AMQP_HOST}/g\" /root/java-client-tests/configuration/src/main/resources/settings.properties"
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "sed -i \"s/^broker.ip_address = .*$/broker.ip_address = ${AMQP_CONTAINER_NAME_IP_ADDRESS}/g\" /root/java-client-tests/configuration/src/main/resources/settings.properties"
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "sed -i \"s/^broker.tcp_port = .*$/broker.tcp_port = 5672/g\" /root/java-client-tests/configuration/src/main/resources/settings.properties"
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "sed -i \"s/^broker.ssl_port = .*$/broker.ssl_port = 5671/g\" /root/java-client-tests/configuration/src/main/resources/settings.properties"
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "sed -i \"s/^broker.truststore = .*$/broker.truststore = ${AMQP_HOST}.truststore/g\" /root/java-client-tests/configuration/src/main/resources/settings.properties"
}

# param: $1 - maven pom xml name
function execute_tests() {
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "cd && cd java-client-tests && mvn test -B -Dsurefire.suiteXmlFiles=/root/java-client-tests/$1"
    local RETURN_CODE=$?
    RESULTS_MSG+="POM.XML: $1," 
    if [ ${RETURN_CODE} -eq 0 ] ; then
        RESULTS_MSG+=" RESULT: SUCCESS\n"
    else
        RESULTS_MSG+=" RESULT: FAILURE\n"
    fi
}

function cleanup() {
    rm -rf ${TMP_DIR}
    ${SUDO} docker stop ${AMQP_CONTAINER_NAME}
    ${SUDO} docker rm ${AMQP_CONTAINER_NAME}
    ${SUDO} docker stop ${JAVA_CONTAINER_NAME}
    ${SUDO} docker rm ${JAVA_CONTAINER_NAME}
    ${SUDO} docker network rm ${DOCKER_NETWORK}
}

# params: $1 - QPID container id, $2 - Maven container id, $3 - name of maven pom.xml on test container
function execute_single_run() {
    local QPIDD_CONTAINER_VERSION=$1
    local MAVEN_CONTAINER_VERSION=$2
    local TESTS_POM_XML_NAME=$3
    startup
    create_network && \
    start_qpidd_container ${QPIDD_CONTAINER_VERSION} && \
    start_tests_container ${MAVEN_CONTAINER_VERSION} && \
    prepare_sources_on_container && \
    prepare_truststore_on_container && \
    prepare_configuration_for_tests && \
    execute_tests ${TESTS_POM_XML_NAME}
    cleanup
}

function execute_all_runs() {
    for mvn_version in ${MVN_IMAGE_VERSIONS} ; do
        for mrg_version in ${MRG_VERSIONS} ; do
            case ${mrg_version} in
              3.*)  execute_single_run ${mrg_version} ${mvn_version} mrg-${mrg_version}.xml ;;
              *) execute_single_run ${mrg_version} ${mvn_version} qpid-${mrg_version}.xml ;;
            esac
        done
    done
}

function print_results() {
    echo -e ${RESULTS_MSG}
}

parse_cmdline_parameters "$@"
execute_all_runs
print_results

