# Copyright (c) 2024, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
# Author: OAG Development
FROM fnproject/fn-java-fdk:jre17-1.0.180
WORKDIR /function
COPY ./target/*.jar /function/app/
CMD ["com.oracle.idm.agcs.grc.fn.requestTemplate.RequestTemplateFunction::handleRequest"]
