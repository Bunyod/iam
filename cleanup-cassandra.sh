#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

LAGOM_CASSANDRA="target/lagom-dynamic-projects/lagom-internal-meta-project-cassandra"
EMBEDDED_CASSANDRA="target/embedded-cassandra"

rm -rf ${DIR}/${LAGOM_CASSANDRA}
rm -rf ${DIR}/${EMBEDDED_CASSANDRA}
